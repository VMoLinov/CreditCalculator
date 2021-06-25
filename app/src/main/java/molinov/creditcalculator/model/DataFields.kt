package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class DataFields(
    val firstDate: Date,
    val amount: Int,
    val loanTerm: Int,
    val rate: Double,
    val isMonths: Boolean,
    val isAnnuity: Boolean
) : Parcelable
