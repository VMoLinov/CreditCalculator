package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Calculate(
    val payment: String,
    val overPayment: String,
    val totalPayment: String
) : Parcelable

private const val LOW_SCALE = 2
private const val BIG_SCALE = 10
private val ROUNDING = RoundingMode.HALF_UP
private val MONTHS = BigDecimal(12, MathContext(LOW_SCALE, ROUNDING))
private val PERCENTS = BigDecimal(100, MathContext(LOW_SCALE, ROUNDING))
private val DAYS_IN_YEAR = BigDecimal(365, MathContext(LOW_SCALE, ROUNDING))
private val MILLIS_IN_A_DAY = BigDecimal(86400000, MathContext(LOW_SCALE, ROUNDING))
private val ONE = BigDecimal(1, MathContext(LOW_SCALE, ROUNDING))

fun parseDataToCalculate(data: DataFields): Calculate {
    val monthRate = data.rate.setBigScale() / (MONTHS * PERCENTS)
    val loanTerm = data.run {
        if (isMonths) loanTerm
        else (loanTerm * MONTHS)
    }
    return if (data.isAnnuity) {
        computeAnnuity(data, monthRate, loanTerm)
    } else {
        computeDifferentiated(data, loanTerm)
    }
}

fun computeAnnuity(data: DataFields, monthRate: BigDecimal, loanTerm: BigDecimal): Calculate {
    val ratio = (ONE + monthRate).pow(loanTerm.toInt())
    val payment =
        (data.amount * (monthRate * ratio))
            .divide(ratio - ONE, LOW_SCALE, ROUNDING)
    val totalPayment = payment * loanTerm
    val overPayment = totalPayment - data.amount
    return Calculate(
        payment.toString(),
        overPayment.setLowScale().toString(),
        totalPayment.setLowScale().toString()
    )
}

fun computeDifferentiated(
    data: DataFields,
    loanTerm: BigDecimal
): Calculate {
    val percentPaymentList: MutableList<BigDecimal> = mutableListOf()
    val basePayment = data.amount.divide(loanTerm, BIG_SCALE, ROUNDING)
    data.apply {
        val daysBeforeFirstPayment =
            firstDate.time.minus(System.currentTimeMillis())
                .toBigDecimal()
                .divide(MILLIS_IN_A_DAY, 0, ROUNDING)
        val dayRate = data.rate.setBigScale()
            .divide(DAYS_IN_YEAR * PERCENTS, BIG_SCALE, ROUNDING)
        percentPaymentList.add((amount * daysBeforeFirstPayment * dayRate).setLowScale())
        val daysCount = Calendar.getInstance()
        daysCount.time = firstDate
        for (month in 1 until loanTerm.toInt()) {
            val date = daysCount.timeInMillis
            daysCount.add(Calendar.MONTH, 1)
            val delta = daysCount.timeInMillis - date
            val daysInMonth = delta.toBigDecimal() / MILLIS_IN_A_DAY
            percentPaymentList.add(
                ((amount - (basePayment.times(month.toBigDecimal()))
                        - percentPaymentSummary(percentPaymentList))
                        * daysInMonth * dayRate).setLowScale()
            )
        }
    }
    val maximum = percentPaymentList.maxOrNull() ?: 0
        .toBigDecimal()
    val minimum = percentPaymentList.minOrNull() ?: 0
        .toBigDecimal()
    return Calculate(
        (maximum + basePayment).setLowScale().toString()
                + " ... " + (minimum + basePayment).setLowScale().toString(),
        percentPaymentSummary(percentPaymentList).toString(),
        ((basePayment * loanTerm) + percentPaymentSummary(percentPaymentList)).setLowScale()
            .toString()
    )
}

fun percentPaymentSummary(percentPayment: MutableList<BigDecimal>): BigDecimal {
    var count = BigDecimal(0, MathContext(LOW_SCALE, ROUNDING))
    for (item in percentPayment) count += item.setLowScale()
    return count
}

fun BigDecimal.setLowScale(): BigDecimal = setScale(LOW_SCALE, ROUNDING)

fun BigDecimal.setBigScale(): BigDecimal = setScale(BIG_SCALE, ROUNDING)
