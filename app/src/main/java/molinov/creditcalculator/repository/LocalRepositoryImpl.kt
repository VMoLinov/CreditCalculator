package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataFieldsEntity
import molinov.creditcalculator.room.ScheduleDao
import molinov.creditcalculator.room.ScheduleData

class LocalRepositoryImpl(
    private val localData: ScheduleDao
) : LocalRepository {
    override fun getAllData(): MutableList<ScheduleData>? {
        return localData.getAll()
    }

    override fun insert(dataFields: DataFieldsEntity, list: List<Schedule>) {
        localData.insert(dataFields, list)
    }

    override fun update(
        from: Pair<DataFieldsEntity, List<Schedule>>,
        to: Pair<DataFieldsEntity, List<Schedule>>
    ) {
        val cash = from.first.id
        from.first.id = to.first.id
        to.first.id = cash
        localData.update(from, to)
    }

    override fun delete(id: Long) {
        localData.delete(id)
    }
}
