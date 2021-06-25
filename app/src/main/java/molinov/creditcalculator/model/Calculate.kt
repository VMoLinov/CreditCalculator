package molinov.creditcalculator.model

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

@Parcelize
data class Calculate(
    val payment: String,
    val overPayment: String,
    val totalPayment: String
) : Parcelable

private const val US_TIME_FORMAT = "MM.dd.yyyy"
private const val CLASSIC_TIME_FORMAT = "dd.MM.yyyy"

@SuppressLint("SimpleDateFormat")
fun parseDataToCalculate(data: DataFields): Calculate {
//    val payment = data.run { amount * (rate / (1 + rate) - loanTerm - 1) }
    val monthRate = data.rate.div(12).div(100)
    val loanTerm = data.run { // convert ages to months if need
        if (isMonths) loanTerm
        else loanTerm * 12
    }
    if (data.isAnnuity) {
        val ratio = (1 + monthRate).pow(loanTerm.toDouble())
        val payment =
            (data.amount * (monthRate * ratio).div((ratio) - 1)).toBigDecimal().setLowScale()
        val totalPayment = payment.multiply(loanTerm.toBigDecimal()).setLowScale()
        val overPayment = totalPayment.minus(data.amount.toBigDecimal()).setLowScale()
        return Calculate(payment.toString(), overPayment.toString(), totalPayment.toString())
    } else {
        data.apply {
            val diff = (firstDate.time - System.currentTimeMillis()) / 1000
            val days = (diff / 86400).toInt()
            val 
        }
        return Calculate()
    }
}

fun BigDecimal.setLowScale(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
