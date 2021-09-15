package molinov.creditcalculator.view.creditslist

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import molinov.creditcalculator.MainActivity
import molinov.creditcalculator.R
import molinov.creditcalculator.databinding.CreditListFragmentBinding
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
    private val adapter: CreditListAdapter by lazy { CreditListAdapter(creditListViewModel) }
    private lateinit var itemTouchHelper: ItemTouchHelper


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreditListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.creditListFragmentRecycleView.adapter = adapter
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.creditListFragmentRecycleView)
        creditListViewModel.creditListLiveData.observe(viewLifecycleOwner, { adapter.setData(it) })
        creditListViewModel.getSchedule()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        (activity as MainActivity).selectedItem = R.id.credit_list_fragment
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        super.onResume()
    }

    override fun onPause() {
        navViewModel.navLiveData.value = adapter
        super.onPause()
    }
}
