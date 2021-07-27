package molinov.creditcalculator.view.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import molinov.creditcalculator.R
import molinov.creditcalculator.app.AppState
import molinov.creditcalculator.databinding.MainFragmentBinding
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.model.setLowScale
import molinov.creditcalculator.view.schedule.ScheduleFragment
import molinov.creditcalculator.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.firstPaymentField.setOnTouchListener { _, event ->
            if (MotionEvent.ACTION_UP == event.action) {
                editTextClicked()
            }
            return@setOnTouchListener false
        }
        binding.scheduleBtn.setOnClickListener {
            if (checkFields()) {
                openSchedule()
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
        binding.calculateBtn.setOnClickListener {
            if (checkFields()) {
                binding.apply {
                    val data = DataFields(
                        dateParse(firstPaymentField.text.toString()),
                        creditAmountField.text.toString().toBigDecimal().setLowScale(),
                        loanTermField.text.toString().toBigDecimal().setLowScale(),
                        rateField.text.toString().toBigDecimal().setLowScale(),
                        month.isChecked,
                        creditType.text.toString() == creditTypesItems[0]
                    )
                    viewModel.calculate(data)
                }
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.mainLiveData.observe(viewLifecycleOwner, { renderData(it) })
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.apply {
                    payment.setText(appState.data.payment)
                    overPayment.setText(appState.data.overPayment)
                    totalPayment.setText(appState.data.totalPayment)
                }
            }
            is AppState.Loading -> {
                //TODO
            }
        }
    }

    private fun dateParse(date: String): Date {
        return if (Locale.getDefault() == Locale.US) {
            SimpleDateFormat(
                resources.getString(R.string.us_time_pattern),
                Locale.US
            ).parse(date) ?: Date()
        } else SimpleDateFormat(
            getString(R.string.classic_time_pattern),
            Locale.getDefault()
        ).parse(date) ?: Date()
    }

    private fun editTextClicked() {
        val picker =
            MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.choice_date))
                .setCalendarConstraints(setConstraints()).setSelection(setDate()).build()
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val formatter =
                if (Locale.getDefault() == Locale.US) SimpleDateFormat(
                    getString(R.string.us_time_pattern),
                    Locale.getDefault()
                )
                else SimpleDateFormat(getString(R.string.classic_time_pattern), Locale.getDefault())
            binding.firstPaymentField.setText(formatter.format(calendar.time))
        }
        picker.show(this.parentFragmentManager, "DATE_PICKER")
    }

    private fun setConstraints(): CalendarConstraints {
        return CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now()).build()
    }

    private fun setDate(): Long {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = today
        calendar.add(Calendar.MONTH, 1)
        return calendar.timeInMillis
    }

    private fun checkFields(): Boolean {
        binding.apply {
            if (firstPaymentField.text.isNullOrEmpty() || creditAmountField.text.isNullOrEmpty()
                || loanTermField.text.isNullOrEmpty() || rateField.text.isNullOrEmpty()
            ) {
                return false
            }
            return true
        }
    }

    private fun openSchedule() {
        parentFragmentManager.apply {
            beginTransaction()
                .replace(R.id.container, ScheduleFragment.newInstance())
                .addToBackStack("")
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, creditTypesItems)
        binding.creditType.setText(creditTypesItems[0])
        binding.creditType.setAdapter(adapter)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}
