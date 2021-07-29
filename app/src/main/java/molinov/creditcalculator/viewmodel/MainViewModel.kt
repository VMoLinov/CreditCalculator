package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.MainFragmentAppState
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.repository.CalculateRepositoryImpl

class MainViewModel(
    val mainLiveData: MutableLiveData<MainFragmentAppState> = MutableLiveData(),
    private val calculateRepository: CalculateRepositoryImpl = CalculateRepositoryImpl()
) : ViewModel() {

    fun calculate(data: DataFields) {
        mainLiveData.value = MainFragmentAppState.Loading
        Thread {
            mainLiveData.postValue(MainFragmentAppState.Success(calculateRepository.getCalculateFromData(data)))
        }.start()
    }
}
