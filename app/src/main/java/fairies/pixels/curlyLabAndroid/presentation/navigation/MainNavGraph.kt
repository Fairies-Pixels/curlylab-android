package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import fairies.pixels.curlyLabAndroid.presentation.composition.screen.CompositionCheckScreen
import fairies.pixels.curlyLabAndroid.presentation.dictionary.screen.DictionaryScreen
import fairies.pixels.curlyLabAndroid.presentation.guide.screen.GuideScreen
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen.HairAnalysisScreen
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen.HairTypingScreen
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen.IsColoredTextTypingScreen
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen.PorosityTextTypingScreen
import fairies.pixels.curlyLabAndroid.presentation.hairTyping.screen.ThicknessTextTypingScreen
import fairies.pixels.curlyLabAndroid.presentation.products.screen.ProductsScreen
import java.util.UUID

fun NavGraphBuilder.mainNavGraph(navHostController: NavHostController) {
    navigation(
        startDestination = Screen.Main.route,
        route = Screen.MainGraph.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(navHostController)
        }
        composable(Screen.Dictionary.route) {
            DictionaryScreen()
        }
        composable(Screen.Products.route) {
            ProductsScreen()
        }
        composable(Screen.Guide.route) {
            GuideScreen(navHostController)
        }

        composable(Screen.CompositionCheck.route) {
            CompositionCheckScreen(navController =navHostController)
        }
        composable(Screen.HairAnalysis.route) {
            HairAnalysisScreen(navController = navHostController)
        }
        composable(Screen.HairTyping.route) {
            HairTypingScreen(navController = navHostController)
        }
        composable(Screen.PorosityTextTyping.route) {
            PorosityTextTypingScreen(navController = navHostController)
        }
        composable(Screen.ThicknessTextTyping.route) {
            ThicknessTextTypingScreen(navController = navHostController)
        }

        composable(Screen.IsColoredTextTyping.route) {
            IsColoredTextTypingScreen(navController = navHostController)
        }
    }
}