package com.capstone.aiyam.utils

fun getRandomDead(): String {
    val alerts = listOf("Chicken Out Cold", "Chicken Has Fallen", "Chicken Has Perished", "Dead Chicken Alert")
    return alerts.random()
}
