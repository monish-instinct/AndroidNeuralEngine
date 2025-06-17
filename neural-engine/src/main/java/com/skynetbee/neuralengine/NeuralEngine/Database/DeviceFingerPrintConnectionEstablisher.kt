package com.skynetbee.neuralengine

import android.annotation.SuppressLint
import android.util.Log
import net.sqlcipher.database.SQLiteDatabase
import java.io.File

class DeviceFingerPrintConnectionEstablisher {

    var db: SQLiteDatabase? = null

    init {
        System.loadLibrary("sqlcipher")
        initializeDatabase()
    }

    val dfsqt = mutableListOf<SqlTracker>()
    fun select(qry: String, file: String = Thread.currentThread().stackTrace[3].fileName, line: Int = Thread.currentThread().stackTrace[3].lineNumber): Map<String, String>? {
        val query = "SELECT $qry"

        val fileName = file.substringAfterLast("/")
        val lineNumber = " @ $line"
        val track = fileName + lineNumber

        for (sqlTracer in dfsqt) {
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
                dfsqt.add(0, SqlTracker(track, it, query))
                if (it.isNotEmpty()) {
                    dfsqt[0].nextRow = 1
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
        Log.d("DB_QUERY", "Executing: $query")

        if (db == null || !db!!.isOpen) {
            Log.e("DB_QUERY", "Database not initialized or not open")
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
                            row[it.getColumnName(i)] = it.getString(i) ?: "NULL"
                        }
                        rows.add(row)
                    }
                }

                Log.d("DB_QUERY", "Query executed successfully: ${rows.size} rows fetched")
                Pair(rows.isNotEmpty(), rows)
            } else {
                db?.execSQL(query)
                Log.d("DB_QUERY", "Non-SELECT query executed successfully")
                Pair(true, null)
            }
        } catch (e: Exception) {
            Log.e("DB_QUERY", "Error executing query: ${e.localizedMessage}", e)
            Pair(false, null)
        }
    }


    @SuppressLint("SdCardPath")
    private fun initializeDatabase() {

        val dbPath1 = "/data/data/skynetbee.developers.DevEnvironment/Databases/DeviceFingerPrint.db" // Replace with your actual package name
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
                CREATE TABLE IF NOT EXISTS all_system_projects_assigned_to_developers (
                    projectcode TEXT,
                    company TEXT,
                    hardnesslevel TEXT,
                    category TEXT,
                    pronam TEXT,
                    wing TEXT,
                    prolink TEXT,
                    prodes TEXT,
                    dat TEXT,
                    deadlinedat TEXT,
                    completedat TEXT,
                    creditpoints TEXT,
                    marks TEXT,
                    developer_unique_member_id TEXT,
                    offinam TEXT,
                    rating TEXT,
                    tbl TEXT,
                    newtbl TEXT,
                    updation TEXT,
                    area TEXT,
                    counti INTEGER PRIMARY KEY AUTOINCREMENT,
                    mcounti TEXT,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe TEXT,
                    toe TEXT
                );
            """)


            db?.execSQL("""
                CREATE TABLE IF NOT EXISTS all_system_developer_details (
                    offinam TEXT,
                    email TEXT,
                    phone TEXT,
                    unique_member_id TEXT,
                    otp TEXT,
                    overallstars TEXT,
                    cp TEXT,
                    rank TEXT,
                    area TEXT,
                    counti INTEGER PRIMARY KEY AUTOINCREMENT,
                    mcounti TEXT,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe TEXT,
                    toe TEXT
                );
            """)


            db?.execSQL("""
                CREATE TABLE IF NOT EXISTS all_system_leaderboard (
                    fname TEXT,
                    fphoto TEXT,
                    sname TEXT,
                    sphoto TEXT,
                    tname TEXT,
                    tphoto TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00'
                );
            """)

            db?.execSQL("""
                CREATE TABLE IF NOT EXISTS last_communication_with_server (
                    otp TEXT,
                    area TEXT,
                    currentdoe DATE NOT NULL DEFAULT '0000-00-00',
                    currenttoe TIME NOT NULL DEFAULT '00:00:00',
                    localcounti INTEGER PRIMARY KEY AUTOINCREMENT,
                    counti INTEGER,
                    mcounti INTEGER,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe TEXT,
                    toe TEXT NOT NULL DEFAULT '0000-00-00'
                );
            """)

            db?.execSQL("""
                CREATE TABLE IF NOT EXISTS last_page_worked_on (
                    pagename TEXT,
                    area TEXT,
                    currentdoe DATE NOT NULL DEFAULT '0000-00-00',
                    currenttoe TIME NOT NULL DEFAULT '00:00:00',
                    localcounti INTEGER PRIMARY KEY AUTOINCREMENT,
                    counti INTEGER,
                    mcounti INTEGER,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe DATE NOT NULL DEFaULT '0000-00-00',
                    toe TIME NOT NULL DEFAULT '0000-00-00'
                );
            """)

            db?.execSQL("""
                    CREATE TABLE IF NOT EXISTS send_msg_via_unique_member_id (
                    uniquememberid TEXT,
                    message TEXT,   
                    area TEXT,
                    localcounti INTEGER PRIMARY KEY AUTOINCREMENT,
                    mcounti TEXT,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe TEXT,
                    toe TEXT
                );
            """)

            db?.execSQL("""
                    CREATE TABLE IF NOT EXISTS send_msg_via_sms (
                    phonenumber TEXT,
                    message TEXT,   
                    area TEXT,
                    localcounti INTEGER PRIMARY KEY AUTOINCREMENT,
                    mcounti TEXT,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe TEXT,
                    toe TEXT
                );
            """)

            db?.execSQL("""
                    CREATE TABLE IF NOT EXISTS send_msg_via_whatsapp (
                    whatsappnumber TEXT,
                    message TEXT,   
                    area TEXT,
                   localcounti INTEGER PRIMARY KEY AUTOINCREMENT,
                    mcounti TEXT,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe TEXT,
                    toe TEXT
                );
            """)

            db?.execSQL("""
                    CREATE TABLE IF NOT EXISTS send_msg_via_email(
                    email TEXT,
                    subject TEXT,   
                    body TEXT,
                    area TEXT,
                    localcounti INTEGER PRIMARY KEY AUTOINCREMENT,
                    mcounti TEXT,
                    fromdat TEXT,
                    ftodat TEXT NOT NULL DEFAULT '0000-00-00',
                    ftotim TEXT NOT NULL DEFAULT '00:00:00',
                    ftovername TEXT,
                    ftover TEXT,
                    ftopid TEXT,
                    todat TEXT NOT NULL DEFAULT '0000-00-00',
                    totim TEXT NOT NULL DEFAULT '00:00:00',
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
                    doe TEXT,
                    toe TEXT
                );
            """)
            Log.d("DevOpsDatabase", "Table created successfully.")
        } catch (e: Exception) {
            Log.e("DevOpsDatabase", "Error creating table: ${e.message}")
        }
    }
}

val DF = DeviceFingerPrintConnectionEstablisher()