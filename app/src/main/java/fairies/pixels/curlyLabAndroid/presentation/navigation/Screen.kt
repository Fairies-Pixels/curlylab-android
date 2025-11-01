package fairies.pixels.curlyLabAndroid.presentation.navigation

sealed class Screen(val route: String) {
    data object AuthGraph : Screen("auth_graph")
    data object MainGraph : Screen("main_graph")

    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object ResetPassword : Screen("reset_password")

    data object Main : Screen("main")

    data object Home : Screen("home")
    data object Profile : Screen("profile")

    data object Dictionary : Screen("dictionary")
    data object Products : Screen("products")
    data object Guide : Screen("guide")
    data object HairAnalysis : Screen("hair_analysis")
    data object CompositionCheck : Screen("composition_check")
    data object HairTyping : Screen("hair_typing")
    data object PorosityTextTyping : Screen("porosity_text_typing_screen")
    data object ThicknessTextTyping : Screen("thickness_text_typing_screen")
    data object IsColoredTextTyping : Screen("is_colored_text_typing_screen")
}
