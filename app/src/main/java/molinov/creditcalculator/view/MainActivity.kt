package molinov.creditcalculator.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import molinov.creditcalculator.R
import molinov.creditcalculator.databinding.MainActivityBinding
import molinov.creditcalculator.view.main.MainFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

    // How clear focus on a EditTextView, when backPressed? It doesn't work :(
    override fun onBackPressed() {
        super.onBackPressed()
        currentFocus?.clearFocus()
    }
}
