package com.example.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    val mapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())

    @Test
    fun shouldReturnSuccessWithFilledBody() {
        val result = restTemplate.getForEntity<String>("/occurrences/wesley/wesleyferminodacruz", String::class)

        assertTrue(result.statusCode.is2xxSuccessful)
        assertNotNull(result.body)
    }

    @Test
    fun shouldReturnCorrectlyPercentageAndSizeAccordingName() {
        val userInput = "teixeira"
        val fromDocumentName = "nilzaaparecidateixeira"

        val requestResult = restTemplate.getForEntity<String>("/occurrences/${userInput}/${fromDocumentName}", String::class)

        val result = mapper.readValue(requestResult.body, Result::class.java)
        assertEquals(fromDocumentName.length, result.size)
        assertEquals("72,73%", result.inputPercentage)
    }
}
