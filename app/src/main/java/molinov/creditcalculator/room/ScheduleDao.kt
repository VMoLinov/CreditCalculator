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
    fun insertData(entity: DataEntity): Long

    @Insert(onConflict = REPLACE)
    fun insertSchedule(entity: List<ScheduleEntity>)

    @Query("DELETE FROM DataEntity WHERE id LIKE :id")
    fun deleteData(id: Long)

    @Query("DELETE FROM ScheduleEntity WHERE ownerId LIKE :id")
    fun deleteSchedule(id: Long)

    @Transaction
    fun delete(id: Long) {
        deleteData(id)
        deleteSchedule(id)
    }

    @Transaction
    fun insert(data: DataEntity, list: List<Schedule>) {
        val id = insertData(data)
        insertSchedule(fromScheduleToEntity(list, id))
    }

    @Transaction
    @Query("SELECT * from DataEntity")
    fun getAll(): MutableList<ScheduleData>?
}
