package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataFieldsForUI(
    val firstDate: String,
    val amount: String,
    val loanTerm: String,
    val rate: String,
    val isMonths: Boolean,
    val isAnnuity: Boolean
) : Parcelable
