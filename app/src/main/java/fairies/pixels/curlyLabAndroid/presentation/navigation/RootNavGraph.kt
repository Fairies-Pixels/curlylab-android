package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import fairies.pixels.curlyLabAndroid.presentation.theme.LightBeige
import fairies.pixels.curlyLabAndroid.presentation.theme.LightGreen

@Composable
fun RootNavGraph(navHostController: NavHostController) {
    val mainViewModel: MainViewModel = hiltViewModel()
    val isLoggedIn by mainViewModel.isLoggedIn.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBeige),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = LightGreen)
        }
        return
    }

    val startDestination = when {
        isLoggedIn == true -> Screen.MainGraph.route
        else -> Screen.AuthGraph.route
    }

    val onGoogleSignIn = {
        // TODO: Реализовать Google Sign-In
    }

    val onNavigateToMain = {
        mainViewModel.setLoggedIn(true)
    }

    NavHost(
        navController = navHostController,
        startDestination = startDestination
    ) {
        authNavGraph(
            navController = navHostController,
            onGoogleSignIn = onGoogleSignIn,
            onNavigateToMain = onNavigateToMain
        )
        mainNavGraph(navHostController)
    }
}