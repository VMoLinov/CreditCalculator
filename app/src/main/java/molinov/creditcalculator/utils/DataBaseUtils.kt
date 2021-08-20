package molinov.creditcalculator.utils

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.room.ScheduleData
import molinov.creditcalculator.room.ScheduleEntity

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

fun fromScheduleDataToPair(scheduleData: List<ScheduleData>?): MutableList<Pair<DataEntity, List<Schedule>>> {
    return if (scheduleData.isNullOrEmpty()) mutableListOf()
    else {
        val result: MutableList<Pair<DataEntity, List<Schedule>>> = mutableListOf()
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
