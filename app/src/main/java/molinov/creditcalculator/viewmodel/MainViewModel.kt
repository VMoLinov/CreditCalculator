package molinov.creditcalculator.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.MainAppState
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.repository.CalculateRepositoryImpl

class MainViewModel(
    val mainLiveData: MutableLiveData<MainAppState> = MutableLiveData(),
    val navLiveData: MutableLiveData<Bundle> = MutableLiveData(),
    private val calculateRepository: CalculateRepositoryImpl = CalculateRepositoryImpl()
) : ViewModel() {

    fun getCalculate(data: DataFields) {
        mainLiveData.value = MainAppState.Loading
        Thread {
            mainLiveData.postValue(
                MainAppState.Success(calculateRepository.getCalculateFromData(data))
            )
        }.start()
    }
}
