package id.dev.oxy.ui

import android.app.Activity
import android.app.KeyguardManager
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.mazenrashed.printooth.Printooth
import com.mazenrashed.printooth.ui.ScanningActivity
import com.mazenrashed.printooth.utilities.Printing
import dagger.hilt.android.AndroidEntryPoint
import id.dev.oxy.R
import id.dev.oxy.databinding.ActivityMainBinding
import id.dev.oxy.ui.auth.AuthActivity
import id.dev.oxy.ui.auth.AuthViewModel
import id.dev.oxy.ui.auth.logout.LogoutConfirmationDialogFragment
import id.dev.oxy.ui.auth.opencash.InputPinDialogFragment
import id.dev.oxy.ui.compose.LoadingDialog
import id.dev.oxy.ui.customer.CustomerViewModel
import id.dev.oxy.ui.customer.component.AddCustomerDialogFragment
import id.dev.oxy.ui.history.HistoryViewModel
import id.dev.oxy.ui.printer.PrinterViewModel
import id.dev.oxy.ui.sales.CashRegisterInfoDialogFragment
import id.dev.oxy.ui.sales.SalesViewModel
import id.dev.oxy.util.PrintHistoryHelper
import id.dev.oxy.util.Resource

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val authViewModel: AuthViewModel by viewModels()
    private val salesViewModel: SalesViewModel by viewModels()
    private val customerViewModel: CustomerViewModel by viewModels()
    private val printerViewModel: PrinterViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    private var printing: Printing? = null

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                printerViewModel.setPrintConnect(true)
                printing = Printooth.printer()
                val snackBar =
                    Snackbar.make(binding.root, R.string.printer_connected, Snackbar.LENGTH_LONG)

                snackBar.setAction(R.string.action_done) {
                    snackBar.dismiss()
                }.setActionTextColor(ContextCompat.getColor(applicationContext, R.color.login_btn))
                    .show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Printooth.hasPairedPrinter()) printing = Printooth.printer()

        binding.logoutDialog.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        }

        setSupportActionBar(binding.appBarMain.toolbar)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        initView()
        initDialog()
        populateData()


        onBackPressedDispatcher.addCallback(this) {
            if (navController.backQueue.isEmpty()) {
                finish()
                authViewModel.saveInputPinSession(true)
            } else {
                navController.popBackStack()
            }
        }
    }

    private fun initView() {
        val drawerLayout = binding.drawerLayout
        val navView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_sales,
                R.id.nav_history,
                R.id.nav_customer,
                R.id.nav_draft,
                R.id.nav_printer,
                R.id.nav_logout
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateOptionsMenu()
            handleNavigationDrawer(destination)
        }
        salesViewModel.loginData.observe(this) { resource ->
            navView.getHeaderView(0).apply {
                findViewById<TextView>(R.id.txt_username).text = resource[0]
                findViewById<TextView>(R.id.txt_email).text = resource[1]
            }
        }
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
    }

    private fun initDialog() {
        authViewModel.getInputPinSession.observe(this) {
            val fragmentManager = supportFragmentManager
            val pinDialog = InputPinDialogFragment()
            pinDialog.isCancelable = false
            pinDialog.dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if (it) {
                pinDialog.show(fragmentManager, MainActivity::class.java.name)
            } else {
                pinDialog.dialog?.dismiss()
            }
        }
    }

    private fun handleNavigationDrawer(destination: NavDestination) {
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val displayViewWidth = size.x

        val params = binding.drawerLayout.layoutParams
        val fragmentCart = supportFragmentManager.findFragmentById(R.id.fragment_cart)
        if (destination.label == getString(R.string.menu_sales)) {
            params.width = displayViewWidth * 65 / 100
            binding.drawerLayout.layoutParams = params
            fragmentCart?.view?.visibility = View.VISIBLE
        } else {
            params.width = displayViewWidth
            binding.drawerLayout.layoutParams = params
            fragmentCart?.view?.visibility = View.GONE
        }
    }

    private fun populateData() {
        binding.navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            binding.drawerLayout.close()
            val logoutDialog = LogoutConfirmationDialogFragment()
            logoutDialog.show(supportFragmentManager, MainActivity::class.java.name)
            true
        }

        printerViewModel.printConnected.observe(this) { value ->
            val menuPrint = binding.navView.menu.findItem(R.id.nav_printer)

            if (value) {
                menuPrint.apply {
                    title = getString(R.string.menu_print_disconnect)
                    icon = AppCompatResources.getDrawable(
                        applicationContext,
                        R.drawable.baseline_print_disabled_24
                    )
                }
            } else {
                menuPrint.apply {
                    title = getString(R.string.menu_print_connect)
                    icon = AppCompatResources.getDrawable(
                        applicationContext,
                        R.drawable.baseline_print_24
                    )
                }
            }

            menuPrint.setOnMenuItemClickListener {
                binding.drawerLayout.close()
                if (value) {
                    Printooth.removeCurrentPrinter()
                    printerViewModel.setPrintConnect(false)
                    val snackBar = Snackbar.make(
                        binding.root,
                        R.string.printer_disconnected,
                        Snackbar.LENGTH_LONG
                    )

                    snackBar.setAction(R.string.action_done) {
                        snackBar.dismiss()
                    }.setActionTextColor(
                        ContextCompat.getColor(applicationContext, R.color.login_btn)
                    ).show()
                } else {
                    resultLauncher.launch(
                        Intent(applicationContext, ScanningActivity::class.java),
                    )
                }
                true
            }
        }

        authViewModel.logoutResponse.observe(this) { resource ->
            when (resource) {
                is Resource.Idle -> {
                    binding.logoutDialog.apply { setContent {} }
                }
                is Resource.Loading -> {
                    binding.logoutDialog.apply { setContent { MaterialTheme { LoadingDialog() } } }
                }
                is Resource.Success -> {
                    authViewModel.run { backToIdle() }
                    Intent(this, AuthActivity::class.java).run {
                        startActivity(this)
                        finish()
                    }
                }
                is Resource.Error -> {
                    val expiredToken = resource.message == "Unauthenticated."

                    if (expiredToken) {
                        Intent(this, AuthActivity::class.java).run {
                            startActivity(this)
                            finish()
                        }
                        authViewModel.expiredToken()
                    } else {
                        handleError(resource.message)
                    }
                }
            }
        }
    }

    private fun handleError(message: String) {
        binding.logoutDialog.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    AlertDialog(
                        title = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Terjadi Error",
                                    textAlign = TextAlign.Center,
                                    fontSize = MaterialTheme.typography.h5.fontSize,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        },
                        text = {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = message,
                                    fontSize = MaterialTheme.typography.subtitle1.fontSize,
                                    fontWeight = FontWeight.Normal,
                                )
                            }
                        },
                        confirmButton = { },
                        onDismissRequest = { authViewModel.backToIdle() }
                    )
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sales_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val destinationLabel = navController.currentDestination?.label
        if (destinationLabel == getString(R.string.menu_sales)) {
            handleSearchMenu(menu)
        }
        if (destinationLabel == getString(R.string.menu_history)) {
            handleHistoryMenu(menu)
        }
        if (destinationLabel == getString(R.string.menu_customer)) {
            menu?.apply {
                removeItem(R.id.action_search)
                removeItem(R.id.action_print)
                removeItem(R.id.action_info)

                val actionAdd = findItem(R.id.action_add)

                actionAdd.setOnMenuItemClickListener {
                    customerViewModel.getCustomerGroup()
                    val pinDialog = AddCustomerDialogFragment()
                    pinDialog.show(supportFragmentManager, MainActivity::class.java.name)

                    true
                }
            }
        }
        if (destinationLabel == getString(R.string.menu_draft)) {
            menu?.apply {
                removeItem(R.id.action_add)
                removeItem(R.id.action_search)
                removeItem(R.id.action_info)
                removeItem(R.id.action_print)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun handleSearchMenu(menu: Menu?) {
        menu?.apply {
            removeItem(R.id.action_add)
            removeItem(R.id.action_print)

            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            val searchView = findItem(R.id.action_search).actionView as SearchView

            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.queryHint = resources.getString(R.string.search_hint)

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(query: String): Boolean {
                    salesViewModel.setQuery(query)
                    return false
                }
            })
            searchView.setOnCloseListener {
                salesViewModel.setQuery("")
                false
            }

            val actionInfo = findItem(R.id.action_info)

            actionInfo.setOnMenuItemClickListener {
                salesViewModel.checkCashRegister()
                val cashRegisterDialog = CashRegisterInfoDialogFragment()
                cashRegisterDialog.show(supportFragmentManager, MainActivity::class.java.name)

                true
            }
        }
    }

    private fun handleHistoryMenu(menu: Menu?) {
        historyViewModel.detailHistory.observe(this) { resources ->
            val visible = resources is Resource.Success
            menu?.apply {
                removeItem(R.id.action_add)
                removeItem(R.id.action_search)
                removeItem(R.id.action_info)

                val actionPrint = findItem(R.id.action_print)
                actionPrint.isVisible = visible

                actionPrint.setOnMenuItemClickListener {
                    if (!Printooth.hasPairedPrinter()) {
                        val snackBar = Snackbar.make(
                            binding.root,
                            R.string.printer_not_connected,
                            Snackbar.LENGTH_LONG
                        )

                        snackBar.setAction(R.string.action_done) {
                            snackBar.dismiss()
                        }.setActionTextColor(
                            ContextCompat.getColor(
                                applicationContext,
                                R.color.login_btn
                            )
                        ).show()
                    } else {
                        historyViewModel.loginData.observe(this@MainActivity) {
                            val cashierName = it[0]
                            val dataPrint = PrintHistoryHelper.formatPrint(
                                applicationContext,
                                (resources as Resource.Success).data.data[0],
                                cashierName
                            )
                            printing?.print(dataPrint)

                            val snackBar = Snackbar.make(
                                binding.root,
                                R.string.printer_on_process,
                                Snackbar.LENGTH_LONG
                            )

                            snackBar.setAction(R.string.action_done) {
                                snackBar.dismiss()
                            }.setActionTextColor(
                                ContextCompat.getColor(
                                    applicationContext,
                                    R.color.login_btn
                                )
                            ).show()
                        }
                    }
                    true
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val keyguardManager: KeyguardManager =
            getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (keyguardManager.isKeyguardLocked) {
            authViewModel.saveInputPinSession(true)
        }
    }
}