package molinov.creditcalculator.model

import molinov.creditcalculator.view.schedule.ScheduleAdapter.Companion.TYPE_MAIN
import molinov.creditcalculator.view.schedule.ScheduleAdapter.Companion.TYPE_TOTAL
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

private const val LOW_SCALE = 2
private const val BIG_SCALE = 10
private const val DECIMAL_FORMAT = "###,###.##"
private val ROUNDING = RoundingMode.HALF_UP
private val MONTHS = BigDecimal(12, MathContext(LOW_SCALE, ROUNDING))
private val PERCENTS = BigDecimal(100, MathContext(LOW_SCALE, ROUNDING))
private val ONE = BigDecimal(1, MathContext(LOW_SCALE, ROUNDING))

fun parseDataFieldsToCalculate(data: DataFields): Calculate {
    val monthRate = data.rate.setBigScale() / (MONTHS * PERCENTS)
    val loanTerm = data.run {
        if (isMonths) loanTerm
        else loanTerm * MONTHS
    }
    return if (data.isAnnuity)
        calculateAnnuity(data.amount, monthRate, loanTerm)
    else
        calculateDifferentiate(data.amount, monthRate, loanTerm)
}

fun getFormattedNumber(payment: String): String {
    val dfs = DecimalFormatSymbols.getInstance()
    dfs.groupingSeparator = ' '
    val df = DecimalFormat(DECIMAL_FORMAT, dfs)
    return payment.replaceRange(0, payment.length, df.format(payment.toDouble()))
}

fun calculateAnnuity(amount: BigDecimal, monthRate: BigDecimal, loanTerm: BigDecimal): Calculate {
    val payment = getAnnualPayment(monthRate, loanTerm, amount)
    val totalPayment = payment * loanTerm
    val overPayment = totalPayment - amount
    return Calculate(
        getFormattedNumber(payment.toString()),
        getFormattedNumber(overPayment.setLowScale().toString()),
        getFormattedNumber(totalPayment.setLowScale().toString())
    )
}

fun getAnnualPayment(monthRate: BigDecimal, loanTerm: BigDecimal, amount: BigDecimal): BigDecimal {
    val ratio = (ONE + monthRate).pow(loanTerm.toInt())
    return (amount * (monthRate * ratio)).divide(ratio - ONE, LOW_SCALE, ROUNDING)
}

fun calculateDifferentiate(
    amount: BigDecimal, monthRate: BigDecimal, loanTerm: BigDecimal
): Calculate {
    val percentPaymentList: MutableList<BigDecimal> = mutableListOf()
    val basePayment = amount.divide(loanTerm, BIG_SCALE, ROUNDING)
    var percent: BigDecimal
    var balance = amount
    for (i in 0 until loanTerm.toInt()) {
        percent = monthRate * balance
        percentPaymentList.add(percent)
        balance -= basePayment
    }
    val max = percentPaymentList.maxOrNull() ?: BigDecimal(0)
    val min = percentPaymentList.minOrNull() ?: BigDecimal(0)
    val payment = getFormattedNumber(
        (max + basePayment).setLowScale().toString()
    ) + " ... " + getFormattedNumber((min + basePayment).setLowScale().toString())
    val overPayment = percentPaymentSummary(percentPaymentList)
    val totalPayment = getFormattedNumber((amount + overPayment).setLowScale().toString())
    return Calculate(payment, getFormattedNumber(overPayment.toString()), totalPayment)
}

fun parseDataFieldsToSchedule(data: DataFields): List<Schedule> {
    val monthRate = data.rate.setBigScale() / (MONTHS * PERCENTS)
    val loanTerm = data.run {
        if (isMonths) loanTerm
        else loanTerm * MONTHS
    }
    return if (data.isAnnuity)
        scheduleAnnuity(monthRate, loanTerm, data.amount, data.firstDate)
    else
        scheduleDifferentiate(monthRate, loanTerm, data.amount, data.firstDate)
}

fun scheduleAnnuity(
    monthRate: BigDecimal, loanTerm: BigDecimal, amount: BigDecimal, firstDate: Date
): List<Schedule> {
    val result: MutableList<Schedule> = mutableListOf()
    var payment = getAnnualPayment(monthRate, loanTerm, amount)
    var balance = amount
    var percent: BigDecimal
    var mainDebt: BigDecimal
    var paymentsCount = BigDecimal(0)
    val daysCount = Calendar.getInstance()
    daysCount.time = firstDate
    daysCount.add(Calendar.MONTH, -1)
    for (month in 0 until loanTerm.toInt()) {
        daysCount.add(Calendar.MONTH, 1)
        percent = monthRate * balance
        if (month == loanTerm.toInt() - 1) payment = balance + percent
        mainDebt = payment - percent
        balance -= mainDebt
        paymentsCount += payment
        result.add(
            Schedule(
                TYPE_MAIN,
                parseLongDateToString(daysCount.timeInMillis),
                payment.setLowScale().toString(),
                mainDebt.setLowScale().toString(),
                percent.setLowScale().toString(),
                balance.setLowScale().toString()
            )
        )
    }
    val percentsCount = (paymentsCount - amount).setLowScale().toString()
    val totalPayments = paymentsCount.setLowScale().toString()
    result.add(
        Schedule(
            TYPE_TOTAL, "", totalPayments, amount.setLowScale().toString(), percentsCount, ""
        )
    )
    return result
}

fun scheduleDifferentiate(
    monthRate: BigDecimal, loanTerm: BigDecimal, amount: BigDecimal, firstDate: Date
): List<Schedule> {
    val result: MutableList<Schedule> = mutableListOf()
    var percent: BigDecimal
    var payment: BigDecimal
    var mainDebt = amount.divide(loanTerm, BIG_SCALE, ROUNDING)
    var balance = amount
    var paymentsCount = BigDecimal(0)
    val daysCount = Calendar.getInstance()
    daysCount.time = firstDate
    daysCount.add(Calendar.MONTH, -1)
    for (month in 0 until loanTerm.toInt()) {
        daysCount.add(Calendar.MONTH, 1)
        percent = monthRate * balance
        if (month == loanTerm.toInt() - 1) mainDebt = balance
        payment = mainDebt + percent
        balance -= mainDebt
        paymentsCount += payment
        result.add(
            Schedule(
                TYPE_MAIN,
                parseLongDateToString(daysCount.timeInMillis),
                payment.setLowScale().toString(),
                mainDebt.setLowScale().toString(),
                percent.setLowScale().toString(),
                balance.setLowScale().toString()
            )
        )
    }
    val percentsCount = (paymentsCount - amount).setLowScale().toString()
    val totalPayments = paymentsCount.setLowScale().toString()
    result.add(
        Schedule(
            TYPE_TOTAL,
            "",
            totalPayments,
            amount.setLowScale().toString(),
            percentsCount,
            ""
        )
    )
    return result
}

fun percentPaymentSummary(percentPayment: MutableList<BigDecimal>): BigDecimal {
    var count = BigDecimal(0, MathContext(LOW_SCALE, ROUNDING))
    for (item in percentPayment) count += item.setLowScale()
    return count
}

fun paymentFromSchedule(data: List<Schedule>): String {
    return if (data[0].payment != data[1].payment) data[0].payment + " ... " + data[data.size - 2].payment
    else data[0].payment
}

fun BigDecimal.setLowScale(): BigDecimal = setScale(LOW_SCALE, ROUNDING)

fun BigDecimal.setBigScale(): BigDecimal = setScale(BIG_SCALE, ROUNDING)
