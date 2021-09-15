package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataFieldsEntity
import molinov.creditcalculator.room.ScheduleData

interface LocalRepository {

    fun getAllData(): MutableList<ScheduleData>?

    fun insert(dataFields: DataFieldsEntity, list: List<Schedule>)

    fun update(
        from: Pair<DataFieldsEntity, List<Schedule>>,
        to: Pair<DataFieldsEntity, List<Schedule>>
    )

    fun delete(id: Long)
}
