package com.arca.android.ui.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arca.android.crypto.CryptoManager
import com.arca.android.data.repository.Credential
import com.arca.android.ui.screens.login.LoginScreen
import com.arca.android.ui.screens.login.LoginViewModel
import com.arca.android.ui.screens.settings.SettingsScreen
import com.arca.android.ui.screens.sync.SyncLogsScreen
import com.arca.android.ui.screens.unlock.UnlockScreen
import com.arca.android.ui.screens.unlock.UnlockViewModel
import com.arca.android.ui.screens.vault.VaultScreen
import com.arca.android.ui.screens.vault.VaultViewModel

/**
 * Navigation routes for the app.
 */
object Routes {
    const val LOGIN = "login"
    const val UNLOCK = "unlock"
    const val VAULT = "vault"
    const val SYNC_LOGS = "sync_logs"
    const val SETTINGS = "settings"
}

/**
 * Main navigation graph.
 * Handles the flow: Login → Unlock → Vault (with Sync Logs & Settings as sub-routes).
 *
 * State management:
 * - DerivedKeys are passed in memory (not serialized) between Login → Unlock → Vault.
 * - The vault key only exists in memory — it's never saved to disk.
 */
@Composable
fun ArcaNavGraph() {
    val navController = rememberNavController()

    // In-memory state — not serializable, intentionally volatile
    var derivedKeys by remember { mutableStateOf<CryptoManager.DerivedKeys?>(null) }
    var userEmail by remember { mutableStateOf("") }
    var vaultCredentials by remember { mutableStateOf<List<Credential>>(emptyList()) }
    var vaultKey by remember { mutableStateOf<javax.crypto.SecretKey?>(null) }

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN,
    ) {
        // ── Login ──
        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = hiltViewModel()

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    derivedKeys = loginViewModel.derivedKeys
                    userEmail = loginViewModel.uiState.value.email
                    navController.navigate(Routes.UNLOCK) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
            )
        }

        // ── Unlock ──
        composable(Routes.UNLOCK) {
            val unlockViewModel: UnlockViewModel = hiltViewModel()

            UnlockScreen(
                email = userEmail,
                derivedKeys = derivedKeys,
                viewModel = unlockViewModel,
                onUnlockSuccess = {
                    vaultCredentials = unlockViewModel.credentials
                    vaultKey = unlockViewModel.vaultKey
                    derivedKeys = null // Clear keys after use
                    navController.navigate(Routes.VAULT) {
                        popUpTo(Routes.UNLOCK) { inclusive = true }
                    }
                },
            )
        }

        // ── Vault ──
        composable(Routes.VAULT) {
            val vaultViewModel: VaultViewModel = hiltViewModel()

            // Initialize with credentials from unlock
            LaunchedEffect(Unit) {
                vaultViewModel.vaultKey = vaultKey
                vaultViewModel.setInitialCredentials(vaultCredentials)
            }

            VaultScreen(
                viewModel = vaultViewModel,
                onSyncLogsClick = { navController.navigate(Routes.SYNC_LOGS) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onLogout = {
                    vaultKey = null
                    vaultCredentials = emptyList()
                    derivedKeys = null
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        // ── Sync Logs ──
        composable(Routes.SYNC_LOGS) {
            SyncLogsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Settings ──
        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    vaultKey = null
                    vaultCredentials = emptyList()
                    derivedKeys = null
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
