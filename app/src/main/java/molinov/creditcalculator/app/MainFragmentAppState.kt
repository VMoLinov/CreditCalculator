package molinov.creditcalculator.app

import molinov.creditcalculator.model.Calculate

sealed class MainFragmentAppState {
    data class Success(val data: Calculate) : MainFragmentAppState()
    object Loading : MainFragmentAppState()
}
