package molinov.creditcalculator.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DataEntity(

    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String?
)

@Entity
data class ScheduleEntity(

    @PrimaryKey(autoGenerate = true) val id: Long,
    val ownerId: Long,
    val type: Int,
    val date: String,
    val payment: String,
    val mainDebt: String,
    val percent: String,
    val balance: String
)

data class ScheduleData(
    @Embedded val dataList: DataEntity,
    @Relation(parentColumn = "id", entityColumn = "ownerId")
    val schedule: List<ScheduleEntity>
)
