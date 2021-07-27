package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.util.*

@Parcelize
data class DataFields(
    val firstDate: Date,
    val amount: BigDecimal,
    val loanTerm: BigDecimal,
    val rate: BigDecimal,
    val isMonths: Boolean,
    val isAnnuity: Boolean
) : Parcelable
