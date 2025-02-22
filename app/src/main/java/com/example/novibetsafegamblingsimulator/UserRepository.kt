package com.example.novibetsafegamblingsimulator

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.util.Date

class UserRepository {
    val client = createSupabaseClient(
        supabaseUrl = "https://fnyguyvlwoixbtmfdala.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZueWd1eXZsd29peGJ0bWZkYWxhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Mzk5Nzg3MTMsImV4cCI6MjA1NTU1NDcxM30.nyyirErozsVak5S93zTI8hX8R23NxNttu-NTMdjQpEI"
    ) {
        install(Postgrest)
    }

    suspend fun authenticateUser(username: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            val response = client.from("customers").select(){
                filter {
                    eq("username", username)
                    eq("password", password)
                }
            }.decodeSingleOrNull<User>()

            response
        }
    }

    suspend fun findUser(username: String): User? {
        return withContext(Dispatchers.IO) {
            val response = client.from("customers").select(){
                filter {
                    eq("username", username)
                }
            }.decodeSingleOrNull<User>()

            response
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun createUser(username: String, password: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val newUser = NewUser(0, 0, 0.0f, username, password)
                client.from("customers").insert(newUser)
                val user: User? = findUser(username)
                user
            } catch (e: Exception) {
                Log.e("UserRepository", "Error creating user", e)
                null
            }
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun updateBalance(userId: Int, newBalance: Float): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                client.from("customers").update(
                    {
                        set("total_remain", newBalance)
                    }
                ) {
                    filter {
                        eq("customer_id", userId)
                    }
                }
                true
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating balance", e)
                false
            }
        }
    }
}


@Serializable
data class databaseInfo(
    val id: Int,
    val num_of_customers: Int
)

@Serializable
data class NewUser(
    val age_band: Int,
    val gender: Int,
    val total_remain: Float,
    val username: String,
    val password: String
)

@Serializable
data class User(
    val customer_id: Int,
    val age_band: Int,
    val gender: Int,
    val total_remain: Float,
    val username: String,
    val password: String
)

@Serializable
data class Transaction(
    val transaction_id: Int,
    val customer_id: Int,
    val data: String
)

