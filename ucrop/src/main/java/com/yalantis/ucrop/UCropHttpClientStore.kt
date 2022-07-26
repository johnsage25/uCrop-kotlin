package com.yalantis.ucrop

import okhttp3.OkHttpClient
import com.yalantis.ucrop.UCropHttpClientStore

class UCropHttpClientStore private constructor() {
    var client: OkHttpClient? = null
        get() {
            if (field == null) {
                field = OkHttpClient()
            }
            return field
        }

    companion object {
        @JvmField
        val INSTANCE = UCropHttpClientStore()
    }
}