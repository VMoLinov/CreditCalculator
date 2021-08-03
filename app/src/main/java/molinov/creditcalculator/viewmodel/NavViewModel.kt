package molinov.creditcalculator.viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NavViewModel : ViewModel() {

    val bundleFromFragment = MutableLiveData<Bundle>()
}
