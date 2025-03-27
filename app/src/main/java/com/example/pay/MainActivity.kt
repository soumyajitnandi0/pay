package com.example.pay

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), PaymentResultWithDataListener {

    private lateinit var amountEditText: EditText
    private lateinit var payButton: Button

    // Retrofit interface for backend communication
    interface PaymentService {
        @POST("/payments/create-order")
        fun createOrder(@Query("amount") amount: Double): Call<String>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        amountEditText = findViewById(R.id.amountEditText)
        payButton = findViewById(R.id.payBtn)

        // Initialize Razorpay
        Checkout.preload(applicationContext)

        payButton.setOnClickListener {
            val amount = amountEditText.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                createRazorpayOrder(amount)
            } else {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createRazorpayOrder(amount: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://your-backend-url.com")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val service = retrofit.create(PaymentService::class.java)

        service.createOrder(amount).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val orderId = response.body()
                    initiateRazorpayPayment(orderId!!, amount)
                } else {
                    Toast.makeText(this@MainActivity, "Order creation failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initiateRazorpayPayment(orderId: String, amount: Double) {
        val checkout = Checkout()
        checkout.setKeyID("YOUR_RAZORPAY_KEY_ID")

        try {
            val options = JSONObject().apply {
                put("name", "Your Business Name")
                put("description", "Payment for Services")
                put("order_id", orderId)
                put("amount", (amount * 100).toInt().toString())
                put("currency", "INR")

                val prefill = JSONObject().apply {
                    put("email", "customer@example.com")
                    put("contact", "9876543210")
                }
                put("prefill", prefill)
            }

            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Toast.makeText(this, "Payment Successful", Toast.LENGTH_LONG).show()
        // Verify payment with your backend
    }

    override fun onPaymentError(errorCode: Int, errorDescription: String?, paymentData: PaymentData?) {
        Toast.makeText(this, "Payment Failed: $errorDescription", Toast.LENGTH_LONG).show()
    }
}