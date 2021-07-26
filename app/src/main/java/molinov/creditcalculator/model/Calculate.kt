package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.pow

@Parcelize
data class Calculate(
    val payment: String,
    val overPayment: String,
    val totalPayment: String
) : Parcelable

private const val US_TIME_FORMAT = "MM.dd.yyyy"
private const val CLASSIC_TIME_FORMAT = "dd.MM.yyyy"

fun parseDataToCalculate(data: DataFields): Calculate {
//    val payment = data.run { amount * (rate / (1 + rate) - loanTerm - 1) }
    val monthRate = data.rate.div(12).div(100)
    val loanTerm = data.run {
        // convert ages to months if need
        if (isMonths) loanTerm
        else (loanTerm * 12)
    }
    if (data.isAnnuity) {
        val ratio = (1 + monthRate).pow(loanTerm.toDouble())
        val payment =
            (data.amount * (monthRate * ratio).div((ratio) - 1)).toBigDecimal().setLowScale()
        val totalPayment = payment.multiply(loanTerm.toBigDecimal()).setLowScale()
        val overPayment = totalPayment.minus(data.amount.toBigDecimal()).setLowScale()
        return Calculate(payment.toString(), overPayment.toString(), totalPayment.toString())
    } else {
        var maximum = BigDecimal(0).setLowScale()
        var minimum = BigDecimal(1000000).setLowScale()
        val percentPayment: MutableList<BigDecimal> = mutableListOf()
        val basePayment = (data.amount / loanTerm).toBigDecimal().setLowScale()
        data.apply {
            val diff = (firstDate.time - System.currentTimeMillis()) / 1000
            val daysBeforeFirstPayment = (diff / 86400).toInt()
            val dayRate = data.rate.div(365).div(100)
            percentPayment.add(
                (amount * daysBeforeFirstPayment * dayRate).toBigDecimal().setLowScale()
            )
            val daysCount = Calendar.getInstance()
            daysCount.time = firstDate
            daysCount.add(Calendar.DAY_OF_MONTH, daysBeforeFirstPayment)
            for (month in 1 until loanTerm) {
                val date = daysCount.timeInMillis
                daysCount.add(Calendar.MONTH, 1)
                val delta = daysCount.timeInMillis - date
                val daysInMonth = delta / 86400000
                percentPayment.add(
                    ((amount.toBigDecimal() - (basePayment.times(month.toBigDecimal())) - percentPayment(
                        percentPayment
                    )) * daysInMonth.toBigDecimal() * dayRate.toBigDecimal()).setLowScale()
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
            ((basePayment.times(loanTerm.toBigDecimal())) + percentPayment(percentPayment)).toString()
        )
    }
}

fun percentPayment(percentPayment: MutableList<BigDecimal>): BigDecimal {
    var count = BigDecimal(0)
    for (item in percentPayment) count += item.setLowScale()
    return count
}

fun BigDecimal.setLowScale(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
