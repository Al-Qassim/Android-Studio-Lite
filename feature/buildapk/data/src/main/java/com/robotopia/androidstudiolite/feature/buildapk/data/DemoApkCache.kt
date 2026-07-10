package com.robotopia.androidstudiolite.feature.buildapk.data

import android.content.Context
import java.io.File
import java.util.UUID

internal class DemoApkCache(
    private val context: Context,
) {
    fun copyBundledDemoApk(): String {
        val cacheDir = File(context.cacheDir, "buildapk").apply { mkdirs() }
        val destination = File(cacheDir, "demo-${UUID.randomUUID()}.apk")
        context.assets.open(DEMO_APK_ASSET).use { input ->
            destination.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return destination.absolutePath
    }

    private companion object {
        const val DEMO_APK_ASSET = "demo-sample.apk"
    }
}
