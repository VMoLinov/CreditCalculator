package molinov.creditcalculator.view.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import molinov.creditcalculator.R
import molinov.creditcalculator.databinding.ScheduleFragmentBinding
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.viewmodel.ScheduleViewModel

class ScheduleFragment : Fragment() {

    private var _binding: ScheduleFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by lazy {
        ViewModelProvider(this).get(ScheduleViewModel::class.java)
    }
    private val navViewModel: ScheduleViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
    private val adapter: ScheduleAdapter by lazy { ScheduleAdapter() }
    private var data: DataFields? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScheduleFragmentBinding.inflate(inflater, container, false)
        binding.scheduleFragmentRecyclerView.adapter = adapter
//        binding.scheduleFragmentRecyclerView.addItemDecoration(
//            DividerItemDecoration(
//                context,
//                LinearLayoutManager.VERTICAL
//            )
//        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = ScheduleFragmentArgs.fromBundle(requireArguments()).data
        if (data == null) {
            binding.scheduleTextView.text = getString(R.string.schedule_empty)
            binding.scheduleTextView.visibility = View.VISIBLE
            navViewModel.navLiveData.observe(viewLifecycleOwner, { restoreData(it) })
        } else {
            restoreData(data)
        }
    }

    private fun restoreData(d: DataFields?) {
        if (d != null) {
            data = d
            binding.scheduleTextView.visibility = View.GONE
            viewModel.scheduleLiveData.observe(viewLifecycleOwner, { adapter.setData(it) })
            viewModel.getSchedule(d)
        }
    }

    override fun onPause() {
        super.onPause()
        navViewModel.navLiveData.value = data
    }
}
