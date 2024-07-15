package id.dev.oxy.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import id.dev.oxy.data.local.OxyDb
import id.dev.oxy.data.local.dao.CustomerDao
import id.dev.oxy.data.local.dao.DraftDao
import id.dev.oxy.data.local.dao.ProductDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): OxyDb {
        return Room.databaseBuilder(
            context.applicationContext,
            OxyDb::class.java,
            "oxy_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun provideProductDao(storyDb: OxyDb): ProductDao {
        return storyDb.productDao()
    }

    @Singleton
    @Provides
    fun provideCustomerDao(storyDb: OxyDb): CustomerDao {
        return storyDb.customerDao()
    }

    @Singleton
    @Provides
    fun provideDraftDao(storyDb: OxyDb): DraftDao {
        return storyDb.draftDao()
    }
}