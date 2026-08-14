package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ExpenseDao
import com.example.data.dao.IncomeDao
import com.example.data.model.ExpenseEntry
import com.example.data.model.IncomeEntry

@Database(
    entities = [IncomeEntry::class, ExpenseEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AccountingDatabase : RoomDatabase() {
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AccountingDatabase? = null

        fun getDatabase(context: Context): AccountingDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AccountingDatabase::class.java,
                    "al_okabi_accounting.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
