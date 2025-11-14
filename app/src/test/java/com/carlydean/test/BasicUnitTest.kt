package com.carlydean.test

import org.junit.Test
import org.junit.Assert.*

/**
 * Pruebas unitarias básicas para verificar que el entorno funciona
 */
class BasicUnitTest {
    
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    
    @Test
    fun string_concatenation_works() {
        val result = "Hello" + " " + "World"
        assertEquals("Hello World", result)
    }
    
    @Test
    fun kotlin_features_work() {
        // Lambda
        val numbers = listOf(1, 2, 3, 4, 5)
        val doubled = numbers.map { it * 2 }
        assertEquals(listOf(2, 4, 6, 8, 10), doubled)
    }
}
