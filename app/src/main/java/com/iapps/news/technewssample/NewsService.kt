package com.iapps.news.technewssample

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonReader.Token.BEGIN_ARRAY
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.jetbrains.annotations.TestOnly
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import java.io.File
import java.lang.ref.WeakReference

object NewsService {
    private const val cacheSize = 10L * 1024 * 1024

    private val apiAdapter by lazy {
        object {
            @Suppress("unused")
            @FromJson
            fun fromJson(json: JsonReader): ApiBody {
                var size = 0
                var list = emptyList<NewsEntity>()
                json.beginObject()
                while (json.hasNext()) {
                    when (json.nextName()) {
                        "num_results" -> size = json.nextInt()
                        "results" -> list = parseResults(json, size)
                        else -> json.skipValue()
                    }
                }
                json.endObject()
                return ApiBody(size, list)
            }

            private fun parseResults(json: JsonReader, size: Int): List<NewsEntity> {
                json.beginArray()
                val results = List(size) { parseNews(json) }
                json.endArray()
                return results
            }

            private fun parseNews(json: JsonReader): NewsEntity {
                var title = ""
                var abstract = ""
                var url = ""
                var byline = ""
                var published = ""
                var multimedia = emptyList<MediaEntity>()

                json.beginObject()
                while (json.hasNext()) {
                    when (json.nextName()) {
                        "title" -> title = json.nextString()
                        "abstract" -> abstract = json.nextString()
                        "url" -> url = json.nextString()
                        "byline" -> byline = json.nextString()
                        "published_date" -> published = json.nextString()
                        "multimedia" -> multimedia = parseMultimedia(json)
                        else -> json.skipValue()
                    }
                }
                json.endObject()

                return NewsEntity(title, abstract, url, byline, published, multimedia)
            }

            private fun parseMultimedia(json: JsonReader): List<MediaEntity> {
                if (BEGIN_ARRAY != json.peek()) {
                    json.skipValue()
                    return emptyList()
                }

                json.beginArray()
                val array = mutableListOf<MediaEntity>()
                while (json.hasNext()) {
                    val media = parseMedia(json)
                    array.add(media)
                }
                json.endArray()
                return array
            }

            private fun parseMedia(json: JsonReader): MediaEntity {
                var url = ""
                var format = ""
                var height = 0
                var width = 0
                var type = ""
                var subType = ""
                var caption = ""
                var copyright = ""

                json.beginObject()
                while (json.hasNext()) {
                    when (json.nextName()) {
                        "url" -> url = json.nextString()
                        "format" -> format = json.nextString()
                        "height" -> height = json.nextInt()
                        "width" -> width = json.nextInt()
                        "type" -> type = json.nextString()
                        "subtype" -> subType = json.nextString()
                        "caption" -> caption = json.nextString()
                        "copyright" -> copyright = json.nextString()
                        else -> json.skipValue()
                    }
                }
                json.endObject()

                return MediaEntity(url, format, height, width, type, subType, caption, copyright)
            }

            @Suppress("unused")
            @ToJson
            fun toJson(body: ApiBody): String {
                return "{}" //todo
            }
        }
    }

    private val service by lazy {
        val client = OkHttpClient.Builder().let {
            if (null != cacheDir) it.cache(Cache(cacheDir!!, cacheSize))
            it.build()
        }
        val moshi = Moshi.Builder()
            .add(apiAdapter)
            .build()
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(Api::class.java)
    }

    private var cacheDir: File? = null
    private lateinit var baseUrl: HttpUrl

    fun init(cacheDir: File?, baseUrl: HttpUrl = HttpUrl.parse("https://api.myjson.com/")!!) {
        this.cacheDir = cacheDir
        this.baseUrl = baseUrl
    }

    fun getNews(callback: Callback) {
        val queue = QueueCall(callback)
        service.getNews()
            .enqueue(queue)
    }

    @TestOnly
    fun getNews(): List<NewsEntity> {
        val response = service.getNews().execute()
        val body = response.body() ?: return emptyList()
        return body.results
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

    data class ApiBody(val num_results: Int, val results: List<NewsEntity>)
}