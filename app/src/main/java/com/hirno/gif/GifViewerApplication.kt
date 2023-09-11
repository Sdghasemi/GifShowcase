package com.hirno.gif

import android.app.Application
import com.hirno.gif.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module


class GifViewerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        setupDependencyInjection()
    }

    private fun setupDependencyInjection() {
        startKoin {
            androidLogger()
            androidContext(this@GifViewerApplication)
            modules(
                appModule,
                module {
                    single { this@GifViewerApplication }
                }
            )
        }
    }
}