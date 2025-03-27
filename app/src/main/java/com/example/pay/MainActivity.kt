package com.example.pay

import android.app.Activity
import android.os.Bundle
import android.widget.Button
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
        enableEdgeToEdge()
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

        try {
            val options = JSONObject()
            options.put("name","SOUMYAJIT NANDI")
            options.put("description","Reference No. #123456")
            //You can omit the image option to fetch the image from the Dashboard
            options.put("image","http://example.com/image/rzp.jpg")
            options.put("theme.color", "#FF1515");
            options.put("currency","INR");
//            options.put("order_id", "order_DBJOWzybf0sJbb");
            options.put("amount","50000")//pass amount in currency subunits

            val retryObj = JSONObject();
            retryObj.put("enabled", false);
//            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            val prefill = JSONObject()
            prefill.put("email","soumyajitnandi7384@gmail.com")
            prefill.put("contact","9876543210")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {

        Toast.makeText(this,"Payment Successful",Toast.LENGTH_LONG).show()

    }

    override fun onPaymentError(p0: Int, p1: String?, p2: PaymentData?) {

        Toast.makeText(this,"Payment Failed",Toast.LENGTH_LONG).show()

    }

}
