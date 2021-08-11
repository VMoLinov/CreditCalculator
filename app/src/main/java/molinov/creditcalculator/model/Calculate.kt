package molinov.creditcalculator.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Calculate(
    val payment: String,
    val overPayment: String,
    val totalPayment: String
) : Parcelable
