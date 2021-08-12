package molinov.creditcalculator

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import molinov.creditcalculator.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    var selectedItem = R.id.main_fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragmentActivityMain.id) as NavHostFragment
        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
//        val options = NavOptions.Builder().setLaunchSingleTop()
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.schedule_fragment -> {
                    if (selectedItem != R.id.schedule_fragment) {
                        selectedItem = R.id.schedule_fragment
                        navController.navigate(R.id.action_main_fragment_to_schedule_fragment)
                    }
                }
                R.id.main_fragment -> {
                    if (selectedItem != R.id.main_fragment) {
                        selectedItem = R.id.main_fragment
                        navController.navigate(R.id.action_schedule_fragment_to_main_fragment)
                        navController.popBackStack(R.id.schedule_fragment, true)
                    }
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)
    }

    // How clear focus on a EditTextView, when backPressed? It doesn't work :(
    override fun onBackPressed() {
        super.onBackPressed()
        currentFocus?.clearFocus()
    }
}
