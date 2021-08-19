package molinov.creditcalculator.view.creditslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import molinov.creditcalculator.R
import molinov.creditcalculator.databinding.CreditListFragmentBinding
import molinov.creditcalculator.view.schedule.ScheduleAdapter
import molinov.creditcalculator.viewmodel.CreditListViewModel

class CreditListFragment : Fragment() {

    private var _binding: CreditListFragmentBinding? = null
    private val binding get() = _binding!!
    private val creditListViewModel: CreditListViewModel by lazy {
        ViewModelProvider(this).get(CreditListViewModel::class.java)
    }
    private val navViewModel: CreditListViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
//    private val adapter: CreditListAdapter by lazy { CreditListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreditListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
