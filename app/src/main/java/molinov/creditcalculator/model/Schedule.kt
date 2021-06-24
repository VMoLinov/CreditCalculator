package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Schedule(
    val pos: Int,
    val amount: Int
) : Parcelable
