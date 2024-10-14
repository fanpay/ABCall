package com.uniandes.abcall.data.database

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.sql.Timestamp

class ConvertersTest {

    private lateinit var converters: Converters

    @Before
    fun setUp() {
        converters = Converters()
    }

    @Test
    fun `fromTimestamp returns null when input is null`() {
        val result = converters.fromTimestamp(null)
        assertNull("Expected null when input is null", result)
    }

    @Test
    fun `fromTimestamp converts Long to Timestamp correctly`() {
        val time = 1625155200000L // Example timestamp
        val result = converters.fromTimestamp(time)
        assertEquals("Expected correct Timestamp conversion", Timestamp(time), result)
    }

    @Test
    fun `timestampToLong returns null when input is null`() {
        val result = converters.timestampToLong(null)
        assertNull("Expected null when input is null", result)
    }

    @Test
    fun `timestampToLong converts Timestamp to Long correctly`() {
        val timestamp = Timestamp(1625155200000L)
        val result = converters.timestampToLong(timestamp)
        assertEquals("Expected correct Long conversion", 1625155200000L, result)
    }
}
