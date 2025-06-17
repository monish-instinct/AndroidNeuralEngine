package com.skynetbee.neuralengine

import android.annotation.SuppressLint
import net.sqlcipher.database.SQLiteDatabase


import android.util.Log
import java.io.File
import java.time.LocalDate

class DevOpsDatabaseConnectionEstablisher {
    var db: SQLiteDatabase? = null

    init {
        System.loadLibrary("sqlcipher")
        initializeDatabase()
    }
    val dosqt = mutableListOf<SqlTracker>()
    fun select(qry: String, file: String = "#file", line: Int = -1): Map<String, String>? {
        val query = "SELECT $qry"

        val fileName = file.substringAfterLast("/")
        val lineNumber = " @ $line"
        val track = fileName + lineNumber

        for (sqlTracer in dosqt) {
            if (sqlTracer.tracker == track) {
                if (sqlTracer.query == query) {
                    return if (sqlTracer.tableData.indices.contains(sqlTracer.nextRow)) {
                        sqlTracer.tableData[sqlTracer.nextRow++]
                    } else {
                        null
                    }
                } else {
                    println("SQL Error thrown by Sqlize.select : tracker : $track reused for ${sqlTracer.query} and $query")
                    return null
                }
            }
        }

        val eR = executeQuery(query)
        return if (eR.first) {
            eR.second?.let {
                dosqt.add(0, SqlTracker(track, it, query))
                if (it.isNotEmpty()) {
                    dosqt[0].nextRow = 1
                    it[0]
                } else {
                    null
                }
            } ?: run {
                println("SQL Error thrown by Sqlize.select : No Data Returned by Database for Tracker : $track and Query : $query")
                null
            }
        } else {
            println("SQL Error thrown by Sqlize.select : Execution Failed for Tracker : $track and Query : $query")
            null
        }
    }
    fun executeQuery(query: String): Pair<Boolean, List<Map<String, String>>?> {
        Log.d("onClick","reached line 174")
        if (db == null || !db!!.isOpen) {
            Log.e("onClick", "Database not initialized or not open")
            return Pair(false, null)
        }

        return try {

            if (query.trim().startsWith("SELECT", ignoreCase = true)) {
                val rows = mutableListOf<Map<String, String>>()
                val cursor = db?.rawQuery(query, null)

                cursor?.use {
                    while (it.moveToNext()) {
                        val row = mutableMapOf<String, String>()
                        for (i in 0 until it.columnCount) {
                            val columnName = it.getColumnName(i)
                            val value = it.getString(i)
                            row[columnName] = value
                        }
                        rows.add(row)
                    }
                }

                Pair(rows.isNotEmpty(), rows)
            } else {
                db?.execSQL(query)
                Log.d("EXQuery", "Query executed successfully (non-SELECT).")
                Pair(true, null)
            }
        } catch (e: Exception) {
            Log.e("EXQuery", "Error executing query: ${e.message}", e)
            Pair(false, null)
        }

    }


    @SuppressLint("SdCardPath")
    private fun initializeDatabase() {

        val dbPath1 = "/data/data/skynetbee.developers.DevEnvironment/Databases/DevOps.db" // Replace with your actual package name
        val password1 = "123" // SQLCipher encryption key
        val dbFile1 = File(dbPath1)

        // Check if the directory exists, if not, create it
        if (!dbFile1.parentFile?.exists()!!) {
            if (dbFile1.parentFile?.mkdirs()!!) {
                Log.d("DevOpsDatabase", "Directory created successfully.")
            } else {
                Log.e("DevOpsDatabase", "Failed to create directory.")
                return
            }
        }

        try {
            // Check if the database file already exists
            if (dbFile1.exists()) {
                Log.d("DevOpsDatabase", "Database already exists. Skipping creation.")
            } else {
                Log.d("DevOpsDatabase", "Creating new database.")
            }

            // Initialize database
            db = SQLiteDatabase.openOrCreateDatabase(dbPath1, password1, null)
            Log.d("DevOpsDatabase", "Database instance: $db")

            if (db != null) {
                Log.d("DevOpsDatabase", "Database initialized successfully.")
                createTableIfNotExists()
            } else {
                Log.e("DevOpsDatabase", "Database initialization failed.")
            }

        } catch (e: Exception) {
            Log.e("DevOpsDatabase", "Error initializing database: ${e.message}")
        }

    }
    private fun createTableIfNotExists() {
        try {
            db?.execSQL(
                "CREATE TABLE IF NOT EXISTS \"t${getCurrentDate().replace("-","")}\" (" +
                        "fileName TEXT," +
                        "className TEXT," +
                        "function TEXT," +
                        "type TEXT," +
                        "line TEXT," +
                        "message TEXT," +
                        "toe TEXT" +
                        ");"
            )
            Log.d("DevOpsDatabase", "Table created successfully.")
        } catch (e: Exception) {
            Log.e("DevOpsDatabase", "Error creating table: ${e.message}")
        }
    }
}

val DevOps = DevOpsDatabaseConnectionEstablisher()
