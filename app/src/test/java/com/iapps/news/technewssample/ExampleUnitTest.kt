package com.iapps.news.technewssample

import com.iapps.news.technewssample.NewsService.getNews
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.net.HttpURLConnection.HTTP_OK
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    private val mockServer = MockWebServer()

    private fun streamBuffer(file: String): Buffer {
        val stream = javaClass.classLoader!!.getResourceAsStream(file)
        return Buffer().readFrom(stream)
    }

    private val mockJson by lazy {
        val body = streamBuffer("nl6jh.json")
        MockResponse().setResponseCode(HTTP_OK).setBody(body)
    }

    @Before
    fun setUp() {
        mockServer.start()
        NewsService.init(null, mockServer.url("/"))
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun listSize_is24() {
        mockServer.enqueue(mockJson)
        val list = getNews()
        assertEquals(24, list.size)
    }

    @Test
    fun index6_hasNoMedia() {
        mockServer.enqueue(mockJson)
        val list = getNews()
        assertNull(list[6].mediaUrl())
    }

    @Test
    fun networkTimeout() {
        val resp = MockResponse()
            .throttleBody(512, 100, TimeUnit.SECONDS)
            .setBody(mockJson.body)
        mockServer.enqueue(resp)
        val list = getNews()
        assertEquals(24, list.size)
    }
}
