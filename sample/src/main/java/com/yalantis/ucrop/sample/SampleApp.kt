package com.yalantis.ucrop.sample

import android.app.Application
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import com.yalantis.ucrop.UCropHttpClientStore

class SampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        setUcropHttpClient()
    }

    private fun setUcropHttpClient() {
        val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .allEnabledCipherSuites()
            .allEnabledTlsVersions()
            .build()
        val client = OkHttpClient.Builder()
            .connectionSpecs(listOf(cs))
            .build()
        UCropHttpClientStore.INSTANCE.client = client
    }
}