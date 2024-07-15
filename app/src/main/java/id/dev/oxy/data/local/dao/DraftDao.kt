package id.dev.oxy.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.dev.oxy.data.local.entity.DraftTable

@Dao
interface DraftDao {
    @Query("SELECT * from draft_table")
    fun getDrafts(): LiveData<List<DraftTable>>

    @Query("SELECT * from draft_table WHERE id == :id")
    suspend fun getDraftsById(id: Int): DraftTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draftTable: DraftTable)

    @Query("DELETE FROM draft_table")
    suspend fun deleteDrafts()
}