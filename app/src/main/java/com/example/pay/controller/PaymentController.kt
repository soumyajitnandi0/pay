package com.example.pay.controller

import com.razorpay.Order
import com.razorpay.RazorpayClient
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RestController
import org.json.JSONObject
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.util.HexFormat

// Data class for payment verification request
data class PaymentVerificationRequest(
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String
) {
    fun getRazorpayOrderId() = razorpayOrderId
    fun getRazorpayPaymentId() = razorpayPaymentId
    fun getRazorpaySignature() = razorpaySignature
}

@RestController
@RequestMapping("/payments")
class PaymentController(
    @Value("\${razorpay.key.id}") private val razorpayKeyId: String,
    @Value("\${razorpay.key.secret}") private val razorpayKeySecret: String
) {

    @PostMapping("/create-order")
    fun createRazorpayOrder(@RequestParam amount: Double): String {
        try {
            val razorpayClient = RazorpayClient(razorpayKeyId, razorpayKeySecret)

            // Convert amount to paisa (smallest currency unit)
            val amountInPaisa = (amount * 100).toInt()

            val orderRequest = JSONObject().apply {
                put("amount", amountInPaisa)
                put("currency", "INR")
                // Optional: Add additional details
                put("receipt", "order_rcptid_${System.currentTimeMillis()}")
            }

            val order: Order = razorpayClient.orders.create(orderRequest)
            return order.get("id") // Return the order ID to the client
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error creating order"
        }
    }

    @PostMapping("/verify-payment")
    fun verifyPayment(@RequestBody request: PaymentVerificationRequest): Boolean {
        return try {
            // Verify payment signature
            val generatedSignature = calculateSignature(
                "${request.razorpayOrderId}|${request.razorpayPaymentId}",
                razorpayKeySecret
            )

            generatedSignature == request.razorpaySignature
        } catch (e: Exception) {
            false
        }
    }

    // Signature calculation method
    private fun calculateSignature(data: String, secret: String): String {
        val sha256Hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        sha256Hmac.init(secretKey)

        val hashBytes = sha256Hmac.doFinal(data.toByteArray())
        return HexFormat.of().formatHex(hashBytes)
    }
}