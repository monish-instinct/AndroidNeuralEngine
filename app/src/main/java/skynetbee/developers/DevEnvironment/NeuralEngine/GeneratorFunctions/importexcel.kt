package skynetbee.developers.DevEnvironment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellUtil
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun ImportCSV(
    tableName: String,
    columnNames: List<String>,
    ignoreDuplicates: Boolean? = true
) {
    val context = LocalContext.current
    var storedData by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var insertQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    // 🔹 Border Gradient (Gold)
    val borderGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFFD700), Color(0xFFC0A060), Color(0xFFFFC107)) // Gold gradient border
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .backgroundCard()
            .height(300.dp)
            .width(350.dp), // Padding for the whole column
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🔹 First Button (Import From Excel)
        Button(
            onClick = {
                isLoading = true
                isSuccess = false

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val inputStream = context.resources.openRawResource(R.raw.excel)
                        val fileType = "spreadsheet" // Assume Excel for now

                        storedData = if (fileType == "spreadsheet") {
                            parseExcel(inputStream)
                        } else {
                            parseCSV(inputStream)
                        }

                        withContext(Dispatchers.Main) {
                            if (storedData.isNotEmpty()) {
                                val extractedColumns = storedData.first().keys.toList()
                                val validColumns = columnNames.filter { extractedColumns.contains(it) }

                                val valuesList = storedData.joinToString(",\n") { row ->
                                    "(" + validColumns.joinToString(", ") { column ->
                                        val value = row[column] ?: "NULL"
                                        if (value.isBlank()) "NULL" else "'$value'"
                                    } + ")"
                                }

                                // Create the Insert Query
                                insertQuery = if (ignoreDuplicates == true) {
                                    """
                                    INSERT INTO $tableName (${validColumns.joinToString(", ") { "`$it`" }}) 
                                    VALUES 
                                    $valuesList;
                                    """.trimIndent()
                                } else {
                                    """
                                    INSERT INTO $tableName (${validColumns.joinToString(", ") { "`$it`" }}) 
                                    VALUES 
                                    $valuesList
                                    ON DUPLICATE KEY UPDATE 
                                    ${validColumns.joinToString(", ") { "`$it` = VALUES(`$it`)" }};
                                    """.trimIndent()
                                }

                                Log.d("FileImport", "Generated Query:\n$insertQuery")
                                isSuccess = true
                                Toast.makeText(context, "✅ Import Successful!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "⚠ No data found!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("FileImport", "Error: ${e.message}")
                            Toast.makeText(context, "❌ Import Failed!", Toast.LENGTH_SHORT).show()
                        }
                    } finally {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                    }
                }
            },
            modifier = Modifier
                .width(300.dp)
                .height(50.dp)
                .border(BorderStroke(2.dp, borderGradient), shape = RoundedCornerShape(12.dp)) // ✅ Gold Border
                .background(Color.Transparent, shape = RoundedCornerShape(12.dp)), // ✅ Transparent Background
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp), // Remove default padding
            enabled = !isLoading // ✅ Disable while loading
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Importing...", color = Color.White)
                } else {
                    if (!isSuccess) { // ✅ Hide icon after success
                        Icon(
                            imageVector = Icons.Default.FileDownload,
                            contentDescription = "Import Excel",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        if (isSuccess) "✅ Imported Successfully!" else "Import From Excel",
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp)) // Spacing between buttons

        // 🔹 Second Button (View as PDF)
        Button(
            onClick = {
                // Always generate PDF when the button is clicked
                if (insertQuery.isNotBlank()) {
                    generatePdf(
                        context = context,
                        id = "pdf_report",
                        query = "name, age, favcolur, gender FROM import_from_excel",
                        fileName = "imported_data.pdf"
                    )
                } else {
                    Toast.makeText(context, "No data to generate PDF!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .width(300.dp)
                .height(50.dp)
                .border(BorderStroke(2.dp, borderGradient), shape = RoundedCornerShape(12.dp)) // ✅ Gold Border
                .background(Color.Transparent, shape = RoundedCornerShape(12.dp)), // ✅ Transparent Background
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(0.dp) // Remove default padding
        ) {
            Text(
                text = "View as PDF",
                fontWeight = FontWeight.Bold,
                color = Color.White // ✅ White Text
            )
        }
    }
}



fun parseCSV(inputStream: java.io.InputStream): List<Map<String, String>> {
    val reader = BufferedReader(InputStreamReader(inputStream))
    val lines = reader.readLines()

    if (lines.isEmpty()) return emptyList()

    // 🔹 Preserve numeric headers as they are in the file
    val headers = lines.first().split(",").map { it.trim() }.map { header ->
        header.toDoubleOrNull()?.let { num ->
            if (num % 1 == 0.0) num.toInt().toString() // Convert 1.0 -> "1"
            else num.toString() // Keep 3.5 as "3.5"
        } ?: header // Keep text headers unchanged
    }

    Log.d("FileImport", "Extracted Headers: $headers")

    return lines.drop(1).map { row ->
        val values = row.split(",").map { it.trim() }

        val rowMap = mutableMapOf<String, String>()
        for (i in headers.indices) {
            rowMap[headers[i]] = if (i < values.size && values[i].isNotBlank()) values[i] else "NULL"
        }

        Log.d("FileImport", "Row Mapped Correctly: $rowMap")
        rowMap
    }
}

fun parseExcel(inputStream: InputStream): List<Map<String, String>> {
    val workbook = WorkbookFactory.create(inputStream)
    val sheet = workbook.getSheetAt(0)

    if (sheet.physicalNumberOfRows == 0) return emptyList() // No data

    val firstRow = sheet.getRow(0) ?: return emptyList()

    val headers = firstRow.map { cell ->
        when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue.trim()
            CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
            else -> "Unknown"
        }
    }

    Log.d("FileImport", "Extracted Headers: $headers")

    return sheet.drop(1).map { row ->
        val rowMap = mutableMapOf<String, String>()
        for (colIndex in headers.indices) {
            val cell = row.getCell(colIndex)
            val cellValue = when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> cell.numericCellValue.toInt().toString()
                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                else -> "NULL"
            }
            rowMap[headers[colIndex]] = cellValue
        }

        Log.d("FileImport", "Row Mapped Correctly: $rowMap")
        rowMap
    }.also {
        workbook.close()
    }
}

