package net.ykenny.brain.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import net.ykenny.brain.entity.BrainMessageEntity
import net.ykenny.brain.view.ui.theme.BrAInTheme
import net.ykenny.brain.viewmodel.BrainConversationViewModel

class ConversationActivity : ComponentActivity() {
    private lateinit var brainConversationViewModel: BrainConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val conversationId = intent.getLongExtra("conversationId", 0)
        brainConversationViewModel = BrainConversationViewModel(applicationContext, conversationId)

        setContent {
            BrAInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConversationScreen()
                }
            }
        }
    }

    private fun handleSendMessage(message: String): Boolean {
        if (message.trim().isEmpty()) return false

        brainConversationViewModel.sendMessage(message.trim())
        return true
    }

    private fun copyToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Copied Text", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    private fun shareText(context: Context, text: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    @Composable
    private fun MessageOptionsDialog(context: Context, message: BrainMessageEntity, dismiss: () -> Unit) {
        AlertDialog(
            title = { Text("Message") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Que souhaitez-vous faire avec ce message ?")
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                        TextButton(
                            content = { Text("Copier") },
                            onClick = {
                                copyToClipboard(context, message.message)
                                Toast.makeText(context, "Message copié", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                        )
                        TextButton(
                            content = { Text("Partager") },
                            onClick = {
                                shareText(context, message.message)
                                dismiss()
                            }
                        )
                    }
                }
            },
            onDismissRequest = { dismiss() },
            confirmButton = {
                TextButton(onClick = { dismiss() }) { Text("Annuler") }
            }
        )
    }

    @Composable
    fun ConversationScreen() {
        var showAlertDialog by remember { mutableStateOf(false) }
        if (showAlertDialog) {
            var conversationName by remember { mutableStateOf(TextFieldValue()) }

            AlertDialog(
                title = { Text("Nom de la conversation") },
                text = {
                    TextField(
                        value = conversationName,
                        onValueChange = { conversationName = it },
                        placeholder = { Text(brainConversationViewModel.conversationEntity.value.conversationName) },
                        textStyle = MaterialTheme.typography.bodyLarge,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            autoCorrect = true,
                            capitalization = KeyboardCapitalization.Sentences,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        )
                    )
                },
                onDismissRequest = { showAlertDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        brainConversationViewModel.updateConversationName(conversationName.text.trim())
                        showAlertDialog = false
                    }) {
                        Text("Valider")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAlertDialog = false }) {
                        Text("Annuler")
                    }
                }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                    title = { Text(brainConversationViewModel.conversationEntity.value.conversationName) },
                    navigationIcon = {
                        IconButton(
                            onClick = { finish() },
                            content = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Retour",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { showAlertDialog = true },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Nom de la conversation",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                        IconButton(
                            onClick = {
                                brainConversationViewModel.deleteConversation()
                                finish()
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Supprimer la conversation",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .fillMaxSize()
            ) {

                val lazyColumnListState = rememberLazyListState()
                val coroutineScope = rememberCoroutineScope()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = innerPadding,
                    state = lazyColumnListState
                ) {
                    coroutineScope.launch {
                        val scrollTo = brainConversationViewModel.conversationMessages.size - 1

                        lazyColumnListState.animateScrollToItem(if (scrollTo < 0) 0 else scrollTo)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    items(brainConversationViewModel.conversationMessages) { message ->
                        MessageItem(message)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                MessageInput()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun MessageItem(message: BrainMessageEntity, isSpeech: Boolean = false) {
        val isAi = message.role == "assistant"

        val horizontalArrangement = if (isAi)
            Arrangement.Start
        else
            Arrangement.End

        val backgroundColor = if (isAi)
            MaterialTheme.colorScheme.surfaceContainer
        else
            MaterialTheme.colorScheme.primaryContainer

        val borderColor = if (isAi)
            MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.1f)
        else
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

        val textColor = if (isAi)
            MaterialTheme.colorScheme.onSurface
        else
            MaterialTheme.colorScheme.onPrimaryContainer

        val leftPadding = if (isAi) 16.dp else 32.dp
        val rightPadding = if (isAi) {
            32.dp
        } else if (isSpeech) 0.dp else 16.dp

        val context = LocalContext.current
        var showOptionsDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(leftPadding, 0.dp, rightPadding, 0.dp),
            horizontalArrangement = horizontalArrangement,
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, borderColor),
                color = backgroundColor,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 12.dp)
            ) {
                Text(
                    text = message.message,
                    modifier = Modifier
                        .padding(10.dp, 8.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { showOptionsDialog = true }
                        ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
        }

        if (showOptionsDialog)
            MessageOptionsDialog(context = context, message = message, dismiss = { showOptionsDialog = false })
    }

    @Composable
    fun MessageInput(modifier: Modifier = Modifier) {
        var message by remember { mutableStateOf(TextFieldValue()) }
        val isFetching by brainConversationViewModel.isFetching

        val buttonBackgroundColor = if (isFetching)
            MaterialTheme.colorScheme.surfaceContainer
        else
            MaterialTheme.colorScheme.primary

        val buttonForegroundColor = if (isFetching)
            MaterialTheme.colorScheme.onSurface
        else
            MaterialTheme.colorScheme.onPrimary

        if (isFetching)
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        else
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), progress = { 0f })

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(16.dp, 12.dp)
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Rédiger un message...") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    capitalization = KeyboardCapitalization.Sentences,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (handleSendMessage(message.text)) {
                            message = TextFieldValue()
                        }
                    }
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(100),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                )
            )

            Spacer(modifier = Modifier.size(8.dp))

            IconButton(
                onClick = {
                    if (handleSendMessage(message.text)) {
                        message = TextFieldValue()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .height(IntrinsicSize.Max)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(100))
                    .background(buttonBackgroundColor),
                content = {
                    if (isFetching)
                        CircularProgressIndicator(color = buttonForegroundColor)
                    else
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = buttonForegroundColor
                        )
                },
                enabled = !isFetching
            )
        }
    }
}