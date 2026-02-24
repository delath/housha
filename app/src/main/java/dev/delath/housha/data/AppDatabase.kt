package dev.delath.housha.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.delath.housha.model.Anime

@Database(entities = [Anime::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscribedAnimeDao(): SubscribedAnimeDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE subscribed_anime ADD COLUMN averageScore INTEGER")
                db.execSQL("ALTER TABLE subscribed_anime ADD COLUMN status TEXT")
                db.execSQL("ALTER TABLE subscribed_anime ADD COLUMN studio TEXT")
            }
        }
    }
}
