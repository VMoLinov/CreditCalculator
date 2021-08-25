package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.room.ScheduleDao
import molinov.creditcalculator.room.ScheduleData

class LocalRepositoryImpl(
    private val localData: ScheduleDao
) : LocalRepository {
    override fun getAllData(): MutableList<ScheduleData>? {
        return localData.getAll()
    }

    override fun insert(data: DataEntity, list: List<Schedule>) {
        localData.insert(data, list)
    }

    override fun delete(id: Long) {
        localData.delete(id)
    }
}
