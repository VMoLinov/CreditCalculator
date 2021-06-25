package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class DataFields(
    private val firstDate: Date,
    private val amount: Int,
    private val loanTerm: Int,
    private val rate: Double
) : Parcelable {
}
