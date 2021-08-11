package molinov.creditcalculator.repository

import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.room.ScheduleDao
import molinov.creditcalculator.room.ScheduleData
import molinov.creditcalculator.room.ScheduleEntity

class LocalRepositoryImpl(
    private val localData: ScheduleDao
): LocalRepository {
    override fun getAllData(): List<ScheduleData>? {
        return localData.getAll()
    }

    override fun saveData(data: DataEntity): Long {
        return localData.insertData(data)
    }

    override fun saveSchedule(schedule: List<ScheduleEntity>) {
        localData.insertSchedule(schedule)
    }
}
