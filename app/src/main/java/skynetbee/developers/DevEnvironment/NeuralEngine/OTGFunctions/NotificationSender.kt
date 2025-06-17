package skynetbee.developers.DevEnvironment


fun sendSMS(phoneNumber: String, message: String) {
    val query = "INSERT INTO send_msg_via_sms (\n" +
            "    phonenumber, message, area, mcounti, fromdat, ftodat, ftotim, ftovername, \n" +
            "    ftover, ftopid, todat, totim, tovername, tover, topid, ipmac, \n" +
            "    deviceanduserainfo, basesite, owncomcode, testeridentity, testcontrol, \n" +
            "    adderpid, addername, adder, doe, toe\n" +
            ") VALUES (\n" +
            "    '${phoneNumber}', '${message}', 'skytest', '', '', \n" +
            "    '', '00:00:00', '', '', '', '0000-00-00', \n" +
            "    '00:00:00', '', '', '', '', \n" +
            "    '', '', '', '', '', \n" +
            "    '', '', '', '${getCurrentDate()}', '${getCurrentTime()}'\n" +
            ");"
    DF.executeQuery(query)
}

fun sendNotification(phoneNumber: String, message: String) {
    val query = "INSERT INTO send_msg_via_unique_member_id (\n" +
            "    uniquememberid, message, area, mcounti, fromdat, ftodat, ftotim, ftovername, \n" +
            "    ftover, ftopid, todat, totim, tovername, tover, topid, ipmac, \n" +
            "    deviceanduserainfo, basesite, owncomcode, testeridentity, testcontrol, \n" +
            "    adderpid, addername, adder, doe, toe\n" +
            ") VALUES (\n" +
            "    '${phoneNumber}', '${message}', 'skytest', '', '', \n" +
            "    '', '00:00:00', '', '', '', '0000-00-00', \n" +
            "    '00:00:00', '', '', '', '', \n" +
            "    '', '', '', '', '', \n" +
            "    '', '', '', '${getCurrentDate()}', '${getCurrentTime()}'\n" +
            ");"
    DF.executeQuery(query)
}

fun sendWhatsApp(whatsappNumber: String, message: String, group: String) {
    val query =  "INSERT INTO send_msg_via_whatsapp (\n" +
            "    whatsappnumber, message, area, mcounti, fromdat, ftodat, ftotim, ftovername, \n" +
            "    ftover, ftopid, todat, totim, tovername, tover, topid, ipmac, \n" +
            "    deviceanduserainfo, basesite, owncomcode, testeridentity, testcontrol, \n" +
            "    adderpid, addername, adder, doe, toe\n" +
            ") VALUES (\n" +
            "    '${whatsappNumber}', '${message}', 'skytest', '', '', \n" +
            "    '', '00:00:00', '', '', '', '0000-00-00', \n" +
            "    '00:00:00', '', '', '', '', \n" +
            "    '', '', '', '', '', \n" +
            "    '', '', '', '${getCurrentDate()}', '${getCurrentTime()}'\n" +
            ");"
    DF.executeQuery(query)
}

fun sendEmail(email: String, subject: String, body: String) {
    val query =  "INSERT INTO send_msg_via_email (\n" +
            "    email, subject,body, area, mcounti, fromdat, ftodat, ftotim, ftovername, \n" +
            "    ftover, ftopid, todat, totim, tovername, tover, topid, ipmac, \n" +
            "    deviceanduserainfo, basesite, owncomcode, testeridentity, testcontrol, \n" +
            "    adderpid, addername, adder, doe, toe\n" +
            ") VALUES (\n" +
            "    '${email}', '${subject}','${body}','skytest', '', '', \n" +
            "    '', '00:00:00', '', '', '', '0000-00-00', \n" +
            "    '00:00:00', '', '', '', '', \n" +
            "    '', '', '', '', '', \n" +
            "    '', '', '', '${getCurrentDate()}', '${getCurrentTime()}'\n" +
            ");"
    DF.executeQuery(query)


}

