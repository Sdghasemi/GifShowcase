package com.hirno.gif.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hirno.gif.R
import com.hirno.gif.databinding.MainActivityBinding
import com.hirno.gif.model.state.MainScreenEffect
import com.hirno.gif.model.state.MainScreenEffect.NavigateBackToRandomScreen
import com.hirno.gif.model.state.MainScreenEffect.NavigateToSearchScreen
import com.hirno.gif.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

    private lateinit var binding: MainActivityBinding

    private val navController: NavController
        get() = findNavController(R.id.nav_host)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        observeViewEffect()
    }

    private fun observeViewEffect() {
        viewModel.obtainEffect.observe(this) { effect ->
            triggerEffect(effect)
        }
    }

    private fun triggerEffect(effect: MainScreenEffect) {
        when (effect) {
            is NavigateToSearchScreen -> navigateToSearch()
            is NavigateBackToRandomScreen -> navigateBackToRandom()
        }
    }
    private fun navigateToSearch() {
        navController.navigate(R.id.SearchFragment)
    }

    private fun navigateBackToRandom() {
        navController.popBackStack(R.id.RandomFragment, false)
    }
}