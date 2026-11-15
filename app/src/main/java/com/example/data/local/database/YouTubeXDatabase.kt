package com.example.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        VideoInteractionEntity::class,
        ChannelSubscriptionEntity::class,
        WatchHistoryEntity::class,
        PlaylistEntity::class,
        PlaylistVideoCrossRef::class,
        SearchHistoryEntity::class,
        CommentEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class YouTubeXDatabase : RoomDatabase() {
    abstract fun youTubeXDao(): YouTubeXDao

    companion object {
        @Volatile
        private var INSTANCE: YouTubeXDatabase? = null

        fun getInstance(context: Context): YouTubeXDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    YouTubeXDatabase::class.java,
                    "youtubex_database.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
