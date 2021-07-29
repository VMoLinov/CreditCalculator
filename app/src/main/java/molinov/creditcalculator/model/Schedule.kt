package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Schedule(
    val number: Int,
    val date: Date,
    val payment: String,
    val mainDebt: String,
    val percent: String,
    val balance: String
) : Parcelable
