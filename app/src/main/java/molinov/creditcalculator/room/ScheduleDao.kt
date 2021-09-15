package molinov.creditcalculator.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.utils.fromScheduleToEntity

@Dao
interface ScheduleDao {

//    @Query("SELECT * FROM DataEntity")
//    fun all(): List<DataEntity>
//
//    @Query("SELECT * FROM DataEntity WHERE id LIKE :id")
//    fun getDataById(id: Int): List<ScheduleEntity>

    @Insert(onConflict = REPLACE)
    fun insertSchedule(entity: List<ScheduleEntity>)

    @Insert(onConflict = REPLACE)
    fun insertDataFields(entity: DataFieldsEntity): Long

    @Update(onConflict = REPLACE)
    fun updateSchedule(entity: List<ScheduleEntity>)

    @Update(onConflict = REPLACE)
    fun updateDataFields(entity: DataFieldsEntity): Int

    @Transaction
    fun update(
        from: Pair<DataFieldsEntity, List<Schedule>>,
        to: Pair<DataFieldsEntity, List<Schedule>>
    ) {
        val first = updateDataFields(from.first).toLong()
        val second = updateDataFields(to.first).toLong()
        deleteSchedule(first)
        deleteSchedule(second)
        updateSchedule(fromScheduleToEntity(from.second, first))
        updateSchedule(fromScheduleToEntity(to.second, second))
    }

    @Transaction
    fun delete(id: Long) {
        deleteDataFields(id)
        deleteSchedule(id)
    }

    @Query("DELETE FROM ScheduleEntity WHERE ownerId LIKE :id")
    fun deleteSchedule(id: Long)

    @Query("DELETE FROM DataFieldsEntity WHERE id LIKE :id")
    fun deleteDataFields(id: Long)

    @Transaction
    fun insert(dataFields: DataFieldsEntity, list: List<Schedule>) {
        val id = insertDataFields(dataFields)
        insertSchedule(fromScheduleToEntity(list, id))
    }

    @Transaction
    @Query("SELECT * from DataFieldsEntity")
    fun getAll(): MutableList<ScheduleData>?
}
