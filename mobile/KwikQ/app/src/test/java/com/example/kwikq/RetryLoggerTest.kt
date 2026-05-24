package com.example.kwikq

import com.example.kwikq.network.RetryLogger
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RetryLoggerTest {

    @Before
    fun setup() {
        RetryLogger.clear()
    }

    @Test
    fun logsAndClearsEvents() {
        RetryLogger.log(1, "TestAction")
        RetryLogger.log(2, "TestAction")

        val events = RetryLogger.getEvents()
        assertEquals(2, events.size)
        assertEquals("attempt=1 action=TestAction", events[0])
        assertEquals("attempt=2 action=TestAction", events[1])

        RetryLogger.clear()
        assertEquals(0, RetryLogger.getEvents().size)
    }
}
