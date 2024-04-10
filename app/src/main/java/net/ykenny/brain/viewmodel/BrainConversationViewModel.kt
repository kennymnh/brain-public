package net.ykenny.brain.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.ykenny.brain.database.BrainDatabase
import net.ykenny.brain.entity.BrainConversationEntity
import net.ykenny.brain.entity.BrainMessageEntity
import net.ykenny.brain.model.OpenAiMessageBody
import net.ykenny.brain.model.request.BodyToSend
import net.ykenny.brain.repository.BrainConversationMessageRepository
import net.ykenny.brain.repository.BrainConversationRepository
import net.ykenny.brain.repository.OpenAiRepository

class BrainConversationViewModel(
    private val applicationContext: Context,
    var conversationId: Long
) : ViewModel() {
    private lateinit var database: BrainDatabase
    private lateinit var conversationRepository: BrainConversationRepository
    private lateinit var messageRepository: BrainConversationMessageRepository
    private val openAiRepository = OpenAiRepository()

    val conversationEntity = mutableStateOf(BrainConversationEntity(0, ""))
    val conversationMessages = mutableStateListOf<BrainMessageEntity>()

    var isFetching = mutableStateOf(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            database = BrainDatabase.getDatabase(applicationContext)

            conversationRepository = BrainConversationRepository(database.brainDAO())

            if (conversationId == 0L)
                conversationId = conversationRepository.insertConversation(BrainConversationEntity(0, "Conversation"))

            conversationEntity.value = conversationRepository.getConversationById(conversationId)

            messageRepository = BrainConversationMessageRepository(database.brainDAO())
            loadMessages()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            conversationMessages.clear()
            conversationMessages.addAll(messageRepository.getMessages(conversationId))
        }
    }

    fun fetchMessages() {
        isFetching.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val messages = conversationMessages.map {
                    OpenAiMessageBody(
                        role = it.role,
                        content = it.message
                    )
                }

                val bodyToSend = BodyToSend(messages = messages)
                val response = openAiRepository.getChatFromOpenAi(bodyToSend)
                val generatedAnswer = response.choices.first().message

                insertMessage(generatedAnswer.role, generatedAnswer.content)
                isFetching.value = false
            } catch (e: Exception) {
                Log.e("fetchMessages", e.message.toString())
            }
        }
    }

    fun insertMessage(role: String, message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.insertMessage(BrainMessageEntity(0, conversationId, role, message))
            loadMessages()
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val msg = BrainMessageEntity(0, conversationId, "user", message)

            messageRepository.insertMessage(msg)
            conversationMessages.add(msg)
            fetchMessages()
        }
    }

    fun updateConversationName(conversationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationRepository.updateConversationName(conversationId, conversationName)
            conversationEntity.value = conversationRepository.getConversationById(conversationId)
        }
    }

    fun deleteConversation(next: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            conversationRepository.deleteConversationById(conversationId)
            conversationRepository.deleteMessagesByConversationId(conversationId)
            next()
        }
    }
}