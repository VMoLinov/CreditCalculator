package molinov.creditcalculator.room

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class DataFieldsEntity(

    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String? = null,
    val amount: Float,
    val loanTerm: Float,
    val rate: Float,
    val isMonths: Boolean,
    val isAnnuity: Boolean,
    var isExpanded: Boolean = false
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
    @Embedded val dataList: DataFieldsEntity,
    @Relation(parentColumn = "id", entityColumn = "ownerId")
    val schedule: List<ScheduleEntity>
)
