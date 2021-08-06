package molinov.creditcalculator.view.schedule

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private var isExpanded = false
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
        setFAB()
        binding.scrollView.viewTreeObserver.addOnGlobalLayoutListener {
            val child = binding.scrollView.getChildAt(0).height
            isScrolling = binding.scrollView.height <= child
            if (!isScrolling) binding.fabLayout.fab.animate().alpha(0.2f).duration = 1000
        }
        binding.scrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            if (binding.scrollView.canScrollVertically(-1)) {
                binding.fabLayout.fab.animate().alpha(0.2f)
            } else binding.fabLayout.fab.animate().alpha(1f)
        }
        return binding.root
    }

    private fun setFAB() {
        setInitialState()
        binding.fabLayout.fab.setOnClickListener {
            if (isExpanded) collapseFAB()
            else expandFAB()
        }
    }

    private fun collapseFAB() {
        isExpanded = false
        if (!isScrolling) binding.fabLayout.fab.animate().alpha(0.2f).duration = 1000
        binding.fabLayout.apply {
            optionOneContainer.fabAction(0f, 0f, false, null)
            optionTwoContainer.fabAction(0f, 0f, false, null)
            transparentBackground.fabAction(0f, 0f, false, null)
        }
    }

    private fun expandFAB() {
        isExpanded = true
        binding.fabLayout.apply {
            fab.animate().alpha(1f)
            optionOneContainer.fabAction(-300f, 1f, true, ROTATE)
            optionTwoContainer.fabAction(-175f, 1f, true, SAVE_CALCULATE)
            transparentBackground.fabAction(0f, 0.8f, true, COLLAPSE_FAB)
        }
    }

    private fun View.fabAction(move: Float, alpha: Float, isClick: Boolean, int: Int?) : View {
        this.animate()
            .translationY(move)
            .alpha(alpha)
            .setDuration(300)
                
            .setListener(object : AnimatorListenerAdapter() {

                override fun onAnimationEnd(animation: Animator?) {
                    when (isClick) {
                        true -> {
                            this@fabAction.setOnClickListener {
                                when (int) {
                                    COLLAPSE_FAB -> collapseFAB()
                                    SAVE_CALCULATE -> Toast.makeText(
                                        it.context,
                                        "message",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        false -> this@fabAction.setOnClickListener(null)
                    }
                    this@fabAction.isClickable = isClick
                }
            })
    }

    private fun setInitialState() {
        binding.fabLayout.apply {
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

    companion object {
        const val COLLAPSE_FAB = 1
        const val SAVE_CALCULATE = 2
        const val ROTATE = 3
    }
}
