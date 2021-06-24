package molinov.creditcalculator.view.main

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import molinov.creditcalculator.R
import molinov.creditcalculator.databinding.MainFragmentBinding
import molinov.creditcalculator.view.schedule.ScheduleFragment
import molinov.creditcalculator.viewmodel.MainViewModel

class MainFragment : Fragment() {

    private val creditTypesItems = listOf("аннуитет", "классика")
    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.scheduleBtn.setOnClickListener {
            parentFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.container, ScheduleFragment.newInstance())
                    .addToBackStack("")
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, creditTypesItems)
        binding.creditType.setText(creditTypesItems[0])
        binding.creditType.setAdapter(adapter)
    }


    companion object {
        fun newInstance() = MainFragment()
    }
}
