package com.skynetbee.neuralengine

import android.util.Log
import kotlin.collections.filterKeys
import kotlin.collections.firstOrNull
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.collections.mapValues
import kotlin.let
import kotlin.run
import kotlin.text.isEmpty
import kotlin.text.substringAfterLast
import kotlin.text.toIntOrNull


var sql = SQLize()


class SqlTracker(
    var tracker: String,
    var tableData: List<Map<String, String>>,
    var query: String,
    var nextRow: Int = 0
)


class SQLize() : NeuralMemoryConnectionEstablisher() {
    init {
        Log.d("onClick","SQL Object Created")
    }

    val sqt = mutableListOf<SqlTracker>()

    fun insert(
        tableName: String,
        kvp: Map<String, String>,
        fileName: String = Thread.currentThread().stackTrace[3].fileName
    ): Pair<Triple<Int, Boolean, Boolean>, String>{

        val query = generateSelectQuery(tableName, kvp) + " and todat = '0000-00-00' and area='skytest'"

        reset(43)
        val result = this.executeQuery("SELECT $query")

        cl(result, "SQLizer")

        return if (result.first) {
            val rci = result.second?.get(0)?.get("localcounti")?.toIntOrNull() ?: -1
            Pair(Triple(rci, true,true), query)
        } else {
            val freeInsertResult = freeInsert(tableName, kvp, fileName)
            Pair(Triple( freeInsertResult.first, freeInsertResult.second, true), freeInsertResult.third)
        }
    }

    private fun generateSelectQuery(tableName: String, data: Map<String, Any>): String {
        val conditions = data.entries.joinToString(" AND ") { (key, value) ->
            if (value == null) "$key IS NULL" else "$key='$value'"
        }
        return "localcounti FROM $tableName WHERE $conditions"
    }


    fun freeInsert(tableName: String, kvp: Map<String, String>, fileName: String = Thread.currentThread().stackTrace[3].fileName): Triple<Int, Boolean, String> {
        val columns = mutableListOf<String>()
        val values = mutableListOf<String>()

        for ((key, value) in kvp) {
            columns.add(enqry(key))
            values.add(ivc(value))
        }

        columns.addAll(listOf("area", "testcontrol", "ipmac", "deviceanduserainfo", "basesite", "owncomcode", "adderpid", "addername", "adder", "doe", "toe"))
        val randomStr = generateRandomAlphaNumeric(7)
        values.addAll(listOf(ivc(user.getArea()), ivc(randomStr), ivc("DevEnvironment"), ivc("Developer Environment"), ivc("DevEnv"), ivc("developer"), ivc(fileName), ivc(user.getName()), ivc(user.getId()), ivc(getCurrentDate()), ivc(getCurrentTime())))

        val columnsString = columns.joinToString(", ")
        val valuesString = values.joinToString(", ")
        val insertQuery = "INSERT INTO $tableName($columnsString) VALUES($valuesString)"

        return if (executeQuery(insertQuery).first) {
            val ctn = executeQuery("SELECT localcounti FROM $tableName WHERE testcontrol = '$randomStr'")
            val count = ctn.second?.firstOrNull()?.get("localcounti")?.toIntOrNull() ?: -1
            if (count != -1) {
                executeQuery("UPDATE $tableName SET testcontrol='' WHERE localcounti = $count AND testcontrol='$randomStr'")
                Triple(count, true, insertQuery)
            } else {
                Triple(-1, false, insertQuery)
            }
        } else {
            Triple(-1, false, insertQuery)
        }
    }


    fun read(columns: String, tableName: String, whereCondition: String? = "", others : String? = "", file: String = Thread.currentThread().stackTrace[3].fileName, line: Int = Thread.currentThread().stackTrace[3].lineNumber): List<Map<String, String>>?{

    // Use line number as default tracker if no tracker is provided
    var condition = whereCondition

    if (!condition.isNullOrEmpty()) {
        condition =  "$whereCondition AND todat IS NULL OR todat = '0000-00-00' AND totim IS NULL OR totim = 00-00-00"
    } else {
        condition = "todat = '0000-00-00'"
    }
    val effectiveTracker ="$file @ $line"

    // Build the SQL query based on parameters
    val query = if (condition.isEmpty() && others?.isEmpty() == true) {
        "SELECT $columns FROM $tableName"
    } else {
        "SELECT $columns FROM $tableName WHERE $condition $others"
    }

    //Log.d("gowtham", "read: $condition")
    // Find the query in the tracker (sqt) to continue from the last accessed row
    for (sqlTracer in sqt) {

        // This helps in determining if the query has already been executed and whether the function needs to continue from the last row it fetched
        if (sqlTracer.query == query && sqlTracer.tracker == effectiveTracker) {
            return sqlTracer.tableData
        } else {
            Log.d("Read Function", "No more rows to read. Returning null.")
            return null
        }
    }
    // If the query is not already in the tracker, execute it
    // In this section the query is executed
    val eR = executeQuery(query)
    val temp = eR.second
    Log.d("Read Function()", "read: $query")
    if (eR.first) {
        if (temp != null && temp.isNotEmpty()) {
            // Add the new query to the tracker
            sqt.add(0, SqlTracker(tracker = effectiveTracker, tableData = temp, query = query))
            sqt[0].nextRow = 1
            Log.d("Read Function", "Query executed successfully, returning first row: ${sqt[0].tracker}")
            return sqt[0].tableData
        } else {
            sqt.add(0, SqlTracker(tracker = effectiveTracker, tableData = emptyList() , query = query))
            sqt[0].nextRow = 1
            Log.d("Read Function", "Query executed but returned no results.")
        }
    } else {
        sqt.add(0, SqlTracker(tracker = effectiveTracker, tableData = emptyList() , query = query))
        sqt[0].nextRow = 1
        Log.d("Read Function", "Query execution failed.")
        //Log.d("gowtham", "read : $sqt")
    }
    Log.d("Read Function", "1")
    // Return null if no data was found or an error occurred
    return null
}
    // This is the fun that reads the value from the SQL Database
    fun readRowByRow(columnNames: String,
                     tableName: String,
                     whereCondition: String? = "",
                     others: String? = "",
                     file: String = Thread.currentThread().stackTrace[3].fileName,
                     line: Int = Thread.currentThread().stackTrace[3].lineNumber): Map<String, String>? {

        // Build the SQL query based on parameters
        val query = if (whereCondition.isNullOrEmpty()) {
            "$columnNames FROM $tableName WHERE todat = '0000-00-00' AND area = 'skytest' $others;"
        } else {
            "$columnNames FROM $tableName WHERE $whereCondition AND todat = '0000-00-00' AND area = 'skytest' $others;"
        }
        return select(query, file, line)
    }

    fun restart(line: Int = Throwable().stackTrace[1].lineNumber, file: String = Throwable().stackTrace[1].fileName): Boolean {

        val tracker = "${file} @ ${line + 1}"

        Log.d("megan", "reset: $tracker")
        var i = 0
        for (cv in sqt) {
            if (cv.tracker == tracker) {
                sqt[i].nextRow = 0
                return true
            } else {
                i++
            }
        }
        return false
    }

    fun restart(file: String = Throwable().stackTrace[1].fileName,lineNumber: Int): Boolean {

        val tracker = "${file} @ ${lineNumber}"

        Log.d("megan", "reset: $tracker")
        var i = 0
        for (cv in sqt) {
            if (cv.tracker == tracker) {
                sqt[i].nextRow = 0
                return true
            } else {
                i++
            }
        }
        return false
    }

    fun reset(line: Int = Throwable().stackTrace[1].lineNumber, file: String = Throwable().stackTrace[1].fileName): Boolean {

        val tracker = "${file} @ ${line + 1}"

        Log.d("megan", "reset: $tracker")
        var i = 0
        for (cv in sqt) {
            if (cv.tracker == tracker) {
                sqt.removeAt(i)
                return true
            } else {
                i++
            }
        }
        return false
    }

    fun reset(file: String = Throwable().stackTrace[1].fileName,lineNumber: Int): Boolean {

        val tracker = "${file} @ ${lineNumber}"

        Log.d("megan", "reset: $tracker")
        var i = 0
        for (cv in sqt) {
            if (cv.tracker == tracker) {
                sqt.removeAt(i)
                return true
            } else {
                i++
            }
        }
        return false
    }


    fun delete(tableName: String, whereCondition: String, fileName: String = Thread.currentThread().stackTrace[3].fileName): Triple<Int, Boolean, String> {
        val selQuery = "SELECT count(*) FROM $tableName WHERE $whereCondition AND area='${user.getArea()}' AND todat='0000-00-00'"
        val eQ = executeQuery(selQuery).second?.firstOrNull()?.get("count(*)")
        val affectedRows = eQ?.toIntOrNull() ?: 0

        val query = "UPDATE $tableName SET todat='${getCurrentDate()}', totim='${getCurrentTime()}', tover='${user.getId()}', tovername='${user.getName()}', topid='${fileName}' WHERE $whereCondition AND area='${user.getArea()}' AND todat='0000-00-00'"

        return if (executeQuery(query).first) {
            Triple(affectedRows, true, query)
        } else {
            Triple(0, false, query)
        }
    }

    fun end(tableName: String, whereCondition: String, fileName: String = Thread.currentThread().stackTrace[3].fileName): Triple<Int, Boolean, String> {
        val selQuery = "SELECT count(*) FROM $tableName WHERE $whereCondition AND area='${user.getArea()}' AND ftodat='0000-00-00'"
        val eQ = executeQuery(selQuery).second?.firstOrNull()?.get("count(*)")
        val affectedRows = eQ?.toIntOrNull() ?: 0

        val query = "UPDATE $tableName SET ftodat='${getCurrentDate()}', ftotime='${getCurrentTime()}', ftover='${user.getId()}', ftovername='${user.getName()}', ftopid='${fileName}' WHERE $whereCondition AND area='${user.getArea()}' AND ftodat='0000-00-00'"

        return if (executeQuery(query).first) {
            Triple(affectedRows, true, query)
        } else {
            Triple(0, false, query)
        }
    }


    fun debug(line: Int, tag: String = "SQL Debug", file: String = Thread.currentThread().stackTrace[3].fileName): SqlTracker? {
        val tracker = "${file} @ ${line}"
        for (cv in sqt) {
            if (cv.tracker == tracker) {
                return cv
            }
        }
        return null
    }
    // This is the fun that reads the value from the SQL Database
    fun select(qry: String, file: String = Throwable().stackTrace[1].fileName, line: Int = Throwable().stackTrace[1].lineNumber): Map<String, String>? {
        val query = "SELECT $qry"

        val fileName = file.substringAfterLast("/")
        val lineNumber = " @ $line"
        val track = fileName + lineNumber


        for (sqlTracer in sqt) {


            if (sqlTracer.tracker == track) {

                if (sqlTracer.query == query) {
                    return if (sqlTracer.tableData.indices.contains(sqlTracer.nextRow)) {

                        sqlTracer.tableData[sqlTracer.nextRow++]
                    } else {

                        null
                    }
                } else {
                    Log.d("SQLError", "SQL Error thrown by Sqlize.select : tracker : $track reused for ${sqlTracer.query} and $query")
                    return null //if code reach this line i start again from the begining of the for
                }
            }
        }

        val eR = executeQuery(query)

        return if (eR.first) {
            eR.second?.let {
                sqt.add(0, SqlTracker(track, it, query))
                if (it.isNotEmpty()) {
                   sqt[0].nextRow = 1


                    it[0]
                } else {

                    null
                }
            } ?: run {

                Log.d("SQLError", "SQL Error thrown by Sqlize.select : No Data Returned by Database for Tracker : $track and Query : $query")
                null
            }
        } else {

            Log.d("SQLError", "SQL Error thrown by Sqlize.select : Execution Failed for Tracker : $track and Query : $query")
            null
        }
    }

    fun update(tableName: String, kvp: Map<String, String>, localcounti: String): Pair<Triple<Int, Boolean, Boolean>, String>{
        val selQuery = "SELECT * FROM $tableName WHERE localcounti='$localcounti'"
        val eR = executeQuery(selQuery)

        return if (eR.first) {
            eR.second?.firstOrNull()?.let { temp ->
                val invalidKeys = setOf(
                    "area", "counti", "doe", "toe", "mcounti", "localcounti", "syncstatus",
                    "syncrejectionreason", "fromdat", "ftodat", "ftotim", "ftovernam", "ftover",
                    "ftopid", "todat", "totim", "tovernam", "tover", "topid", "deviceanduserainfo",
                    "basesite", "owncomcode", "ipmac", "testeridentity", "testcontrol", "adderpid",
                    "addernam", "adder"
                )


                val ourkvp = temp.filterKeys { it !in invalidKeys }.mapValues { kvp[it.key] ?: it.value }
                delete(tableName, "localcounti='$localcounti'")
                val insertResult = insert(tableName, ourkvp)
                //Quad(insertResult.first, insertResult.second, true, selQuery)
                // Explicitly cast values to Int and Boolean
                val firstValue = insertResult.first.first
                val secondValue = insertResult.first.second
                val thirdValue = insertResult.first.third

                Pair(Triple(firstValue, secondValue, thirdValue), selQuery)
            } ?: Pair(Triple(-1, false, false), selQuery )
        } else {
            Pair(Triple(-1, false, false), selQuery )
        }
    }

    fun ivc(value: String): String {
        // Wrap the value in quotes for SQL safety
        return "'$value'"
    }

    fun enqry(value: String): String {
        // Escape or sanitize the column name if necessary
        return "`$value`"
    }
}