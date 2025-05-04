package com.project.phycare

import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {
      @POST("upload_text")
      fun UploadText(@Body request: TextRequest):retrofit2.Call<TextResponse>
}
data class TextRequest(val text:String)
data class TextResponse(val text: String,val emotion: String,val llm_response:String)