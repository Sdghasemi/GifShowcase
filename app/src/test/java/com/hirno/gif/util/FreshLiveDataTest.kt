package com.hirno.gif.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotEquals

@RunWith(AndroidJUnit4::class)
class FreshLiveDataTest {
    private val liveData = FreshLiveData<Int>()

    @Test
    fun `test emitting value`() {
        val receivedValue = liveData.getOrAwaitValue {
            liveData.value = 1
        }

        assertEquals(
            expected = 1,
            actual = receivedValue,
        )
    }

    @Test
    fun `test emitting new value`() {
        val receivedValue1 = liveData.getOrAwaitValue {
            liveData.value = 1
        }

        val receivedValue2 = liveData.getOrAwaitValue {
            liveData.value = 2
        }

        assertNotEquals(
            illegal = receivedValue1,
            actual = receivedValue2,
        )
    }

    @Test
    fun `test emitting fresh values`() {
        liveData.value = 1

        assertFails {
            liveData.getOrAwaitValue(1)
        }
    }
}