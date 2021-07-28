package molinov.creditcalculator.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
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

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val creditTypes: Array<String> by lazy { resources.getStringArray(R.array.credit_types) }
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            firstPaymentField.editText?.setOnTouchListener { _, event ->
                if (MotionEvent.ACTION_UP == event?.action) editTextClicked()
                return@setOnTouchListener false
            }
            firstPaymentField.onFocusChangeListener =
                View.OnFocusChangeListener { _, _ -> }
            scheduleBtn.setOnClickListener {
                if (checkFields()) {
                    openSchedule()
                    hideKeyboard(requireContext(), view)
                } else {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                }
            }
            calculateBtn.setOnClickListener {
                if (checkFields()) {
                    val data = DataFields(
                        dateParse(firstPaymentField.editText?.text.toString()),
                        creditAmountField.editText?.text.toString().toBigDecimal()
                            .setLowScale(),
                        loanTermField.editText?.text.toString().toBigDecimal().setLowScale(),
                        rateField.editText?.text.toString().toBigDecimal().setLowScale(),
                        month.isChecked,
                        creditType.text.toString() == creditTypes[0]
                    )
                    viewModel.calculate(data)
                    hideKeyboard(requireContext(), view)
                }
            }
            creditAmountField.editText?.addTextChangedListener {
                creditAmountField.apply {
                    if (it.toString().startsWith("0")) {
                        it?.clear()
                        error = resources.getString(R.string.nole_ahead)
                        isErrorEnabled = true
                    } else isErrorEnabled = false
                }
            }
            loanTermField.editText?.addTextChangedListener { zeroSmallListener(it, loanTermField) }
            rateField.editText?.addTextChangedListener { zeroSmallListener(it, rateField) }
        }
        viewModel.mainLiveData.observe(viewLifecycleOwner, { renderData(it) })
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun zeroSmallListener(it: Editable?, view: TextInputLayout) {
        view.apply {
            if (it.toString().startsWith("0")) {
                it?.clear()
                error = resources.getString(R.string.nole_ahead_small)
                isErrorEnabled = true
            } else isErrorEnabled = false
        }
    }

    private fun renderData(appState: AppState) {
        when (appState) {
            is AppState.Success -> {
                binding.apply {
                    payment.editText?.setText(appState.data.payment)
                    overPayment.editText?.setText(appState.data.overPayment)
                    totalPayment.editText?.setText(appState.data.totalPayment)
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
            binding.firstPaymentField.editText?.setText(formatter.format(calendar.time))
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
            if (firstPaymentField.editText?.text.isNullOrEmpty() || creditAmountField.editText?.text.isNullOrEmpty()
                || loanTermField.editText?.text.isNullOrEmpty() || rateField.editText?.text.isNullOrEmpty()
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

        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, creditTypes)
        binding.creditType.setText(creditTypes[0])
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
