package utils

import java.text.SimpleDateFormat
import java.util.Date

object MyDateTimeUtils {
    private const val FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss"
    private val sDateTimeFormatter = SimpleDateFormat(FORMAT_DATETIME)

    /**
     * 当前日期时间 yyyy-MM-dd HH:mm:ss
     */
    fun currentDateTime(): String = sDateTimeFormatter.format(Date())
}
