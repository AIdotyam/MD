package com.capstone.aiyam.utils

fun getRandomDead(): String {
    val alerts = listOf("Chicken Out Cold", "Chicken Fallen", "Chicken Perished", "Dead Chicken")
    return alerts.random()
}
