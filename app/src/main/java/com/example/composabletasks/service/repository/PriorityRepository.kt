package com.example.composabletasks.service.repository

import android.content.Context
import com.example.composabletasks.service.model.PriorityModel
import com.example.composabletasks.service.repository.local.TaskDatabase
import com.example.composabletasks.service.repository.remote.PriorityService
import com.example.composabletasks.service.repository.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class PriorityRepository(context: Context) {
    private val remote = RetrofitClient.getService(PriorityService::class.java)
    private val database = TaskDatabase.getDatabase(context).priorityDAO()

    companion object {
        private val cache = mutableMapOf<Int, String>()

        fun setCacheDescription(id: Int, value: String) {
            cache[id] = value
        }

        fun getCacheDescription(id: Int) = cache[id] ?: ""

        fun clearCache() {
            cache.clear()
        }
    }

    suspend fun getList(): Response<List<PriorityModel>> {
        return remote.getList()
    }

    fun list(): Flow<List<PriorityModel>> {
        return database.list()
    }

    suspend fun getPriorityDescription(id: Int): String {
        val cached = getCacheDescription(id)
        if (cached != "") {
            return cached
        }

        val description = database.getDescription(id) ?: ""
        if (description != "") {
            setCacheDescription(id, description)
        }
        return description
    }

    suspend fun save(list: List<PriorityModel>) {
        clearCache()
        database.clear()
        database.save(list)
    }
}