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
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import molinov.creditcalculator.MainActivity
import molinov.creditcalculator.R
import molinov.creditcalculator.app.MainAppState
import molinov.creditcalculator.databinding.MainFragmentBinding
import molinov.creditcalculator.model.*
import molinov.creditcalculator.viewmodel.MainViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!
    private val creditTypes: Array<String> by lazy { resources.getStringArray(R.array.credit_types) }
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this).get(MainViewModel::class.java) }
    private val navViewModel: MainViewModel by navGraphViewModels(R.id.mobile_navigation) {
        defaultViewModelProviderFactory
    }
    private var dropDownPos = 0

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
        navViewModel.navLiveData.observe(viewLifecycleOwner, { restoreData(it) })
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
                    val action = MainFragmentDirections.actionMainToSchedule(
                        collectDataFields(binding)
                    )
                    Navigation.findNavController(it).navigate(action)
                    (activity as MainActivity).selectedItem = R.id.schedule_fragment
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
                    viewModel.getCalculate(data)
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

                /** How i may synchronized this fun without Boolean? */
                override fun afterTextChanged(s: Editable?) {
                    incorrectInputCheck(s, creditAmountField, incorrectPrefixes, mainErrors)
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

    private fun renderData(mainAppState: MainAppState) {
        when (mainAppState) {
            is MainAppState.Success -> {
                binding.apply {
                    payment.editText?.setText(mainAppState.data.payment)
                    overPayment.editText?.setText(mainAppState.data.overPayment)
                    totalPayment.editText?.setText(mainAppState.data.totalPayment)
                }
            }
            is MainAppState.Loading -> {
                // TODO
            }
        }
    }

    private fun collectDataFields(b: MainFragmentBinding): DataFields {
        val amount = b.creditAmountField.editText?.text.toString().replace(" ", "")
        return DataFields(
            parseStringToDate(b.firstPaymentField.editText?.text.toString()),
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
                            error = string
                            isErrorEnabled = true
                            true
                        }
                        pressBtn -> {
                            clearFocus()
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
                .setCalendarConstraints(setPickerConstraints()).setSelection(setDefaultPickerDate())
                .build()
        picker.addOnPositiveButtonClickListener {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = it
            val formatter = getSimpleDateFormat()
            binding.firstPaymentField.editText?.setText(formatter.format(calendar.time))
        }
        picker.show(this.parentFragmentManager, "DATE_PICKER")
    }

    override fun onResume() {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, creditTypes)
        binding.creditType.setText(creditTypes[dropDownPos])
        binding.creditType.setAdapter(adapter)
        (activity as MainActivity).selectedItem = R.id.main_fragment
        super.onResume()
    }

    private fun restoreData(b: Bundle) {
        binding.apply {
            firstPaymentField.editText?.setText(b.getString(getString(R.string.date_key)))
            creditAmountField.editText?.setText(b.getString(getString(R.string.amount_key)))
            loanTermField.editText?.setText(b.getString(getString(R.string.loan_term_key)))
            typeTerm.check(b.getInt(getString(R.string.type_term_key)))
            rateField.editText?.setText(b.getString(getString(R.string.rate_key)))
            payment.editText?.setText(b.getString(getString(R.string.payment_key)))
            overPayment.editText?.setText(b.getString(getString(R.string.overpayment_key)))
            totalPayment.editText?.setText(b.getString(getString(R.string.total_payment_key)))
            dropDownPos = b.getInt(getString(R.string.type_of_credit_key))
            creditType.setText(creditTypes[dropDownPos])
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            navViewModel.navLiveData.value = bundleOf(
                getString(R.string.date_key) to firstPaymentField.editText?.text.toString(),
                getString(R.string.amount_key) to creditAmountField.editText?.text.toString(),
                getString(R.string.loan_term_key) to loanTermField.editText?.text.toString(),
                getString(R.string.type_term_key) to typeTerm.checkedButtonId,
                getString(R.string.rate_key) to rateField.editText?.text.toString(),
                getString(R.string.payment_key) to payment.editText?.text.toString(),
                getString(R.string.overpayment_key) to binding.overPayment.editText?.text.toString(),
                getString(R.string.total_payment_key) to binding.totalPayment.editText?.text.toString(),
                getString(R.string.type_of_credit_key) to creditTypes.indexOf(binding.creditType.text.toString())
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
