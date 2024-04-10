package net.ykenny.brain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.ykenny.brain.entity.BrainConversationEntity
import net.ykenny.brain.entity.BrainMessageEntity

@Dao
interface BrainDAO {
    @Query("SELECT * FROM brain_conversations ORDER BY conversationId DESC")
    fun getConversations(): List<BrainConversationEntity>

    @Query("DELETE FROM brain_conversations")
    suspend fun deleteAllConversations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(brainConversationEntity: BrainConversationEntity): Long

    @Query("SELECT * FROM brain_conversations WHERE conversationId = :conversationId")
    fun getConversationById(conversationId: Long): BrainConversationEntity

    @Query("UPDATE brain_conversations SET conversationName = :conversationName WHERE conversationId = :conversationId")
    suspend fun updateConversationName(conversationId: Long, conversationName: String)

    @Query("DELETE FROM brain_conversations WHERE conversationId = :conversationId")
    suspend fun deleteConversationById(conversationId: Long)

    @Query("SELECT * FROM brain_messages WHERE conversationId = :conversationId ORDER BY messageId")
    fun getMessages(conversationId: Long): List<BrainMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(brainMessageEntity: BrainMessageEntity): Long

    @Query("DELETE FROM brain_messages WHERE conversationId = :conversationId")
    suspend fun deleteMessagesByConversationId(conversationId: Long)

    @Query("DELETE FROM brain_messages")
    suspend fun deleteAllMessages()
}