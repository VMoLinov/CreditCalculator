package molinov.creditcalculator

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
        appBarConfiguration = AppBarConfiguration(navController.graph)
        navView.setupWithNavController(navController)
        initNavigation(navView)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
        }
        // setupActionBarWithNavController(navController, appBarConfiguration)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration)
//    }
    /** It permanently add to back stack all actions, and not clear it */
    private fun initNavigation(navView: BottomNavigationView) {
        navView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.schedule_fragment -> {
                    when (selectedItem) {
                        R.id.main_fragment -> navController.navigate(R.id.action_main_to_schedule)
                        R.id.credit_list_fragment -> navController.navigate(R.id.action_credit_list_to_schedule)
                    }
                    selectedItem = R.id.schedule_fragment
                }
                R.id.main_fragment -> {
                    when (selectedItem) {
                        R.id.schedule_fragment -> navController.navigate(R.id.action_schedule_to_main)
                        R.id.credit_list_fragment -> navController.navigate(R.id.action_credit_list_to_main)
                    }
                    selectedItem = R.id.main_fragment
                }
                R.id.credit_list_fragment -> {
                    when (selectedItem) {
                        R.id.main_fragment -> navController.navigate(R.id.action_main_to_credit_list)
                        R.id.schedule_fragment -> navController.navigate(R.id.action_schedule_to_credit_list)
                    }
                    selectedItem = R.id.credit_list_fragment
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    override fun onBackPressed() {
        when (navController.currentDestination?.id) {
            R.id.schedule_fragment -> navController.navigate(R.id.action_schedule_to_main)
            R.id.credit_list_fragment -> navController.navigate(R.id.action_credit_list_to_main)
            R.id.main_fragment -> {
                AlertDialog.Builder(this).setMessage("Выйти")
                    .setPositiveButton("Ok") { _, _ -> finish() }.show()
            }
            else -> super.onBackPressed()
        }
        /** How clear focus on a EditTextView, when backPressed? It doesn't work :( */
        currentFocus?.clearFocus()
    }
}
