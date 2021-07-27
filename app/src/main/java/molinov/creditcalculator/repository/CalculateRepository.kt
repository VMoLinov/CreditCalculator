package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Calculate
import molinov.creditcalculator.model.DataFields

interface CalculateRepository {

    fun getCalculateFromData(data: DataFields): Calculate
}