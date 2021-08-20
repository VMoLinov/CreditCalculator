package molinov.creditcalculator.app

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataEntity

sealed class CreditListAppState {
    data class Success(val data: MutableList<Pair<DataEntity, List<Schedule>>>) : CreditListAppState()
    object Loading : CreditListAppState()
}
