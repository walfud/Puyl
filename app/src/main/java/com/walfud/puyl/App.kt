package com.walfud.puyl

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.walfud.puyl.ui.theme.PuylTheme
import androidx.compose.runtime.getValue

@Composable
fun App() {
    PuylTheme {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            scaffoldState = scaffoldState,
        ) { innerPadding ->
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = ROUTINE_TASK
            ) {
                composable(ROUTINE_TASK) {
                    val taskVM = viewModel<TaskViewModel>(factory = MyViewModelFactory(navController))
                    TaskPage(taskVM)
                }
            }
        }
    }
}