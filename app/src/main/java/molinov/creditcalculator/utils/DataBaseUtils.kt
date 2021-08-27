package molinov.creditcalculator.utils

import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.model.setLowScale
import molinov.creditcalculator.room.DataFieldsEntity
import molinov.creditcalculator.room.ScheduleData
import molinov.creditcalculator.room.ScheduleEntity
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
            amount.toBigDecimal().setLowScale(),
            loanTerm.toBigDecimal().setLowScale(),
            rate.toBigDecimal().setLowScale(),
            isMonths,
            isAnnuity
        )
    }
}
