package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.room.ScheduleData

interface LocalRepository {

    fun getAllData(): MutableList<ScheduleData>?

    fun insert(data: DataEntity, list: List<Schedule>)

    fun delete(id: Long)
}
