package com.example.demo

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController("/occurrences")
class ValidateNameController {

    @GetMapping("/occurrences/{inputName}/{documentName}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun process(@PathVariable inputName: String, @PathVariable documentName: String): Result {
        val map: MutableMap<String, Double> = mutableMapOf()
        val documentNameSanitized = documentName.removeBlankSpaces()
        val letterPercentageMap = mappingPercentageOfEachChar(documentNameSanitized)

        inputName.removeBlankSpaces().toSet().forEach {
            val letter = it.toString()
            letterPercentageMap[letter]?.let { lpr ->
                val percentage = lpr.percentage
                if (map.containsKey(letter)) {
                    map[letter] = map.getValue(letter) + percentage
                } else {
                    map[letter] = percentage
                }
            }

        }

        val percentage = map.values.sumByDouble { it }
        return Result(
            fromAppName = inputName,
            documentName = documentName,
            size = documentNameSanitized.length,
            inputPercentage = String.format("%.2f", percentage) + "%"
        )
    }

    private fun mappingPercentageOfEachChar(name: String): MutableMap<String, LetterProcessingResult> {
        val quantityCharMap = mutableMapOf<String, LetterProcessingResult>()
        val nameSize = name.length

        name.forEach {
            val letter = it.toString()

            if (quantityCharMap.containsKey(letter)) {
                val processResult = quantityCharMap.getValue(letter)
                val newQtyValue = processResult.qty + 1
                quantityCharMap[letter] =
                    processResult.copy(qty = newQtyValue, percentage = (newQtyValue.toDouble().div(nameSize)).times(100.0))
            } else {
                val initialValue = 1
                quantityCharMap[letter] =
                    LetterProcessingResult(percentage = initialValue.toDouble().div(nameSize).times(100.0))
            }
        }



        return quantityCharMap
    }

    fun String.removeBlankSpaces(): String {
        run {
            return this.replace(" ", "")
        }
    }
}


data class Result(
    val fromAppName: String,
    val documentName: String,
    val size: Int,
    val inputPercentage: String
)

data class LetterProcessingResult(val qty: Int = 1, val percentage: Double = 0.0)
