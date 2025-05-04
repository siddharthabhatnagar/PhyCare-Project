package com.project.phycare.ThoughtSharing

import android.annotation.SuppressLint
import android.net.http.UrlRequest.Callback
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.project.phycare.APIService
import com.project.phycare.R
import com.project.phycare.TextRequest
import com.project.phycare.TextResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SharingWithAIActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sharing_with_aiactivity)
        val inputmsg=findViewById<EditText>(R.id.inputMessage)
        val sendbtn=findViewById<ImageButton>(R.id.sendMessageButton)
        val textResponse=findViewById<TextView>(R.id.text_response)
        val emotionResponse=findViewById<TextView>(R.id.emotion_response)
        val llmResponse=findViewById<TextView>(R.id.llm_response)
        val retrofit=Retrofit.Builder()
            .baseUrl("https://bhatnagarbrand.pythonanywhere.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val apiservice=retrofit.create(APIService::class.java)
        sendbtn.setOnClickListener {
            textResponse.text="Loading"
            emotionResponse.text=""
            llmResponse.text=""
            val request=TextRequest(inputmsg.text.toString())
            apiservice.UploadText(request).enqueue(object : retrofit2.Callback<TextResponse>{
                override fun onResponse(p0: Call<TextResponse>, p1: Response<TextResponse>) {
                    if (p1.isSuccessful){
                        textResponse.text=p1.body()!!.text
                        emotionResponse.text=p1.body()!!.emotion
                        llmResponse.text=p1.body()!!.llm_response
                    }
                    else{
                        textResponse.text="Not Successful"
                    }
                }

                override fun onFailure(p0: Call<TextResponse>, p1: Throwable) {
                    textResponse.text="Failure"
                }

            })
        }
    }
}