package com.hirno.gif.di

import android.app.Application
import androidx.test.platform.app.InstrumentationRegistry
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.dsl.module

fun KoinApplication.testAppModules(vararg modules: Module) {
    val applicationContext = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
    androidContext(applicationContext)
    modules(
        appModule,
        *modules,
        module {
            single {
                applicationContext as Application
            }
        },
    )
}