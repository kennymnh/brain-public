package net.ykenny.brain.repository

import androidx.annotation.WorkerThread
import net.ykenny.brain.dao.BrainDAO
import net.ykenny.brain.entity.BrainMessageEntity

class BrainConversationMessageRepository(private val brainDAO: BrainDAO) {
    @WorkerThread
    fun getMessages(conversationId: Long) = brainDAO.getMessages(conversationId)

    @WorkerThread
    suspend fun insertMessage(brainMessageEntity: BrainMessageEntity) = brainDAO.insertMessage(brainMessageEntity)
}