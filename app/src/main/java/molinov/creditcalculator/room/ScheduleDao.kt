package molinov.creditcalculator.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE

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

//    @Delete
//    fun delete(entity: ScheduleEntity)

    @Transaction
    @Query("SELECT * from DataEntity")
    fun getAll(): List<ScheduleData>?
}
