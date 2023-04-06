package aockt.y2015

import io.github.jadarma.aockt.core.Solution
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object Y2015D12 : Solution {

    /** Recursively find all integers in this Json structure and add them up. */
    private fun JsonElement.sum(): Int = when (this) {
        is JsonPrimitive -> content.toIntOrNull() ?: 0
        is JsonArray -> sumOf { it.sum() }
        is JsonObject -> values.sumOf { it.sum() }
    }

    /** Like [JsonElement.sum], but completely ignoring out all objects containing the value "red". */
    private fun JsonElement.sumIgnoringRed(): Int = when (this) {
        is JsonPrimitive -> content.toIntOrNull() ?: 0
        is JsonArray -> sumOf { it.sumIgnoringRed() }
        is JsonObject -> values
            .onEach { if (it is JsonPrimitive && it.content == "red") return 0 }
            .sumOf { it.sumIgnoringRed() }
    }

    override fun partOne(input: String) = Json.parseToJsonElement(input).sum()
    override fun partTwo(input: String) = Json.parseToJsonElement(input).sumIgnoringRed()
}
