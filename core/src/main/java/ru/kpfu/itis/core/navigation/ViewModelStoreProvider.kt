package ru.kpfu.itis.core.navigation

import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class ViewModelStoreProvider {
    private val stores = mutableMapOf<String, ViewModelStore>()

    fun get(key: NavKey): ViewModelStoreOwner {
        val keyString = key.toString()
        val store = stores.getOrPut(keyString) { ViewModelStore() }
        return object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = store
        }
    }


    fun clearStore(key: NavKey) {
        val keyString = key.toString()
        stores[keyString]?.clear()
        stores.remove(keyString)
    }

    fun clearAllStores() {
        stores.values.forEach { it.clear() }
        stores.clear()
    }
}