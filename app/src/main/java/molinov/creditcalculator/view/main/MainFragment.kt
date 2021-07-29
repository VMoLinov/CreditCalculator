package molinov.creditcalculator.view.main

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import molinov.creditcalculator.R
import molinov.creditcalculator.app.MainFragmentAppState
import molinov.creditcalculator.databinding.MainFragmentBinding
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.model.setLowScale
import molinov.creditcalculator.viewmodel.MainViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
        bindingsSet(binding)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.mainLiveData.observe(viewLifecycleOwner, { renderData(it) })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindingsSet(binding: MainFragmentBinding) {
        val mainErrors = resources.getStringArray(R.array.input_errors_main)
        val errors = resources.getStringArray(R.array.input_errors)
        val incorrectPrefixes = resources.getStringArray(R.array.incorrect_prefixes)
        binding.apply {
            // How I may accept this behavior (clearFocus) to disabled views on a bottom?
            // I try focused, focusedInTouchMode, clicked = true, not work.
//            nestedScrollMain.setOnTouchListener { _, _ ->
//                requireActivity().currentFocus?.clearFocus()
//                hideKeyboard(requireContext(), requireView())
//                return@setOnTouchListener true
//            }
            firstPaymentField.editText?.setOnTouchListener { _, event ->
                if (MotionEvent.ACTION_UP == event?.action) datePickerLaunch()
                return@setOnTouchListener false
            }
            scheduleBtn.setOnClickListener {
                if (checkFields()) {
                    requireActivity().currentFocus?.clearFocus()
                    val action = MainFragmentDirections.actionMainFragmentToScheduleFragment(
                        collectDataFields(binding)
                    )
                    Navigation.findNavController(it).navigate(action)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.complete_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            calculateBtn.setOnClickListener {
                if (checkFields()) {
                    requireActivity().currentFocus?.clearFocus()
                    val data = collectDataFields(binding)
                    viewModel.calculate(data)
                    hideKeyboard(requireContext(), it)
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.complete_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            var isEditing = false
            creditAmountField.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                // How i may synchronized this fun without Boolean?
                override fun afterTextChanged(s: Editable?) {
                    if (isEditing) return
                    if (s.toString().isNotEmpty()) {
                        isEditing = true
                        val formattedString = s.toString().replace(" ", "").toDouble()
                        val dfs = DecimalFormatSymbols.getInstance()
                        dfs.groupingSeparator = ' '
                        val df =
                            DecimalFormat(getString(R.string.decimal_format_pattern_amount), dfs)
                        s?.replace(0, s.length, df.format(formattedString))
                        isEditing = false
                    }
                }
            })
            creditAmountField.editText?.addTextChangedListener {
                incorrectInputCheck(it, creditAmountField, incorrectPrefixes, mainErrors)
            }
            loanTermField.editText?.addTextChangedListener {
                incorrectInputCheck(it, loanTermField, incorrectPrefixes, errors)
            }
            rateField.editText?.addTextChangedListener {
                incorrectInputCheck(it, rateField, incorrectPrefixes, errors)
                if (it.toString().contains(Regex(getString(R.string.rate_regex)))) {
                    it?.insert(2, ".")
                }
            }
            creditAmountField.setKeyboardAction(
                EditorInfo.IME_ACTION_NEXT,
                getString(R.string.required_field),
                false
            )
            loanTermField.setKeyboardAction(
                EditorInfo.IME_ACTION_NEXT,
                getString(R.string.required_field_small),
                false
            )
            rateField.setKeyboardAction(
                EditorInfo.IME_ACTION_DONE,
                getString(R.string.required_field_small),
                true
            )
        }
    }

    private fun renderData(mainFragmentAppState: MainFragmentAppState) {
        when (mainFragmentAppState) {
            is MainFragmentAppState.Success -> {
                binding.apply {
                    payment.editText?.setText(mainFragmentAppState.data.payment)
                    overPayment.editText?.setText(mainFragmentAppState.data.overPayment)
                    totalPayment.editText?.setText(mainFragmentAppState.data.totalPayment)
                }
            }
            is MainFragmentAppState.Loading -> {
                // TODO
            }
            else -> {
                // TODO
            }
        }
    }

    private fun collectDataFields(b: MainFragmentBinding): DataFields {
        val amount = b.creditAmountField.editText?.text.toString().replace(" ", "")
        return DataFields(
            dateParse(b.firstPaymentField.editText?.text.toString()),
            amount.toBigDecimal().setLowScale(),
            b.loanTermField.editText?.text.toString().toBigDecimal().setLowScale(),
            b.rateField.editText?.text.toString().toBigDecimal().setLowScale(),
            b.month.isChecked,
            b.creditType.text.toString() == creditTypes[0]
        )
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

    private fun incorrectInputCheck(
        it: Editable?, view: TextInputLayout, prefix: Array<String>, errors: Array<String>
    ) {
        view.apply {
            for (i in prefix.indices) {
                if (it.toString().startsWith(prefix[i])) {
                    it?.clear()
                    error = errors[i]
                    isErrorEnabled = true
                    break
                } else isErrorEnabled = false
            }
        }
    }

    private fun TextInputLayout.setKeyboardAction(action: Int, string: String, pressBtn: Boolean) {
        this.editText?.setOnEditorActionListener { _, actionId, _ ->
            return@setOnEditorActionListener when (actionId) {
                action -> {
                    val boolean = this.editText?.text.toString().isEmpty()
                    when {
                        boolean -> {
                            this.error = string
                            this.isErrorEnabled = true
                            true
                        }
                        pressBtn -> {
                            this.clearFocus()
                            binding.calculateBtn.apply {
                                performClick()
                                isPressed = true
                                invalidate()
                                isPressed = false
                                invalidate()
                            }
                            false
                        }
                        else -> false
                    }
                }
                else -> true
            }
        }
    }

    private fun hideKeyboard(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun datePickerLaunch() {
        val picker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.choice_date))
                .setCalendarConstraints(setConstraints()).setSelection(setDate()).build()
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val formatter =
                if (Locale.getDefault() == Locale.US) SimpleDateFormat(
                    getString(R.string.us_time_pattern),
                    Locale.getDefault()
                )
                else SimpleDateFormat(
                    getString(R.string.classic_time_pattern),
                    Locale.getDefault()
                )
            binding.firstPaymentField.editText?.setText(formatter.format(calendar.time))
        }
        picker.show(this.parentFragmentManager, "DATE_PICKER")
    }

    private fun setConstraints(): CalendarConstraints {
        return CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
            .build()
    }

    private fun setDate(): Long {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = today
        calendar.add(Calendar.MONTH, 1)
        return calendar.timeInMillis
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

    override fun onResume() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, creditTypes)
        binding.creditType.setText(creditTypes[0])
        binding.creditType.setAdapter(adapter)
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
