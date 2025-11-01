package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

@Composable
fun RootNavGraph(navHostController: NavHostController) {
    var isLoggedIn by remember { mutableStateOf(false) }

    NavHost(navController = navHostController,
        startDestination = if (isLoggedIn) Screen.MainGraph.route else Screen.AuthGraph.route
    ) {
        authNavGraph(navHostController)
        mainNavGraph(navHostController)
    }
}