package net.ykenny.brain.repository

import androidx.annotation.WorkerThread
import net.ykenny.brain.dao.BrainDAO
import net.ykenny.brain.entity.BrainConversationEntity

class BrainConversationRepository(private val brainDAO: BrainDAO) {
    @WorkerThread
    fun getConversations() = brainDAO.getConversations()

    @WorkerThread
    suspend fun deleteAllConversations() = brainDAO.deleteAllConversations()

    @WorkerThread
    suspend fun insertConversation(conversationEntity: BrainConversationEntity) = brainDAO.insertConversation(conversationEntity)

    @WorkerThread
    fun getConversationById(conversationId: Long) = brainDAO.getConversationById(conversationId)

    @WorkerThread
    suspend fun updateConversationName(conversationId: Long, conversationName: String) = brainDAO.updateConversationName(conversationId, conversationName)

    @WorkerThread
    suspend fun deleteConversationById(conversationId: Long) = brainDAO.deleteConversationById(conversationId)

    @WorkerThread
    suspend fun deleteMessagesByConversationId(conversationId: Long) = brainDAO.deleteMessagesByConversationId(conversationId)

    @WorkerThread
    suspend fun deleteAllMessages() = brainDAO.deleteAllMessages()
}