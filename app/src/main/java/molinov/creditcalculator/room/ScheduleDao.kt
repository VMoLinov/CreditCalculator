package molinov.creditcalculator.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("DELETE FROM ScheduleEntity WHERE ownerId LIKE :id")
    fun deleteSchedule(id: Long)

    @Query("DELETE FROM DataFieldsEntity WHERE id LIKE :id")
    fun deleteDataFields(id: Long)

    @Transaction
    fun delete(id: Long) {
        deleteDataFields(id)
        deleteSchedule(id)
    }

    @Transaction
    fun insert(dataFields: DataFieldsEntity, list: List<Schedule>) {
        val id = insertDataFields(dataFields)
        insertSchedule(fromScheduleToEntity(list, id))
    }

    @Transaction
    @Query("SELECT * from DataFieldsEntity")
    fun getAll(): MutableList<ScheduleData>?
}
