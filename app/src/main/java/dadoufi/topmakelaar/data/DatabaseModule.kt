/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dadoufi.topmakelaar.data


import android.content.Context
import android.os.Debug
import androidx.room.Room
import dadoufi.topmakelaar.data.daos.LastRequestDao
import dadoufi.topmakelaar.data.daos.MakelaarDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(context: Context): MakelaarDb {
        val builder = Room.databaseBuilder(context, MakelaarDb::class.java, "makelaar.db")
            .fallbackToDestructiveMigration()
        if (Debug.isDebuggerConnected()) {
            builder.allowMainThreadQueries()
        }
        return builder.build()
    }


    @Provides
    fun provideUserDao(db: MakelaarDb): MakelaarDao = db.makelaarDao()

    @Provides
    fun provideLastRequestDao(db: MakelaarDb): LastRequestDao = db.lastRequestDao()


    @Singleton
    @Provides
    fun provideDatabaseTransactionRunner(db: MakelaarDb): DatabaseTransactionRunner =
        RoomTransactionRunner(db)
}