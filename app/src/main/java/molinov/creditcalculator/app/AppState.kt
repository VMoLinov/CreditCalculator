package molinov.creditcalculator.app

import molinov.creditcalculator.model.Schedule

sealed class AppState {
    data class Success(val data: Schedule) : AppState()
}
