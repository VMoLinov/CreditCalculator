package molinov.creditcalculator.view.schedule

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.navGraphViewModels
import molinov.creditcalculator.MainActivity
import molinov.creditcalculator.R
import molinov.creditcalculator.databinding.ScheduleFragmentBinding
import molinov.creditcalculator.model.*
import molinov.creditcalculator.utils.formattedYears
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
    private val adapter: ScheduleAdapter by lazy { ScheduleAdapter(requireContext()) }
    private var data: DataFields? = null
    private var isFABExpanded = false
    private var isRecyclerExpanded = false
    private var isScrolling = false

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
        setScrollBehavior(binding.scrollView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data = ScheduleFragmentArgs.fromBundle(requireArguments()).data
        if (data == null) {
            binding.scheduleTextView.text = getString(R.string.schedule_empty)
            binding.body.visibility = View.GONE
            binding.scheduleTextView.visibility = View.VISIBLE
            binding.scheduleImageButton.visibility = View.VISIBLE
            setBack()
            navViewModel.navLiveData.observe(viewLifecycleOwner, { restoreData(it) })
        } else {
            restoreData(data)
        }
    }

    private fun setBack() {
        binding.scheduleImageButton.setOnClickListener {
            Navigation.findNavController(requireView())
                .navigate(ScheduleFragmentDirections.actionScheduleToMain())
        }
    }

    private fun restoreData(d: DataFields?) {
        if (d != null) {
            setFAB()
            data = d
            binding.scheduleTextView.visibility = View.GONE
            binding.scheduleImageButton.visibility = View.GONE
            binding.body.visibility = View.VISIBLE
            viewModel.scheduleLiveData.observe(viewLifecycleOwner, { adapter.setData(it) })
            viewModel.getSchedule(d)
            val c = parseDataFieldsToCalculate(d)
            fillHeader(c)
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun fillHeader(c: Calculate) {
        if (data != null) {
            binding.header.apply {
                val d = parseDataFieldsToUI(data!!)
                creditAmount?.text = d.amount
                loanTerm?.text = d.loanTerm + " " +
                        if (d.isMonths) resources.getString(R.string.months)
                        else formattedYears(d.loanTerm.toBigDecimal(), requireView())
                payment.text = c.payment
                binding.recycleHeader.payment.text = c.payment
                overPayment?.text = c.overPayment
                rate?.text = d.rate
                effectiveRate?.text = effectiveRate(c.overPayment, d.amount)
                details?.setOnClickListener {
                    if (isRecyclerExpanded) {
                        binding.recycleHeader.scheduleRecyclerViewHeader.visibility = View.GONE
                        binding.scrollView.visibility = View.GONE
                    } else {
                        binding.recycleHeader.scheduleRecyclerViewHeader.visibility = View.VISIBLE
                        binding.scrollView.visibility = View.VISIBLE
                        adapter.notifyDataSetChanged()
                    }
                    isRecyclerExpanded = !isRecyclerExpanded
                }
            }
        }
    }

    private fun setFAB() {
        setInitialState()
        binding.fabLayout.fab.setOnClickListener {
            if (isFABExpanded) collapseFAB()
            else expandFAB()
        }
    }

    private fun setInitialState() {
        binding.fabLayout.apply {
            fabLayout.isVisible = true
            transparentBackground.alpha = 0f
            optionOneContainer.apply {
                alpha = 0f
                isClickable = false
            }
            optionTwoContainer.apply {
                alpha = 0f
                isClickable = false
            }
        }
    }

    private fun collapseFAB() {
        isFABExpanded = false
        if (!isScrolling) binding.fabLayout.fab.animate().alpha(0.2f).duration = 1000
        binding.fabLayout.apply {
            optionOneContainer.apply {
                animate().translationY(0f).alpha(0f)
                getChildAt(0).isClickable = false
                getChildAt(1).isClickable = false
            }
            optionTwoContainer.apply {
                animate().translationY(0f).alpha(0f)
                getChildAt(0).isClickable = false
                getChildAt(1).isClickable = false
            }
            transparentBackground.apply {
                animate().translationY(0f).alpha(0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            setOnClickListener(null)
                            isClickable = false
                        }
                    })
            }
        }
    }

    private fun expandFAB() {
        isFABExpanded = true
        binding.fabLayout.apply {
            fab.animate().alpha(1f)
            optionOneContainer.apply {
                animate().translationY(-300f).alpha(1f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            getChildAt(1).setOnClickListener {
                                createDialogToSave()
                                collapseFAB()
                            }
                            getChildAt(1).isClickable = true
                        }
                    })
            }
            optionTwoContainer.apply {
                animate().translationY(-175f).alpha(1f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            getChildAt(1).setOnClickListener {
                                collapseFAB()
                                requireActivity().requestedOrientation =
                                    if (activity?.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
                                        ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                                    else ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            }
                            getChildAt(1).isClickable = true
                        }
                    })
            }
            transparentBackground.apply {
                animate().translationY(0f).alpha(0.8f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            setOnClickListener { collapseFAB() }
                            isClickable = true
                        }
                    })
            }
        }
    }

    private fun createDialogToSave() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.dialog_save_calculation)
            .setPositiveButton(R.string.save) { d, _ ->
                val dataToSend = data
                dataToSend?.let {
                    synchronized(it) {
                        val dialog = d as Dialog
                        val nameField = dialog.findViewById<AppCompatEditText>(R.id.dialog_name)
                        val name = nameField.text.toString()
                        viewModel.saveDataToDB(adapter.data, name, dataToSend)
                        d.dismiss()
                        Navigation.findNavController(requireView())
                            .navigate(ScheduleFragmentDirections.actionScheduleToCreditList())
                        (activity as MainActivity).selectedItem = R.id.credit_list_fragment
                    }
                }
                if (data == null) Toast.makeText(
                    requireContext(),
                    getString(R.string.no_data),
                    Toast.LENGTH_SHORT
                ).show()
            }
            .setNegativeButton(R.string.cancel) { d, _ -> d.cancel() }
        val dialog = builder.create()
        dialog.show()
        val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        button.isEnabled = false
        val nameField = dialog.findViewById<EditText>(R.id.dialog_name)
        nameField.addTextChangedListener { button.isEnabled = it?.isNotEmpty() == true }
    }

    private fun setScrollBehavior(scrollView: NestedScrollView) {
        scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val child = binding.scrollView.getChildAt(0).height.toFloat()
            val scroll = binding.scrollView.height
            isScrolling = scroll > child && child / scroll > 0.9
            if (isScrolling) binding.fabLayout.fab.animate().alpha(0.2f).duration = 1000
        }
        scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            if (!binding.scrollView.canScrollVertically(1)) {
                binding.fabLayout.fab.animate().alpha(0.2f)
            } else binding.fabLayout.fab.animate().alpha(1f)
            binding.header.scheduleRecyclerViewHeader.isSelected =
                scrollView.canScrollVertically(-1)
        }
    }

    override fun onPause() {
        super.onPause()
        navViewModel.navLiveData.value = data
    }

    override fun onResume() {
        (activity as MainActivity).selectedItem = R.id.schedule_fragment
        requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
