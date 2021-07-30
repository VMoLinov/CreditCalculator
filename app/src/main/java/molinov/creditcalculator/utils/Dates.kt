package molinov.creditcalculator.model

import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

private const val US_TIME_PATTERN = "MM.dd.yyyy"
private const val CLASSIC_TIME_PATTERN = "dd.MM.yyyy"
private val MONTHS_RUS = listOf(
    "января",
    "февраля",
    "марта",
    "апреля",
    "мая",
    "июня",
    "июля",
    "августа",
    "сентября",
    "октября",
    "ноября",
    "декабря"
)

fun setPickerConstraints(): CalendarConstraints {
    return CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
        .build()
}

fun setDefaultPickerDate(): Long {
    val today = MaterialDatePicker.todayInUtcMilliseconds()
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = today
    calendar.add(Calendar.MONTH, 1)
    return calendar.timeInMillis
}

fun parseStringToDate(date: String): Date = getSimpleDateFormat().parse(date) ?: Date()

fun parseLongDateToString(long: Long): String {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.timeInMillis = long
    val formatter = getSimpleDateFormat()
    val string = formatter.format(calendar.time)
    val month = calendar.get(Calendar.MONTH)
    return string.replaceRange(2, 8, " ${MONTHS_RUS[month]} ")
}

fun getSimpleDateFormat(): SimpleDateFormat {
    return if (Locale.getDefault() == Locale.US) {
        SimpleDateFormat(
            US_TIME_PATTERN, Locale.getDefault()
        )
    } else {
        SimpleDateFormat(
            CLASSIC_TIME_PATTERN, Locale.getDefault()
        )
    }
}
