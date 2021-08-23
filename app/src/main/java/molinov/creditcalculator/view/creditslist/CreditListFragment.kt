package molinov.creditcalculator.view.creditslist

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
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
    private val adapter: CreditListAdapter by lazy { CreditListAdapter() }
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
        itemTouchHelper = ItemTouchHelper(
            ItemTouchHelperCallback(
                adapter,
                ResourcesCompat.getDrawable(resources, R.drawable.ic_bank, null)!!,
                GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        resources.getColor(android.R.color.holo_red_dark, null),
                        resources.getColor(android.R.color.holo_red_light, null)
                    )
                ),
                GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        resources.getColor(android.R.color.holo_orange_light, null),
                        resources.getColor(android.R.color.holo_orange_dark, null)
                    )
                )
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.creditListFragmentRecycleView)
        creditListViewModel.creditListLiveData.observe(viewLifecycleOwner, { adapter.setData(it) })
        creditListViewModel.getSchedule()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
