package fairies.pixels.curlyLabAndroid.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import fairies.pixels.curlyLabAndroid.R

sealed class BottomNavItem(
    val icon: ImageVector,
    @StringRes val titleRes: Int,
    val route: String
) {
    data object Home : BottomNavItem(Icons.Default.Home, R.string.home, Screen.Home.route)
    data object Profile :
        BottomNavItem(Icons.Default.Person, R.string.profile, Screen.Profile.route)
}