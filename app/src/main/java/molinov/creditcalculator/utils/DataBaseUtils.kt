package molinov.creditcalculator.utils

import android.view.View
import molinov.creditcalculator.R
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataFieldsEntity
import molinov.creditcalculator.room.ScheduleData
import molinov.creditcalculator.room.ScheduleEntity
import java.math.BigDecimal
import java.util.*

fun fromScheduleToEntity(schedule: List<Schedule>, index: Long): List<ScheduleEntity> {
    val result: MutableList<ScheduleEntity> = mutableListOf()
    schedule.iterator().forEach {
        result.add(
            ScheduleEntity(
                0,
                index,
                it.type,
                it.date,
                it.payment,
                it.mainDebt,
                it.percent,
                it.balance
            )
        )
    }
    return result
}

fun fromDataToEntity(data: DataFields, name: String): DataFieldsEntity {
    data.apply {
        return DataFieldsEntity(
            0,
            name,
            amount.toFloat(),
            loanTerm.toFloat(),
            rate.toFloat(),
            isMonths,
            isAnnuity
        )
    }
}

fun fromScheduleDataToPair(scheduleData: List<ScheduleData>?): MutableList<Pair<DataFieldsEntity, List<Schedule>>> {
    return if (scheduleData.isNullOrEmpty()) mutableListOf()
    else {
        val result: MutableList<Pair<DataFieldsEntity, List<Schedule>>> = mutableListOf()
        val schedule: MutableList<Schedule> = mutableListOf()
        scheduleData.iterator().forEach {
            it.schedule.iterator().forEach { e ->
                schedule.add(Schedule(e.type, e.date, e.payment, e.mainDebt, e.percent, e.balance))
            }
            result.add(Pair(it.dataList, schedule.toList()))
            schedule.clear()
        }
        result
    }
}

fun fromEntityToDataFields(data: DataFieldsEntity): DataFields {
    data.apply {
        return DataFields(
            Date(System.currentTimeMillis()),
            amount.toBigDecimal().setScale(0),
            loanTerm.toBigDecimal().setScale(0),
            if (rate.toString().endsWith(".0")) rate.toBigDecimal().setScale(0)
            else rate.toBigDecimal(),
            isMonths,
            isAnnuity
        )
    }
}

fun formattedYears(term: BigDecimal, view: View): String {
    val s = term.toString()
    return if (s.endsWith("11") || s.endsWith("12") || s.endsWith("13") || s.endsWith("14")
    ) view.resources.getString(R.string.years)
    else if (s.endsWith("1")) view.resources.getString(R.string.years_1)
    else if (s.endsWith("2") || s.endsWith("3") || s.endsWith("4")) view.resources.getString(R.string.years_2)
    else view.resources.getString(R.string.years)
}
