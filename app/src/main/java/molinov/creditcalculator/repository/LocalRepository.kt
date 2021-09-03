package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataFieldsEntity
import molinov.creditcalculator.room.ScheduleData

interface LocalRepository {

    fun getAllData(): MutableList<ScheduleData>?

    fun insert(dataFields: DataFieldsEntity, list: List<Schedule>)

    fun delete(id: Long)
}
