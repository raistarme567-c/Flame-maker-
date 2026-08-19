package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.*
import com.example.ui.theme.CanvasDark
import com.example.ui.theme.FlameMakerTheme
import com.example.ui.viewmodel.EngineViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EngineViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlameMakerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CanvasDark
                ) {
                    FlameMakerApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun FlameMakerApp(viewModel: EngineViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onOpenProject = {
                    navController.navigate("editor")
                },
                onOpenWizard = {
                    navController.navigate("game_wizard")
                },
                onOpenBuildCenter = {
                    navController.navigate("build_center")
                },
                onOpenSettings = {
                    navController.navigate("settings")
                },
                onOpenAuth = {
                    navController.navigate("phone_auth")
                },
                onOpenCommunityFeed = {
                    navController.navigate("community_feed")
                }
            )
        }

        composable("editor") {
            EditorScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenBuildCenter = {
                    navController.navigate("build_center")
                }
            )
        }

        composable("build_center") {
            BuildExportScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("game_wizard") {
            GameWizardScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onProjectCreated = {
                    navController.navigate("editor") {
                        popUpTo("home")
                    }
                }
            )
        }

        composable("settings") {
            SettingsAndDocsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("phone_auth") {
            PhoneAuthScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("community_feed") {
            CommunityFeedScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenInEditor = { proj ->
                    viewModel.openProject(proj)
                    navController.navigate("editor")
                }
            )
        }
    }
}

