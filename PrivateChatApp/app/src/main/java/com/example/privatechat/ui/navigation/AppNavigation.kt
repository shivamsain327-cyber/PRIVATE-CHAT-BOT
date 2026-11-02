package com.example.privatechat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.privatechat.data.model.CallType
import com.example.privatechat.data.repository.ChatRepository
import com.example.privatechat.ui.screens.AuthScreen
import com.example.privatechat.ui.screens.CallScreen
import com.example.privatechat.ui.screens.ChatDetailScreen
import com.example.privatechat.ui.screens.ChatListScreen
import com.example.privatechat.ui.screens.PinLockScreen

object Destinations {
    const val PIN_LOCK = "pin_lock"
    const val AUTH = "auth"
    const val CHAT_LIST = "chat_list"
    const val CHAT_DETAIL = "chat_detail/{friendId}"
    const val CALL = "call"
}

@Composable
fun AppNavigation(
    chatRepository: ChatRepository
) {
    val navController = rememberNavController()
    val friends by chatRepository.friends.collectAsState()

    val startDestination = if (chatRepository.isPinSetup()) {
        Destinations.PIN_LOCK
    } else {
        Destinations.AUTH
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Destinations.PIN_LOCK) {
            PinLockScreen(
                chatRepository = chatRepository,
                onUnlocked = {
                    navController.navigate(Destinations.CHAT_LIST) {
                        popUpTo(Destinations.PIN_LOCK) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.AUTH) {
            AuthScreen(
                chatRepository = chatRepository,
                onContinue = {
                    navController.navigate(Destinations.PIN_LOCK) {
                        popUpTo(Destinations.AUTH) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.CHAT_LIST) {
            ChatListScreen(
                chatRepository = chatRepository,
                onFriendSelected = { friend ->
                    navController.navigate("chat_detail/${friend.id}")
                },
                onLockApp = {
                    navController.navigate(Destinations.PIN_LOCK) {
                        popUpTo(Destinations.CHAT_LIST) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Destinations.CHAT_DETAIL,
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId")
            val friend = friends.firstOrNull { it.id == friendId } ?: friends.first()

            ChatDetailScreen(
                friend = friend,
                chatRepository = chatRepository,
                onBack = { navController.popBackStack() },
                onStartCall = { callType: CallType ->
                    chatRepository.startCall(friend, callType)
                    navController.navigate(Destinations.CALL)
                }
            )
        }

        composable(Destinations.CALL) {
            CallScreen(
                chatRepository = chatRepository,
                onCallEnded = {
                    navController.popBackStack()
                }
            )
        }
    }
}
