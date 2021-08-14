package com.timermakov.findmyphone

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.apache.commons.io.IOUtils
import java.io.InputStream

class ChildrenReader(private val context: Context) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val type = Types.newParameterizedType(
        List::class.java,
        Child::class.java,
        LatLng::class.java
    )

    private val childrenAdapter = moshi.adapter<List<Child>>(type)

    private val inputStream: InputStream
        get() = context.resources.openRawResource(R.raw.children)

    fun read(): List<Child>? {
        return childrenAdapter.fromJson(IOUtils.toString(inputStream)).also {
            IOUtils.closeQuietly(inputStream)
        }
    }
}