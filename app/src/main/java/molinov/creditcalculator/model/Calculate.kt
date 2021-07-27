package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Parcelize
data class Calculate(
    val payment: String,
    val overPayment: String,
    val totalPayment: String
) : Parcelable

private const val US_TIME_FORMAT = "MM.dd.yyyy"
private const val CLASSIC_TIME_FORMAT = "dd.MM.yyyy"
private const val LOW_SCALE = 2
private const val BIG_SCALE = 10
private val MONTHS = BigDecimal(12).setLowScale()
private val PERCENTS = BigDecimal(100).setLowScale()
private val DAYS_IN_YEAR = BigDecimal(365).setLowScale()
private val MILLIS_IN_A_DAY = BigDecimal(86400000).setLowScale()
private val ONE = BigDecimal(1).setLowScale()

fun parseDataToCalculate(data: DataFields): Calculate {
    val monthRate = data.rate.setBigScale() / (MONTHS * PERCENTS)
    val loanTerm = data.run {
        if (isMonths) loanTerm
        else (loanTerm * MONTHS)
    }
    if (data.isAnnuity) {
        val ratio = (ONE + monthRate).pow(loanTerm.toInt())
        val payment = (data.amount * (monthRate * ratio) / ((ratio) - ONE)).setLowScale()
        val totalPayment = (payment * loanTerm).setLowScale()
        val overPayment = totalPayment - data.amount
        return Calculate(payment.toString(), overPayment.toString(), totalPayment.toString())
    } else {
        var maximum = BigDecimal(0).setLowScale()
        var minimum = BigDecimal(1000000).setLowScale()
        val percentPayment: MutableList<BigDecimal> = mutableListOf()
        val basePayment = (data.amount / loanTerm).setLowScale()
        data.apply {
            val daysBeforeFirstPayment =
                (firstDate.time - System.currentTimeMillis()).toBigDecimal()
                    .div(MILLIS_IN_A_DAY)
                    .setScale(0)
            val dayRate = data.rate.setBigScale().div(DAYS_IN_YEAR).div(PERCENTS)
            percentPayment.add(
                (amount * daysBeforeFirstPayment * dayRate).setLowScale()
            )
            val daysCount = Calendar.getInstance()
            daysCount.time = firstDate
            daysCount.add(Calendar.DAY_OF_MONTH, daysBeforeFirstPayment.toInt())
            for (month in 1 until loanTerm.toInt()) {
                val date = daysCount.timeInMillis
                daysCount.add(Calendar.MONTH, 1)
                val delta = daysCount.timeInMillis - date
                val daysInMonth = delta / 86400000
                percentPayment.add(
                    ((amount - (basePayment.times(month.toBigDecimal())) - percentPayment(
                        percentPayment
                    )) * daysInMonth.toBigDecimal() * dayRate).setLowScale()
                )
            }
            for (item in percentPayment) {
                if (item > maximum) maximum = item
                else if (item < minimum) minimum = item
            }
        }
        return Calculate(
            (maximum + basePayment).toString() + "..." + (minimum + basePayment).toString(),
            percentPayment(percentPayment).toString(),
            ((basePayment.times(loanTerm)) + percentPayment(percentPayment)).toString()
        )
    }
}

fun percentPayment(percentPayment: MutableList<BigDecimal>): BigDecimal {
    var count = BigDecimal(0)
    for (item in percentPayment) count += item.setLowScale()
    return count
}

fun BigDecimal.setLowScale(): BigDecimal = setScale(LOW_SCALE, RoundingMode.HALF_UP)

fun BigDecimal.setBigScale(): BigDecimal = setScale(BIG_SCALE, RoundingMode.HALF_UP)
