package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.App
import molinov.creditcalculator.app.CreditListAppState
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.repository.LocalRepository
import molinov.creditcalculator.repository.LocalRepositoryImpl
import molinov.creditcalculator.utils.fromScheduleDataToPair

class CreditListViewModel(
    val creditListLiveData: MutableLiveData<CreditListAppState> = MutableLiveData(),
    val navLiveData: MutableLiveData<DataFields> = MutableLiveData(),
    private val creditListRepository: LocalRepository = LocalRepositoryImpl(App.schedule_dao),
) : ViewModel() {

    fun getSchedule() {
        creditListLiveData.value = CreditListAppState.Loading
        Thread {
            creditListLiveData.postValue(
                CreditListAppState.Success(fromScheduleDataToPair(creditListRepository.getAllData()))
            )
        }.start()
    }
}
