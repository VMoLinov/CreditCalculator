package molinov.creditcalculator.view.creditslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import molinov.creditcalculator.databinding.CreditListFragmentBinding
import molinov.creditcalculator.viewmodel.CreditListViewModel

class CreditListFragment : Fragment() {

    private val creditListViewModel: CreditListViewModel by lazy {
        ViewModelProvider(this).get(CreditListViewModel::class.java)
    }
    private var _binding: CreditListFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = CreditListFragmentBinding.inflate(inflater, container, false)
        val textView: TextView = binding.textCreditList
        creditListViewModel.text.observe(viewLifecycleOwner, { textView.text = it })
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
