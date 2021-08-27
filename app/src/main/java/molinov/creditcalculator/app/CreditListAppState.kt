package molinov.creditcalculator.app

import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataFieldsEntity

sealed class CreditListAppState {
    data class Success(val data: MutableList<Pair<DataFieldsEntity, List<Schedule>>>) :
        CreditListAppState()

    object Loading : CreditListAppState()
}
