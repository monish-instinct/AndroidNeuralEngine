package com.skynetbee.neuralengine

import android.app.Activity
import android.widget.Toast
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

/**
 * PaymentHandler is a helper class that implements PaymentResultListener.
 * It updates your database based on the payment outcome.
 */
class PaymentHandler(
    private val activity: Activity,
    private val transactionId: String
) : PaymentResultListener {

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        Toast.makeText(activity, "Payment Successful: $razorpayPaymentId", Toast.LENGTH_LONG).show()
        // Update the transaction status to "Success" in your table.
        val query = """
            UPDATE all_digital_pay_transactions_and_attempts_with_details
            SET status = 'Success'
            WHERE transaction_id = '$transactionId'
        """.trimIndent()
        DF.executeQuery(query)
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(activity, "Payment Failed: $response", Toast.LENGTH_LONG).show()
        // Update the transaction status to "Failed" in your table.
        val query = """
            UPDATE all_digital_pay_transactions_and_attempts_with_details
            SET status = 'Failed'
            WHERE transaction_id = '$transactionId'
        """.trimIndent()
        DF.executeQuery(query)
    }
}

/**
 * PaymentScreen is a Compose UI that lets the user enter a payment amount and trigger the payment.
 * It expects an Activity that already implements PaymentResultListener.
 */

/**
 * PayOnline inserts an initial "Pending" transaction, then launches Razorpay's checkout.
 * The Activity passed in must implement PaymentResultListener (or delegate it to PaymentHandler).
 *
 * When running in demo mode, Razorpay's UI will let you simulate "Success" or "Failure".
 * Based on that, the respective callback (onPaymentSuccess or onPaymentError) is triggered.
 */
fun PayOnline(
    activity: Activity,
    amount: String,
    transactionid: String,
    uniquememberid: String,
    paidbyname: String,
    paidfor: String
) {
    val initialStatus = "Pending"

    // Insert an initial transaction record with "Pending" status.
    val insertQuery = """
        INSERT INTO all_digital_pay_transactions_and_attempts_with_details
        (amount, transaction_id, unique_member_id, paidbyname, paidfor, status)
        VALUES ('$amount', '$transactionid', '$uniquememberid', '$paidbyname', '$paidfor', '$initialStatus')
    """.trimIndent()
    DF.executeQuery(insertQuery)

    val checkout = Checkout()
    checkout.setKeyID("rzp_test_cahwIHllEPMzHc") // Replace with your Razorpay test key

    try {
        val options = JSONObject().apply {
            put("name", paidbyname)
            put("description", paidfor)
            put("currency", "INR")
            put("amount", amount.toInt() * 100) // Convert amount from INR to paise.
            put("prefill", JSONObject().apply {
                put("contact", "9876543210")       // Example phone number.
                put("email", "user@example.com")     // Example email.
            })
        }

        // Open Razorpay checkout.
        // IMPORTANT: Your Activity must implement PaymentResultListener.
        // If your Activity already implements it, then its callbacks (or a delegated PaymentHandler) will be triggered.
        checkout.open(activity, options)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        // In case of any exceptions, update the transaction status to "Failed".
        val failureQuery = """
            UPDATE all_digital_pay_transactions_and_attempts_with_details
            SET status = 'Failed'
            WHERE transaction_id = '$transactionid'
        """.trimIndent()
        DF.executeQuery(failureQuery)
    }
}
