package molinov.creditcalculator.view.creditslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
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
    private val adapter: CreditListAdapter by lazy {
        CreditListAdapter(
            object : OnListItemClickListener {
                override fun onItemClick() {
                    Toast.makeText(context, "click", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreditListFragmentBinding.inflate(inflater, container, false)
        binding.creditListFragmentRecycleView.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        creditListViewModel.getSchedule()
        creditListViewModel.creditListLiveData.observe(viewLifecycleOwner, { adapter.setData(it) })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
