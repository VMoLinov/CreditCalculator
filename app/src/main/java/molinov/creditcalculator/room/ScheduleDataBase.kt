package molinov.creditcalculator.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DataFieldsEntity::class, ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ScheduleDataBase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao
}
