package com.health.health.dataclass

import java.io.Serializable

data class Gym(
    val CreatedAt: String,
    val DeletedAt: Any,
    val ID: Int,
    val UpdatedAt: String,
    val amenities: List<String>,
    val coordinates: List<Double>,
    val description: String,
    val images: List<String>,
    val location: String,
    val logo: String,
    val name: String,
    val type: String
): Serializable