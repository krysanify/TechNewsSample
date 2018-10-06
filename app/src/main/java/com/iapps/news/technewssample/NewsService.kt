package com.iapps.news.technewssample

import android.content.Context
import android.util.Log
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

object NewsService {
    private const val cacheSize = 10L * 1024 * 1024

    private val service by lazy {
        val client = OkHttpClient.Builder()
            .cache(Cache(cacheDir, cacheSize))
            .build()
        Retrofit.Builder()
            //.client(client)
            .baseUrl("https://api.myjson.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    private lateinit var cacheDir: File

    fun init(context: Context) {
        cacheDir = context.cacheDir
    }

    fun getNews(callback: Callback) {
//        val req = Request.Builder().url("https://api.myjson.com/bins/nl6jh").build()
//        val client = OkHttpClient.Builder().build()
//        client.newCall(req).enqueue(object : okhttp3.Callback {
//            override fun onFailure(call: okhttp3.Call, e: IOException) {
//                TODO("not implemented")
//            }
//
//            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
//                val bodyText = response.body()?.string()
//                Log.d("", bodyText)
//            }
//        })
        val queue = QueueCall(callback)
        service.getNews()
            .enqueue(queue)
    }

    class QueueCall(callback: Callback) : retrofit2.Callback<ApiBody> {
        private val callbackRef = WeakReference(callback)

        override fun onResponse(call: Call<ApiBody>, response: Response<ApiBody>) {
            val callback = callbackRef.get()
            callbackRef.clear()
            val body = response.body() ?: return
            callback?.onResult(body.results)
        }

        override fun onFailure(call: Call<ApiBody>, t: Throwable) {
            val callback = callbackRef.get()
            callbackRef.clear()
            callback?.onError(t.message ?: t.toString())
        }
    }

    interface Callback {
        fun onResult(results: List<NewsEntity>)
        fun onError(message: String)
    }

    interface Api {
        @GET("bins/nl6jh")
        fun getNews(): Call<ApiBody>
    }

    data class ApiBody(
        val status: String,
        val copyright: String,
        val section: String,
        val last_updated: String,
        val num_results: Int,
        val results: List<NewsEntity>
    )
}