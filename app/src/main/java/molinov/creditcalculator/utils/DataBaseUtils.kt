package molinov.creditcalculator.utils

import molinov.creditcalculator.model.Schedule
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

fun fromScheduleDataToPair(scheduleData: List<ScheduleData>?): List<Pair<Long, List<Schedule>>>? {
    return if (scheduleData.isNullOrEmpty()) null
    else {
        val result: MutableList<Pair<Long, List<Schedule>>> = mutableListOf()
        val schedule: MutableList<Schedule> = mutableListOf()
        scheduleData.iterator().forEach {
            it.schedule.iterator().forEach { e ->
                schedule.add(Schedule(e.type, e.date, e.payment, e.mainDebt, e.percent, e.balance))
            }
            result.add(Pair(it.dataList.id, schedule.toList()))
            schedule.clear()
        }
        result
    }
}
