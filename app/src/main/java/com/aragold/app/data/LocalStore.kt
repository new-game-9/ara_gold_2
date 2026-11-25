package com.aragold.app.data

import android.content.Context
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import java.io.File

object LocalStore {
    private val json = Json { ignoreUnknownKeys = true }
    private var file: File? = null
    private var savedFile: File? = null
    @Volatile var initialized = false; private set

    private data class Cache(val lastSync: Long, val products: List<GoldProduct>)

    private val _products = MutableStateFlow<List<GoldProduct>>(emptyList())
    val products = _products.asStateFlow()

    private val _saved = MutableStateFlow<Set<String>>(emptySet())
    val saved = _saved.asStateFlow()

    fun init(ctx: Context) {
        if (initialized) return
        file = File(ctx.filesDir, "catalog.json")
        savedFile = File(ctx.filesDir, "saved.json")
        _saved.value = runCatching { json.decodeFromString<Set<String>>(savedFile!!.readText()) }
            .getOrDefault(emptySet())
        val cache = runCatching { json.decodeFromString<Cache>(file!!.readText()) }.getOrNull()
        if (cache == null) {
            val seeds = SeedData.build()
            write(0L, seeds)
        } else {
            _products.value = cache.products.filterNot { it.deleted }
        }
        initialized = true
    }

    fun lastSync(): Long =
        runCatching { json.decodeFromString<Cache>(file!!.readText()).lastSync }.getOrDefault(0L)

    fun merge(incoming: List<GoldProduct>) {
        val map = _products.value.associateBy { it.id }.toMutableMap()
        for (p in incoming) if (p.deleted) map.remove(p.id) else map[p.id] = p
        write(System.currentTimeMillis(), map.values.toList())
    }

    fun upsertLocal(p: GoldProduct) {
        val map = _products.value.associateBy { it.id }.toMutableMap()
        map[p.id] = p
        write(lastSync(), map.values.toList())
    }

    fun removeLocal(id: String) =
        write(lastSync(), _products.value.filterNot { it.id == id })

    fun toggleSaved(id: String) {
        val next = _saved.value.toMutableSet()
        if (!next.add(id)) next.remove(id)
        _saved.value = next
        runCatching { savedFile!!.writeText(json.encodeToString(next)) }
    }

    private fun write(lastSync: Long, list: List<GoldProduct>) {
        val tmp = File(file!!.parentFile, "catalog.json.tmp")
        tmp.writeText(json.encodeToString(Cache(lastSync, list)))
        if (!tmp.renameTo(file!!)) { file!!.delete(); tmp.copyTo(file!!, overwrite = true) }
        _products.value = list.filterNot { it.deleted }
    }
}
