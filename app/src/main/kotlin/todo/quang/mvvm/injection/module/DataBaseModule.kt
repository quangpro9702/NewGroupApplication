package todo.quang.mvvm.injection.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.model.database.AppDatabase
import todo.quang.mvvm.model.database.MIGRATION_1_2
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DataBaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build()
    }


    @Singleton
    @Provides
    fun provideAppInfoDao(database: AppDatabase): AppInfoDao = database.appInfoDao()
}