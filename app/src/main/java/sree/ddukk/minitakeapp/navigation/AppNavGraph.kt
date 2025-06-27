package sree.ddukk.minitakeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import sree.ddukk.minitakeapp.ui.QRScannerScreen
import sree.ddukk.minitakeapp.ui.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("scanner") { QRScannerScreen() }
    }
}
