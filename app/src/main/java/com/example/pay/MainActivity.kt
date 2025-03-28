package com.example.pay

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.razorpay.Checkout
import org.json.JSONObject
import android.widget.Toast
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

class MainActivity : AppCompatActivity() , PaymentResultWithDataListener {






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        Checkout.preload(applicationContext)
        val co = Checkout()

        co.setKeyID("rzp_test_NhfzLjYqDQdROT")

        val payButton = findViewById<Button>(R.id.payBtn)
        payButton.setOnClickListener {


            initPayment()
        }

    }

    private fun initPayment() {
        val activity: Activity = this
        val co = Checkout()
        val amountInput = findViewById<EditText>(R.id.amountEditText).text.toString()

        if (amountInput.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Convert to subunits (INR → paise)
            val amount = (amountInput.toDouble() * 100).toInt().toString()

            val options = JSONObject().apply {
                put("name", "SOUMYAJIT NANDI")
                put("description", "Reference No. #123456")
                put("image", "http://example.com/image/rzp.jpg")
                put("theme.color", "#FF1515")
                put("currency", "INR")
                put("amount", amount) // Use dynamic amount

                // ... rest of your options ...
            }

            co.open(activity, options)
        } catch (e: NumberFormatException) {
            Toast.makeText(activity, "Invalid amount format", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {

        Toast.makeText(this,"Payment Successful",Toast.LENGTH_LONG).show()

    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {

        Toast.makeText(this,"Payment Failed",Toast.LENGTH_LONG).show()

    }

}
