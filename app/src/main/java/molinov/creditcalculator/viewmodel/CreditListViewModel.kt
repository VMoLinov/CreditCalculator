package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.App.Companion.schedule_dao
import molinov.creditcalculator.app.CreditListAppState
import molinov.creditcalculator.repository.LocalRepository
import molinov.creditcalculator.repository.LocalRepositoryImpl
import molinov.creditcalculator.utils.fromScheduleDataToPair
import molinov.creditcalculator.view.creditslist.CreditListAdapter

class CreditListViewModel(
    val creditListLiveData: MutableLiveData<CreditListAppState> = MutableLiveData(),
    val navLiveData: MutableLiveData<CreditListAdapter> = MutableLiveData(),
    private val creditListRepository: LocalRepository = LocalRepositoryImpl(schedule_dao),
) : ViewModel() {

    fun getSchedule() {
        creditListLiveData.value = CreditListAppState.Loading
        Thread {
            creditListLiveData.postValue(
                CreditListAppState.Success(fromScheduleDataToPair(creditListRepository.getAllData()))
            )
        }.start()
    }

    fun delete(id: Long) {
        creditListRepository.delete(id)
    }
}
