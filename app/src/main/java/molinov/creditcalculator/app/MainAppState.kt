package molinov.creditcalculator.app

import molinov.creditcalculator.model.Calculate

sealed class MainAppState {
    data class Success(val data: Calculate) : MainAppState()
    object Loading : MainAppState()
}
