package com.walfud.puyl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController

abstract class BaseViewModel(val navController: NavController) : ViewModel()

class MyViewModelFactory(private val navController: NavController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return modelClass.getConstructor(NavController::class.java).newInstance(navController)
    }
}