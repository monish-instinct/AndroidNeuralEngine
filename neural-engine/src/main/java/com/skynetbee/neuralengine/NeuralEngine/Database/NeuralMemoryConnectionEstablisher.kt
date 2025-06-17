package com.skynetbee.neuralengine

import android.annotation.SuppressLint
import android.util.Log
import net.sqlcipher.database.SQLiteDatabase
import java.io.File


open class NeuralMemoryConnectionEstablisher {

    var db: SQLiteDatabase? = null

    init {
        System.loadLibrary("sqlcipher")
        initializeDatabase()
    }

    fun executeQuery(query: String): Pair<Boolean, List<Map<String, String>>?> {
        if (db == null || !db!!.isOpen) {
            Log.e("DB_ERROR", "Database is null or closed")
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
                            val value = it.getString(i) ?: "NULL"
                            row[columnName] = value
                        }
                        rows.add(row)
                    }
                }

                Log.d("DB_QUERY", "Executed SELECT query: $query, Rows fetched: ${rows.size}")
                Pair(rows.isNotEmpty(), rows)
            } else {
                db?.execSQL(query)
                Log.d("DB_QUERY", "Executed non-SELECT query: $query")
                Pair(true, null)
            }
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Error executing query: $query, Exception: ${e.message}")
            Pair(false, null)
        }
    }




    @SuppressLint("SdCardPath")
    private fun initializeDatabase() {

        val dbPath1 = "/data/data/skynetbee.developers.DevEnvironment/Databases/NeuralMemory.db" // Replace with your actual package name
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
            db?.execSQL("""
                CREATE TABLE IF NOT EXISTS just_for_fun (
                    s1 TEXT,
                    s2 TEXT,
                    s3 TEXT,
                    name TEXT,
                    rno TEXT,
                    area TEXT,
                    local_counti INTEGER PRIMARY KEY AUTOINCREMENT,
                    counti TEXT,
                    mcounti TEXT,
                    fromdat TEXT,
                    ftodat TEXT,
                    ftotim TEXT,
                    ftovername  TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT,
                    totim TEXT,
                    tovername TEXT,
                    tover TEXT,
                    topid TEXT,
                    ipmac TEXT,
                    deviceanduserainfo TEXT,
                    basesite TEXT,
                    owncomcode TEXT,
                    testeridentity TEXT,
                    testcontrol TEXT,
                    adderpid TEXT,
                    addername TEXT,
                    adder TEXT,
                    syncstatus TEXT,
                    syncrejectionreason TEXT,
                    doe TEXT,
                    toe TEXT
                );
            """)

            db?.execSQL("""CREATE TABLE IF NOT EXISTS "studata" (
                "unique_member_id"	TEXT,
                "nam"	TEXT,
                "rating"	TEXT,
                "area"	TEXT,
                "localcounti"	INTEGER PRIMARY KEY AUTOINCREMENT,
                "counti"	TEXT,
                "mcounti"	TEXT,
                "fromdat"	TEXT,
                "ftodat"	TEXT DEFAULT '0000-00-00',
                "ftotim"	TEXT DEFAULT '00:00:00',
                "ftovername"	TEXT,
                "ftover"	TEXT,
                "ftopid"	TEXT,
                "todat"	TEXT NOT NULL DEFAULT '0000-00-00',
                "totim"	TEXT DEFAULT '00:00:00',
                "tovername"	TEXT,
                "tover"	TEXT,
                "topid"	TEXT,
                "ipmac"	TEXT,
                "deviceanduserainfo"	TEXT,
                "basesite"	TEXT,
                "owncomcode"	TEXT,
                "testeridentity"	TEXT,
                "testcontrol"	TEXT,
                "adderpid"	TEXT,
                "addername"	TEXT,
                "adder"	TEXT,
                "syncstatus"	TEXT,
                "syncrejectionreason"	TEXT,
                "doe"	TEXT,
                "toe"	TEXT
            )""")
            Log.d("DevOpsDatabase", "Table created successfully.")
        } catch (e: Exception) {
            Log.e("DevOpsDatabase", "Error creating table: ${e.message}")
        }
    }
}



