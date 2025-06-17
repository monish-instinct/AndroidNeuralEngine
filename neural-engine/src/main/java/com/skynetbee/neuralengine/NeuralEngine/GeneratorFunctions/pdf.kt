package com.skynetbee.neuralengine

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.io.File
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.properties.UnitValue
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


fun generatePdf(context: Context, id: String, query: String? = null, paragraphText: String? = null,fileName: String? = null) {
    try {
        val pdfDirectory = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        if (pdfDirectory != null && !pdfDirectory.exists()) pdfDirectory.mkdirs()

        val fileName = if (fileName != null && fileName.endsWith(".pdf")) {
            fileName
        } else {
            "${fileName ?: "skynet_data"}.pdf" // ✅ Ensure .csv extension
        }
        val pdfFile = File(pdfDirectory, fileName)

        val pdfWriter = PdfWriter(pdfFile)
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument, PageSize.A4)
        document.setMargins(30f, 30f, 30f, 30f)

        // Add paragraph if provided
        if (!paragraphText.isNullOrEmpty()) {
            val paragraph = createParagraph(paragraphText)
            val mm = document.add(paragraph)
            Log.d("xcv", "generatePdf: ${mm}")
        }

        // ✅ Run Coroutine to Fetch Table Data
        runBlocking {
            if (!query.isNullOrEmpty()) {
                Log.d("qwe", "generatePdf: $query")
                val table = createTableFromDatabase(query)  // ✅ Called inside coroutine
                Log.d("xcv", "dfg:${table.header}")
                document.add(table)
            }
        }

        document.close()
        Log.d("PDF", "PDF saved at: ${pdfFile.absolutePath}")

        val uri: Uri = FileProvider.getUriForFile(
            context, "${context.packageName}.provider", pdfFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(intent, "Open PDF with"))

    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("PDF", "Error generating/opening PDF: ${e.message}")
    }
}


fun createParagraph(text: String): Paragraph {
    return Paragraph(text).setFontSize(12f).setBold()
}


suspend fun createTableFromDatabase(query: String): Table {
    val tableData: MutableList<Map<String, String>> = mutableListOf()

    while (true) {
        val data = sql.select(query)
        if (data == null)
            break
        tableData.add(data)
    }
    Log.d("xcv", "createTableFromDatabase:$tableData ")
    val resultData: List<Map<String, String>> = tableData
    val columnNames = resultData.firstOrNull()?.keys?.toList() ?: emptyList()
    val columnWidths = FloatArray(columnNames.size) { 2f }
    val table = Table(columnWidths).setWidth(UnitValue.createPercentValue(100f))

    // 🔹 Format Headers
    columnNames.forEach { columnName ->
        val formattedHeader = columnName
            .replace("_", " ") // Replace underscores with spaces
            .replace("unique member id", "ID", ignoreCase = true) // Replace specific column name

        table.addCell(Cell().add(Paragraph(formattedHeader).setBold().setFontSize(18f)))
    }
    Log.d("xcv", "RESULT:$resultData ")

    // 🔹 Loop through all rows and add data
    for (row in resultData) {
        for (column in columnNames) {
            val cellData = row[column] ?: "N/A"

            if (column.lowercase().contains("image") && cellData.startsWith("http")) {

                val image = loadImageFromUrl(cellData)
                if (image != null) {
                    table.addCell(Cell().add(image.setAutoScale(true))) // ✅ Add Image
                } else {
                    table.addCell(Paragraph("❌ Image Not Found").setFontSize(16f))
                }
            } else {
                Log.d("xcvn", "Co:$cellData")
                table.addCell(Paragraph(cellData).setFontSize(16f))
            }
        }
    }
    Log.d("xcvs", "createTableFromDatabase:${table.header}--${table.footer} ")
    return table
}

suspend fun loadImageFromUrl(imageUrl: String): Image? {
    return withContext(Dispatchers.IO) {
        try {
            Log.d("PDF", "📥 Downloading image from: $imageUrl")

            var url = URL(imageUrl)
            var connection = url.openConnection() as HttpURLConnection
            var redirectCount = 0

            while (true) {
                connection.apply {
                    requestMethod = "GET"
                    instanceFollowRedirects = false // Prevent auto-redirects
                    setRequestProperty("User-Agent", "Mozilla/5.0")
                    setRequestProperty("Accept", "image/*")
                    doInput = true
                }

                Log.d("PDF", "🔄 Connecting to server...")
                connection.connect() // ✅ Connect AFTER setting properties

                val responseCode = connection.responseCode
                Log.d("PDF", "📡 Response Code: $responseCode")

                // ✅ Handle Redirects Properly (Max 5 redirects to prevent infinite loop)
                if (responseCode in 300..399 && redirectCount < 5) {
                    val newUrl = connection.getHeaderField("Location")
                    if (!newUrl.isNullOrEmpty()) {
                        Log.d("PDF", "🔄 Redirecting to: $newUrl")
                        url = URL(newUrl)
                        connection = url.openConnection() as HttpURLConnection
                        redirectCount++
                        continue
                    }
                }
                break // Exit loop if not redirected
            }

            // ✅ Validate Content-Type
            val contentType = connection.contentType
            if (contentType == null || !contentType.startsWith("image/")) {
                Log.e("PDF", "❌ Invalid content type: $contentType (Not an image)")
                return@withContext null
            }

            val inputStream: InputStream = connection.inputStream
            Log.d("PDF", "✅ Input stream opened")

            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            Log.d("PDF", "🎉 Image downloaded successfully!")

            val imageData = ImageDataFactory.create(outputStream.toByteArray())
            Log.d("PDF", "✅ Image data created")

            Image(imageData).setWidth(60f).setHeight(60f)
        } catch (e: Exception) {
            Log.e("PDF", "🚨 Error downloading image: ${e.message}")
            null
        }
    }
}


























