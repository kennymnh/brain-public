package net.ykenny.brain.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.ykenny.brain.dao.BrainDAO
import net.ykenny.brain.entity.BrainConversationEntity
import net.ykenny.brain.entity.BrainMessageEntity

@Database(entities = [BrainConversationEntity::class, BrainMessageEntity::class], version = 1)
abstract class BrainDatabase : RoomDatabase() {
    abstract fun brainDAO(): BrainDAO

    companion object {
        @Volatile
        private var INSTANCE: BrainDatabase? = null

        fun getDatabase(context: Context): BrainDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BrainDatabase::class.java,
                    "brain_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance

                instance
            }
        }
    }
}