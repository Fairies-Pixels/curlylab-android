package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import fairies.pixels.curlyLabAndroid.presentation.auth.screen.ResetPasswordScreen
import fairies.pixels.curlyLabAndroid.presentation.auth.screen.SignInScreen
import fairies.pixels.curlyLabAndroid.presentation.auth.screen.SignUpScreen

fun NavGraphBuilder.authNavGraph(navHostController: NavHostController) {
    navigation(
        startDestination = Screen.SignIn.route,
        route = Screen.AuthGraph.route
    ) {
        composable(Screen.SignIn.route) {
            SignInScreen(onSignInSuccess = {
                navHostController.navigate(Screen.MainGraph.route) {
                    popUpTo(Screen.AuthGraph.route) { inclusive = true }
                }
            }, onNavigateToSignUp = {
                navHostController.navigate(Screen.SignUp.route)
            }, onNavigateToResetPassword = {
                navHostController.navigate(Screen.ResetPassword.route)
            })
        }

        composable(Screen.SignUp.route) {
            SignUpScreen(onSignUpSuccess = {
                navHostController.navigate(Screen.SignIn.route) {
                    popUpTo(Screen.AuthGraph.route) { inclusive = true }
                }
            }, onNavigateToSignIn = {
                navHostController.navigate(Screen.SignIn.route)
            })
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(onResetSuccess = {
                navHostController.navigate(Screen.SignIn.route) {
                    popUpTo(Screen.AuthGraph.route) { inclusive = true }
                }
            })
        }
    }
}
