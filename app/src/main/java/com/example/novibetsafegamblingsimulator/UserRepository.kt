package com.example.novibetsafegamblingsimulator

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

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
    suspend fun updateWinLoss(userId: Int, date: String?, newWinLoss: Float?){
        return withContext(Dispatchers.IO) {
            try {
                client.from("budget_data").update(
                    {
                        set("win_loss", newWinLoss)
                    }
                ) {
                    filter {
                        eq("customer_id", userId)
                        if (date != null) {
                            eq("date", date)
                        }
                    }
                }
                true
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating balance", e)
                false
            }
        }
    }

    suspend fun findBudgetData(userId: Int, date: String?): Budget_data? {
        return withContext(Dispatchers.IO) {
            val response = client.from("budget_data").select(){
                filter {
                    eq("customer_id", userId)
                    if (date != null) {
                        eq("date", date)
                    }
                }
            }.decodeSingleOrNull<Budget_data>()

            response
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun updateWage(userId: Int, date: String?, newWage: Float?){
        return withContext(Dispatchers.IO) {
            try {
                client.from("budget_data").update(
                    {
                        set("wage", newWage)
                    }
                ) {
                    filter {
                        eq("customer_id", userId)
                        if (date != null) {
                            eq("date", date)
                        }
                    }
                }
                true
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating balance", e)
                false
            }
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun updateFinalBalance(userId: Int, date: String?, newBalance: Float?){
        return withContext(Dispatchers.IO) {
            try {
                client.from("budget_data").update(
                    {
                        set("balance", newBalance)
                    }
                ) {
                    filter {
                        eq("customer_id", userId)
                        if (date != null) {
                            eq("date", date)
                        }
                    }
                }
                true
            } catch (e: Exception) {
                Log.e("UserRepository", "Error updating balance", e)
                false
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

    @OptIn(UnstableApi::class)
    suspend fun insertBudgetData(customer_id: Int, date: String?, wage: Float?, win_loss: Float?, balance: Float?): Budget_data?{
        return withContext(Dispatchers.IO) {
            val newBudgetData = Budget_data(customer_id, date, win_loss, wage, balance)
            client.from("budget_data").insert(newBudgetData){
                select()
            }.decodeSingle<Budget_data>()
        }
    }

    @OptIn(UnstableApi::class)
    suspend fun insertTransaction(customer_id: Int, date:String?, event_type: Int, initial_balance: Float?, ending_balance: Float?, game_type: Int?, wager_amount: Float?, win_loss: Boolean?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val newTransaction = NewTransaction(customer_id, date, event_type, initial_balance, ending_balance)
                val transaction: Transaction = client.from("transactions").insert(newTransaction) {
                    select()
                }.decodeSingle<Transaction>()

                var budget_data = findBudgetData(transaction.customer_id, transaction.date)

                if(budget_data == null){
                    budget_data = insertBudgetData(transaction.customer_id, transaction.date, 0.0f, 0.0f, transaction.ending_balance)
                } else{
                    updateFinalBalance(transaction.customer_id, transaction.date, transaction.ending_balance)
                }

                if(transaction.event_type == 0){
                    // A bet happened
                    if (ending_balance != null && initial_balance != null) {
                        val balanceChange: Float = ending_balance - initial_balance
                        val newBet = Bet(transaction.transaction_id, game_type, wager_amount, win_loss, balanceChange)
                        try{
                            client.from("bets").insert(newBet)
                        }catch (e: Exception) {
                            Log.e("UserRepository", "Error updating balance", e)
                            false
                        }
                        if (budget_data != null && wager_amount != null) {
                            val new_win_loss = budget_data.win_loss?.plus(balanceChange)
                            updateWinLoss(transaction.customer_id, transaction.date, new_win_loss)
                            val new_wage = budget_data.wage?.plus(wager_amount)
                            updateWage(transaction.customer_id, transaction.date, new_wage)
                        }
                    }
                }
                else if(transaction.event_type == 1){
                    // Store the deposit in the database
                    if (ending_balance != null && initial_balance != null){
                        val newDeposit = Deposit(transaction.transaction_id, ending_balance - initial_balance)
                        client.from("deposits").insert(newDeposit)
                    }

                }
                else if(transaction.event_type == 2){
                    // Store the withdrawal in the database
                }
                else if(transaction.event_type == 3){
                    // Store the register in the database
                    val newRegister = Register(transaction.transaction_id, transaction.date, transaction.customer_id)
                    client.from("registers").insert(newRegister)
                }
                else if(transaction.event_type == 4){
                    // do whatever you wanna do, this was a flag
                }
                true
            } catch (e: Exception) {
                Log.e("UserRepository", "Error inserting transaction", e)
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
    val date: String?,
    val event_type: Int,
    val initial_balance: Float,
    val ending_balance: Float
)

@Serializable
data class NewTransaction(
    val customer_id: Int,
    val date: String?,
    val event_type: Int,
    val initial_balance: Float?,
    val ending_balance: Float?
)

@Serializable
data class Deposit(
    val transaction_id: Int,
    val deposit_amount: Float
)

@Serializable
data class Register(
    val transaction_id: Int,
    val date: String?,
    val customer_id: Int
)

@Serializable
data class Bet(
    val transaction_id: Int,
    val game_type: Int?,
    val wager_amount: Float?,
    val win_loss: Boolean?,
    val win_loss_amount: Float?
)

@Serializable
data class Budget_data(
    val customer_id: Int,
    val date: String?,
    val win_loss: Float?,
    val wage: Float?,
    val balance: Float?
)