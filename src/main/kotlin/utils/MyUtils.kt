package utils

object MyUtils {

    fun floatToPercentage(value: Float): String {
        val percentage = String.format("%.2f", value * 100)
        return "$percentage%"
    }
}