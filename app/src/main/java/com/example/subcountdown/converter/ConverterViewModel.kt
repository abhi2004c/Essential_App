package com.example.subcountdown.converter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class ConverterCategory(val title: String) {
    LENGTH("Length"),
    WEIGHT("Weight"),
    TEMPERATURE("Temperature"),
    AREA("Area"),
    VOLUME("Volume")
}

data class ConversionUnit(val name: String, val ratioToBase: Double)

class ConverterViewModel : ViewModel() {
    var selectedCategory by mutableStateOf(ConverterCategory.LENGTH)
    var inputValue by mutableStateOf("1")

    val lengthUnits = listOf(
        ConversionUnit("Meter (m)", 1.0),
        ConversionUnit("Kilometer (km)", 1000.0),
        ConversionUnit("Centimeter (cm)", 0.01),
        ConversionUnit("Millimeter (mm)", 0.001),
        ConversionUnit("Mile (mi)", 1609.34),
        ConversionUnit("Yard (yd)", 0.9144),
        ConversionUnit("Foot (ft)", 0.3048),
        ConversionUnit("Inch (in)", 0.0254)
    )

    val weightUnits = listOf(
        ConversionUnit("Kilogram (kg)", 1.0),
        ConversionUnit("Gram (g)", 0.001),
        ConversionUnit("Milligram (mg)", 0.000001),
        ConversionUnit("Pound (lb)", 0.453592),
        ConversionUnit("Ounce (oz)", 0.0283495),
        ConversionUnit("Metric Ton (t)", 1000.0)
    )

    val areaUnits = listOf(
        ConversionUnit("Square Meter (m²)", 1.0),
        ConversionUnit("Square Kilometer (km²)", 1000000.0),
        ConversionUnit("Square Foot (ft²)", 0.092903),
        ConversionUnit("Acre (ac)", 4046.86),
        ConversionUnit("Hectare (ha)", 10000.0)
    )

    val volumeUnits = listOf(
        ConversionUnit("Liter (L)", 1.0),
        ConversionUnit("Milliliter (ml)", 0.001),
        ConversionUnit("Cubic Meter (m³)", 1000.0),
        ConversionUnit("Gallon (US)", 3.78541),
        ConversionUnit("Quart (US)", 0.946353),
        ConversionUnit("Pint (US)", 0.473176),
        ConversionUnit("Cup (US)", 0.236588)
    )

    fun getUnits(): List<ConversionUnit> {
        return when (selectedCategory) {
            ConverterCategory.LENGTH -> lengthUnits
            ConverterCategory.WEIGHT -> weightUnits
            ConverterCategory.AREA -> areaUnits
            ConverterCategory.VOLUME -> volumeUnits
            ConverterCategory.TEMPERATURE -> emptyList() // Special logic
        }
    }

    fun convertFromBase(valueInBase: Double, unit: ConversionUnit): Double {
        return valueInBase / unit.ratioToBase
    }

    fun convertToBase(value: Double, unit: ConversionUnit): Double {
        return value * unit.ratioToBase
    }
}
