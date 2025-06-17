package skynetbee.developers.DevEnvironment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.Q)
fun generateCsv(context: Context, query: String, fileName: String? = null) {
    try {
        // ✅ Ensure filename has ".csv" extension
        val finalFileName = if (!fileName.isNullOrBlank() && fileName.endsWith(".csv")) {
            fileName
        } else {
            "${fileName ?: "skynet_data"}.csv" // ✅ Use default if filename is empty
        }

        // ✅ Save CSV file in Downloads using MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, finalFileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                // **Fetch Data from Database**
                val data = fetchDataFromDatabase(query) // ✅ Returns List<Map<String, String>>

                if (data.isEmpty()) {
                    Log.e("CSV", "🚨 No data found for query: $query")
                    return
                }

                // **Write Headers**
                val headers = data.first().keys.toList()
                outputStream.write((headers.joinToString(",") + "\n").toByteArray())

                // **Write Data Rows**
                data.forEach { row ->
                    val rowValues = headers.map { row[it] ?: "N/A" }
                    outputStream.write((rowValues.joinToString(",") + "\n").toByteArray())
                }

                outputStream.flush()
            }

            Log.d("CSV", "✅ CSV file saved at: $uri")
            openCsv(context, uri) // ✅ Open CSV immediately
        } ?: run {
            Log.e("CSV", "🚨 Failed to create CSV file")
        }

    } catch (e: Exception) {
        Log.e("CSV", "🚨 Error generating CSV: ${e.message}")
    }
}

@SuppressLint("QueryPermissionsNeeded")
fun openCsv(context: Context, uri: Uri) {
    val excelPackage = "com.microsoft.office.excel"

    // ✅ Check if Microsoft Excel is installed
    if (isAppInstalled(context, excelPackage)) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv") // ✅ Ensures it opens as CSV
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            setPackage(excelPackage) // ✅ Open in Microsoft Excel
        }
        context.startActivity(intent)
    } else {
        // ✅ If Excel is NOT installed, open in Google Sheets or browser
        val webIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "text/csv")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(Intent.createChooser(webIntent, "Open CSV with"))
    }
}

// ✅ Function to check if an app is installed
fun isAppInstalled(context: Context, packageName: String): Boolean {
    return try {
        context.packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

fun fetchDataFromDatabase(query: String): List<Map<String, String>> {
    val resultList = mutableListOf<Map<String, String>>() // ✅ Stores all rows
    Log.d("DB123", "✅ Executing Query: $query")

    try {
        val result = sql.executeQuery(query) // ✅ Use `executeQuery()` to fetch data
        val success = result.first
        val data = result.second

        Log.d("DB123", "✅ Query Success: $success, Data Fetched: $data")

        if (success && data != null) {
            resultList.addAll(data)
        } else {
            Log.e("DB123", "🚨 No data found or query failed!")
        }

    } catch (e: Exception) {
        Log.e("DB123", "🚨 Error fetching data: ${e.message}")
    }

    Log.d("DB123", "✅ Final Data (All Rows): $resultList") // ✅ Debug log
    return resultList
}
