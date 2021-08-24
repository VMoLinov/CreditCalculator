package molinov.creditcalculator.repository

import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.room.ScheduleData
import molinov.creditcalculator.room.ScheduleEntity

interface LocalRepository {

    fun getAllData(): MutableList<ScheduleData>?

    fun saveData(data: DataEntity): Long

    fun saveSchedule(schedule: List<ScheduleEntity>)
}
