package molinov.creditcalculator.view.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import molinov.creditcalculator.databinding.ScheduleFragmentBinding
import molinov.creditcalculator.viewmodel.ScheduleViewModel

class ScheduleFragment : Fragment() {

    private var _binding: ScheduleFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by lazy {
        ViewModelProvider(this).get(ScheduleViewModel::class.java)
    }
    private val adapter: ScheduleAdapter by lazy { ScheduleAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ScheduleFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scheduleFragmentRecyclerView.adapter = adapter
        viewModel.scheduleLiveData.observe(viewLifecycleOwner, {

        })
    }

    companion object {
        fun newInstance() = ScheduleFragment()
    }
}
