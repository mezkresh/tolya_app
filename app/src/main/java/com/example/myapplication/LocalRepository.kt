package com.example.myapplication

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import kotlin.coroutines.CoroutineContext



class LocalRepository(private val context: Context) {

    companion object{
        const val CACHE_FILE_NAME = "SUKA_BLYAT"
    }
    private val saverContext: CoroutineContext = Dispatchers.IO

    suspend fun saveUserData(user: User){
        withContext(saverContext){
            writeObjectToFile(CACHE_FILE_NAME, obj = user)
        }
    }

    suspend fun getUserData(): User? {
        return withContext(saverContext) {
            runCatching {
                val userDataFile = File(context.cacheDir, CACHE_FILE_NAME)
                readObjectFromFile<User?>(userDataFile)
            }.getOrNull()
        }
    }

    fun writeObjectToFile(name: String, directory: File = context.cacheDir, obj: Any?) {
        val file = File(directory, name)
        file.createNewFile()

        ObjectOutputStream(FileOutputStream(file, false)).use { outStream ->
            outStream.writeObject(obj)
        }
    }

    inline fun <reified T> readObjectFromFile(file: File): T? {
        return ObjectInputStream(FileInputStream(file)).use { inStream ->
            inStream.readObject() as? T
        }
    }


}