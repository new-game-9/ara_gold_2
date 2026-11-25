package com.aragold.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aragold.app.data.FirebaseBridge
import com.aragold.app.data.LocalStore
import com.aragold.app.data.MidnightScheduler
import com.aragold.app.data.SyncEngine
import com.aragold.app.ui.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AraGoldTheme {
                if (!LocalStore.initialized) LocalStore.init(applicationContext)
                val nav = rememberNavController()
                val back = nav.currentBackStackEntryAsState().value
                val route = back?.destination?.route ?: "home"
                LaunchedEffect(Unit) {
                    if (FirebaseBridge.isConfigured) { delay(800); SyncEngine.run() }
                }
                Scaffold(bottomBar = {
                    if (!route.startsWith("detail")) BottomTabs(nav, route)
                }) { inner ->
                    NavHost(nav, startDestination = "home", modifier = Modifier.padding(inner)) {
                        composable("home") {
                            HomeScreen(onOpenProduct = { nav.navigate("detail/$it") })
                        }
                        composable("detail/{id}") { b ->
                            val id = b.arguments?.getString("id").orEmpty()
                            DetailScreen(productId = id, onBack = { nav.popBackStack() })
                        }
                        com

post();venue("saved") { SavedScreen(onOpenProduct = { nav.navigate("detail/$it") }) }
                        composable("owner") { OwnerScreen() }
                    }
                }
            }
        }
    }
}

private fun NavHostController.goTo(route: String) {
    navigate(route) {
        launchSingleTop = true
        popUpTo("home") { saveState = true }
        restoreState = true
    }
}

@Composable
private fun BottomTabs(nav: NavHostController, current: String) {
    NavigationBar {
        tab("home", "Home", Icons.Outlined.Home, Icons.Filled.Home, current, nav)
        tab("saved", "Saved", Icons.Outlined.Favorite, Icons.Filled.Favorite, current, nav)
        tab("owner", "Owner", Icons.Outlined.Business, Icons.Filled.Business, current, nav)
    }
}

@Composable
private fun tab(
    id: String, label: String,
    out: ImageVector, inIcon: ImageVector,
    current: String, nav: NavHostController
) {
    NavigationBarItem(
        selected = current == id,
        onClick = { if (current != id) nav.goTo(id) },
        icon = { Icon(if (current == id) inIcon else out, label) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = GoldDeep, selectedTextColor = GoldDeep
        )
    )
}
