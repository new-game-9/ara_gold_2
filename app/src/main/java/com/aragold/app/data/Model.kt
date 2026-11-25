package com.aragold.app.data

import kotlinx.serialization.Serializable

@Serializable
data class GoldProduct(
    val id: String,
    val name: String,
    val description: String,
    val grams: Double,
    val karat: Int,
    val price: Double,
    val category: String,
    val stones: String,
    val shopName: String,
    val shopPhone: String,
    val photos: List<String>,
    val createdAt: Long,
    val updatedAt: Long,
    val deleted: Boolean = false,
    val ownerId: String? = null
)

fun fmtWeight(g: Double): String =
    if (g % 1.0 == 0.0) g.toInt().toString() else String.format("%.1f", g)

fun fmtPrice(p: Double): String = String.format("%,.0f", p)
