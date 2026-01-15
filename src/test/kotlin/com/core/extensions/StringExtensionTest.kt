package com.core.extensions

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class StringExtensionTest {

    @Test
    fun `replacePlaceholders should replace single placeholder`() {
        val template = "Hola {nombre}, bienvenido"
        val parameters = mapOf("nombre" to "Carlos")
        
        val result = template.replacePlaceholders(parameters)
        
        assertEquals("Hola Carlos, bienvenido", result)
    }

    @Test
    fun `replacePlaceholders should replace multiple placeholders`() {
        val template = "Hola {nombre}, tu pedido {pedido_id} está listo"
        val parameters = mapOf(
            "nombre" to "María",
            "pedido_id" to "12345"
        )
        
        val result = template.replacePlaceholders(parameters)
        
        assertEquals("Hola María, tu pedido 12345 está listo", result)
    }

    @Test
    fun `replacePlaceholders should return original string when parameters is null`() {
        val template = "Hola {nombre}, bienvenido"
        
        val result = template.replacePlaceholders(null)
        
        assertEquals("Hola {nombre}, bienvenido", result)
    }

    @Test
    fun `replacePlaceholders should return original string when parameters is empty`() {
        val template = "Hola {nombre}, bienvenido"
        val parameters = emptyMap<String, Any>()
        
        val result = template.replacePlaceholders(parameters)
        
        assertEquals("Hola {nombre}, bienvenido", result)
    }

    @Test
    fun `replacePlaceholders should handle non-string values`() {
        val template = "Tienes {cantidad} productos por {precio} pesos"
        val parameters = mapOf(
            "cantidad" to 5,
            "precio" to 100.50
        )
        
        val result = template.replacePlaceholders(parameters)
        
        assertEquals("Tienes 5 productos por 100.5 pesos", result)
    }

    @Test
    fun `replacePlaceholders should not modify string without placeholders`() {
        val template = "Este texto no tiene placeholders"
        val parameters = mapOf("nombre" to "Carlos")
        
        val result = template.replacePlaceholders(parameters)
        
        assertEquals("Este texto no tiene placeholders", result)
    }

    @Test
    fun `replacePlaceholders should replace same placeholder multiple times`() {
        val template = "{nombre} dice: Hola, soy {nombre}"
        val parameters = mapOf("nombre" to "Ana")
        
        val result = template.replacePlaceholders(parameters)
        
        assertEquals("Ana dice: Hola, soy Ana", result)
    }

    @Test
    fun `replacePlaceholders should handle placeholder not in parameters`() {
        val template = "Hola {nombre}, tu email es {email}"
        val parameters = mapOf("nombre" to "Pedro")
        
        val result = template.replacePlaceholders(parameters)
        
        assertEquals("Hola Pedro, tu email es {email}", result)
    }
}
