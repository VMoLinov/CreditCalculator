package molinov.creditcalculator.model

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

private const val LOW_SCALE = 2
private const val BIG_SCALE = 10
private val ROUNDING = RoundingMode.HALF_UP
private val MONTHS = BigDecimal(12, MathContext(LOW_SCALE, ROUNDING))
private val PERCENTS = BigDecimal(100, MathContext(LOW_SCALE, ROUNDING))
private val DAYS_IN_YEAR = BigDecimal(365, MathContext(LOW_SCALE, ROUNDING))
private val MILLIS_IN_A_DAY = BigDecimal(86400000, MathContext(LOW_SCALE, ROUNDING))
private val ONE = BigDecimal(1, MathContext(LOW_SCALE, ROUNDING))

fun parseDataFieldsToCalculate(data: DataFields): Calculate {
    val monthRate = data.rate.setBigScale() / (MONTHS * PERCENTS)
    val loanTerm = data.run {
        if (isMonths) loanTerm
        else loanTerm * MONTHS
    }
    return if (data.isAnnuity) {
        calculateAnnuity(data, monthRate, loanTerm)
    } else {
        calculateDifferentiated(data, loanTerm)
    }
}

fun calculateAnnuity(data: DataFields, monthRate: BigDecimal, loanTerm: BigDecimal): Calculate {
    val payment = getAnnualPayment(monthRate, loanTerm, data.amount)
    val totalPayment = payment * loanTerm
    val overPayment = totalPayment - data.amount
    return Calculate(
        payment.toString(),
        overPayment.setLowScale().toString(),
        totalPayment.setLowScale().toString()
    )
}

fun getAnnualPayment(monthRate: BigDecimal, loanTerm: BigDecimal, amount: BigDecimal): BigDecimal {
    val ratio = (ONE + monthRate).pow(loanTerm.toInt())
    return (amount * (monthRate * ratio)).divide(ratio - ONE, LOW_SCALE, ROUNDING)
}

fun calculateDifferentiated(data: DataFields, loanTerm: BigDecimal): Calculate {
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

fun parseDataFieldsToSchedule(data: DataFields): List<Schedule> {
    val monthRate = data.rate.setBigScale() / (MONTHS * PERCENTS)
    val loanTerm = data.run {
        if (isMonths) loanTerm
        else loanTerm * MONTHS
    }
    return if (data.isAnnuity) {
        scheduleAnnuity(monthRate, loanTerm, data.amount, data.firstDate)
    } else listOf()
}

fun scheduleAnnuity(
    monthRate: BigDecimal, loanTerm: BigDecimal, amount: BigDecimal, firstDate: Date
): List<Schedule> {
    val result: MutableList<Schedule> = mutableListOf()
    var payment = getAnnualPayment(monthRate, loanTerm, amount)
    var percent = monthRate * amount
    var mainDebt = payment - percent
    var balance = amount - mainDebt
    result.add(
        Schedule(
            parseLongDateToString(firstDate.time),
            payment.setLowScale().toString(),
            mainDebt.setLowScale().toString(),
            percent.setLowScale().toString(),
            balance.setScale(0, ROUNDING).toString()
        )
    )
    val daysCount = Calendar.getInstance()
    daysCount.time = firstDate
    for (month in 1 until loanTerm.toInt()) {
        daysCount.add(Calendar.MONTH, 1)
        percent = monthRate * balance
        if (month == loanTerm.toInt() - 1) payment = balance + percent
        mainDebt = payment - percent
        balance -= mainDebt
        result.add(
            Schedule(
                parseLongDateToString(daysCount.timeInMillis),
                payment.setLowScale().toString(),
                mainDebt.setLowScale().toString(),
                percent.setLowScale().toString(),
                balance.setScale(0, ROUNDING).toString()
            )
        )
    }
    return result
}

fun percentPaymentSummary(percentPayment: MutableList<BigDecimal>): BigDecimal {
    var count = BigDecimal(0, MathContext(LOW_SCALE, ROUNDING))
    for (item in percentPayment) count += item.setLowScale()
    return count
}

fun BigDecimal.setLowScale(): BigDecimal = setScale(LOW_SCALE, ROUNDING)

fun BigDecimal.setBigScale(): BigDecimal = setScale(BIG_SCALE, ROUNDING)
