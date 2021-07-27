package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.AppState
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.repository.CalculateRepositoryImpl

class MainViewModel(
    val mainLiveData: MutableLiveData<AppState> = MutableLiveData(),
    private val calculateRepository: CalculateRepositoryImpl = CalculateRepositoryImpl()
) : ViewModel() {

    fun calculate(data: DataFields) {
        mainLiveData.value = AppState.Loading
        Thread {
            mainLiveData.postValue(AppState.Success(calculateRepository.getCalculateFromData(data)))
        }.start()
    }
}
