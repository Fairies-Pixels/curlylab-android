package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import fairies.pixels.curlyLabAndroid.presentation.auth.screen.SignInScreen
import fairies.pixels.curlyLabAndroid.presentation.auth.screen.SignUpScreen

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onGoogleSignIn: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    navigation(
        startDestination = Screen.SignIn.route,
        route = Screen.AuthGraph.route
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    onNavigateToMain()
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToResetPassword = {
                    navController.navigate(Screen.ResetPassword.route)
                },
                onGoogleSignIn = onGoogleSignIn
            )
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.popBackStack()
                }
            )
        }
    }
}