package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.App.Companion.schedule_dao
import molinov.creditcalculator.app.CreditListAppState
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.repository.LocalRepository
import molinov.creditcalculator.repository.LocalRepositoryImpl
import molinov.creditcalculator.room.DataFieldsEntity
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

    fun update(
        from: Pair<DataFieldsEntity, List<Schedule>>,
        to: Pair<DataFieldsEntity, List<Schedule>>
    ) {
        Thread {
            creditListRepository.update(from, to)
        }.start()
    }

    fun delete(id: Long) {
        creditListRepository.delete(id)
    }
}
