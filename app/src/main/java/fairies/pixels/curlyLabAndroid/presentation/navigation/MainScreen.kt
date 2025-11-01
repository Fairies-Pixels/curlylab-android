package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fairies.pixels.curlyLabAndroid.presentation.home.screen.HomeScreen
import fairies.pixels.curlyLabAndroid.presentation.profile.screen.ProfileScreen

@Composable
fun MainScreen(navController: NavHostController) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(mainNavController) }
    ) {
        innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController = navController)}
            composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
        }
    }
}