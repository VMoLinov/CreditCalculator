package molinov.creditcalculator.model

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

fun parseDataToCalculate(data: DataFields): Calculate {
//    val payment = data.run { amount * (rate / (1 + rate) - loanTerm - 1) }
    val monthRate = data.rate.div(12).div(100)
    val ratio = (1 + monthRate).pow(data.loanTerm.toDouble())
    val payment = (data.amount * (monthRate * ratio).div((ratio) - 1)).toBigDecimal().setLowScale()
    val totalPayment = payment.multiply(data.loanTerm.toBigDecimal()).setLowScale()
    val overPayment = totalPayment.minus(data.amount.toBigDecimal()).setLowScale()
    return Calculate(payment.toString(), overPayment.toString(), totalPayment.toString())
}

fun BigDecimal.setLowScale(): BigDecimal = setScale(2, RoundingMode.HALF_UP)
