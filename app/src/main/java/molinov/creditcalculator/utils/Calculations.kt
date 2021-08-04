package molinov.creditcalculator.model

import molinov.creditcalculator.view.schedule.ScheduleAdapter.Companion.TYPE_HEADER
import molinov.creditcalculator.view.schedule.ScheduleAdapter.Companion.TYPE_MAIN
import molinov.creditcalculator.view.schedule.ScheduleAdapter.Companion.TYPE_TOTAL
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
    return if (data.isAnnuity)
        calculateAnnuity(data.amount, monthRate, loanTerm)
    else
        calculateDifferentiate(data.amount, monthRate, loanTerm)
}

fun calculateAnnuity(amount: BigDecimal, monthRate: BigDecimal, loanTerm: BigDecimal): Calculate {
    val payment = getAnnualPayment(monthRate, loanTerm, amount)
    val totalPayment = payment * loanTerm
    val overPayment = totalPayment - amount
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
    val payment = (max + basePayment).setLowScale()
        .toString() + " ... " + (min + basePayment).setLowScale().toString()
    val overPayment = percentPaymentSummary(percentPaymentList)
    val totalPayment = (amount + overPayment).setLowScale()
    return Calculate(payment, overPayment.toString(), totalPayment.toString())
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
    result.add(Schedule(TYPE_HEADER, "", "", "", "", ""))
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
    result.add(Schedule(TYPE_HEADER, "", "", "", "", ""))
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

fun BigDecimal.setLowScale(): BigDecimal = setScale(LOW_SCALE, ROUNDING)

fun BigDecimal.setBigScale(): BigDecimal = setScale(BIG_SCALE, ROUNDING)
