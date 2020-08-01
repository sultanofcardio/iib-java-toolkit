package com.sultanofcardio.iib.interfaces

interface Configuration {
    fun getStringProperty(property: String, defaultValue: String): String
    fun getStringProperty(property: String): String?
    fun setProperty(property: String, value: String)
    fun getIntProperty(property: String): Int
    fun getIntProperty(property: String, defaultValue: Int): Int
    fun getLongProperty(property: String): Long
    fun getLongProperty(property: String, defaultValue: Long): Long
    fun getDoubleProperty(property: String): Double
    fun getDoubleProperty(property: String, defaultValue: Double): Double
}