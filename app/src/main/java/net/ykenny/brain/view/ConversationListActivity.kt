package net.ykenny.brain.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import net.ykenny.brain.entity.BrainConversationEntity
import net.ykenny.brain.ui.theme.BrAInTheme
import net.ykenny.brain.viewmodel.BrainConversationListViewModel

class ConversationListActivity : ComponentActivity() {
    private lateinit var conversationListViewModel: BrainConversationListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        conversationListViewModel = BrainConversationListViewModel(applicationContext)
        conversationListViewModel.loadConversations()

        setContent {
            BrAInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    @Composable
    fun MainScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = {
                        Text("Conversations")
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                Box {
                    Conversations(innerPadding)
                    BottomButtons()
                }
            }
        }
    }

    @Composable
    fun BottomButtons() {
        val context = LocalContext.current

        Box(
            Modifier
                .fillMaxSize()
                .zIndex(2f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.weight(1f))

                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f),
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .padding(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                context.startActivity(Intent(context, ConversationActivity::class.java))
                            }, Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = "Nouvelle conversation"
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Chat",
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.weight(0.05f))

                        var showDeleteAllAlertDialog by remember { mutableStateOf(false) }
                        if (showDeleteAllAlertDialog) {
                            AlertDialog(
                                title = { Text("Confirmation") },
                                text = { Text("Voulez-vous vraiment effacer toutes les conversations ?") },
                                onDismissRequest = { showDeleteAllAlertDialog = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        conversationListViewModel.deleteAllConversations()
                                        showDeleteAllAlertDialog = false
                                    }) {
                                        Text("Oui")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteAllAlertDialog = false }) {
                                        Text("Non")
                                    }
                                }
                            )
                        }

                        Button(
                            onClick = {
                                showDeleteAllAlertDialog = true
                            }, Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Supprimer toutes les conversations")
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Effacer",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Conversations(innerPadding: PaddingValues) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp)
                .zIndex(1f)
        ) {
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                items(conversationListViewModel.conversations) { conversation ->
                    ConversationItem(conversation)
                }
            }
        }
    }

    @Composable
    fun ConversationItem(conversation: BrainConversationEntity) {
        val context = LocalContext.current

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                .padding(8.dp, 0.dp)
        ) {

            TextButton(
                onClick = {
                    context.startActivity(Intent(context, ConversationActivity::class.java).apply {
                        putExtra("conversationId", conversation.conversationId)
                    })
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            ) {
                Text(conversation.conversationName)
                Spacer(modifier = Modifier.weight(1f))
            }

            IconButton(
                onClick = { conversationListViewModel.deleteConversationById(conversation.conversationId) },
                modifier = Modifier
                    .size(48.dp)
                    .height(IntrinsicSize.Max)
                    .padding(0.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Supprimer la conversation"
                    )
                }
            )
        }
    }
}