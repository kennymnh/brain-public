package net.ykenny.brain.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.ykenny.brain.database.BrainDatabase
import net.ykenny.brain.entity.BrainConversationEntity
import net.ykenny.brain.repository.BrainConversationRepository

class BrainConversationListViewModel(private val applicationContext: Context) : ViewModel() {
    private lateinit var database: BrainDatabase
    private lateinit var repository: BrainConversationRepository

    val conversations = mutableStateListOf<BrainConversationEntity>()

    init {
        viewModelScope.launch {
            database = BrainDatabase.getDatabase(applicationContext)
            repository = BrainConversationRepository(database.brainDAO())
        }
    }

    fun loadConversations() {
        viewModelScope.launch(Dispatchers.IO) {
            conversations.clear()
            conversations.addAll(repository.getConversations())
        }
    }

    fun insertConversation(conversationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertConversation(BrainConversationEntity(0, conversationName))
            loadConversations()
        }
    }

    fun deleteConversationById(conversationId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteConversationById(conversationId)
            repository.deleteMessagesByConversationId(conversationId)
            loadConversations()
        }
    }

    fun deleteAllConversations() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllConversations()
            repository.deleteAllMessages()
            loadConversations()
        }
    }
}