package molinov.creditcalculator.app

import molinov.creditcalculator.model.Calculate

sealed class AppState {
    data class Success(val data: Calculate) : AppState()
    object Loading : AppState()
}
