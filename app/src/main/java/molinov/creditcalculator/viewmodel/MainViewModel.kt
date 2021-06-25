package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.AppState
import molinov.creditcalculator.model.DataFields

class MainViewModel(
    val mainLiveData: MutableLiveData<AppState> = MutableLiveData()
) : ViewModel() {

    fun calculate(data: DataFields) {
        mainLiveData.value = AppState.Loading
        Thread {
            mainLiveData.postValue(AppState.Success())
        }.start()
    }
}
