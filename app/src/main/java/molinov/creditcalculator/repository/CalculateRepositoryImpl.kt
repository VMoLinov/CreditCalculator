package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Calculate
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.model.parseDataToCalculate

class CalculateRepositoryImpl : CalculateRepository {

    override fun getCalculateFromData(data: DataFields): Calculate = parseDataToCalculate(data)
}