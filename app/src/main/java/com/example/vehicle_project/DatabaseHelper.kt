package com.example.vehicle_project

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "vehicle_database"
        private const val DATABASE_VERSION = 4

        const val TABLE_NAME = "user_data"
        const val COLUMN_ID = "id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_CONFIRM_PASSWORD = "confirm_password"

        // Vehicle Table
        const val TABLE_VEHICLE = "vehicle_data"
        const val COLUMN_VEHICLE_ID = "vehicle_id"
        const val COLUMN_VEHICLE_USER_EMAIL = "user_email"
        const val COLUMN_VEHICLE_NAME = "vehicle_name"
        const val COLUMN_VEHICLE_NUMBER = "vehicle_number"
        const val COLUMN_VEHICLE_TYPE = "vehicle_type"
        const val COLUMN_VEHICLE_BRAND = "brand"
        const val COLUMN_VEHICLE_FUEL = "fuel_type"

        // Insurance Table
        const val TABLE_INSURANCE = "insurance_data"
        const val COLUMN_INSURANCE_ID = "insurance_id"
        const val COLUMN_INSURANCE_USER_EMAIL = "user_email"
        const val COLUMN_INSURANCE_VEHICLE_NUMBER = "vehicle_number"
        const val COLUMN_INSURANCE_COMPANY = "insurance_company"
        const val COLUMN_INSURANCE_POLICY_NUMBER = "policy_number"
        const val COLUMN_INSURANCE_EXPIRY_DATE = "expiry_date"
        const val COLUMN_INSURANCE_PREMIUM_AMOUNT = "premium_amount"

        // Service Table
        const val TABLE_SERVICE = "service_data"
        const val COLUMN_SERVICE_ID = "service_id"
        const val COLUMN_SERVICE_USER_EMAIL = "user_email"
        const val COLUMN_SERVICE_VEHICLE_NUMBER = "vehicle_number"
        const val COLUMN_SERVICE_TYPE = "service_type"
        const val COLUMN_SERVICE_DATE = "service_date"
        const val COLUMN_SERVICE_COST = "service_cost"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE IF NOT EXISTS $TABLE_NAME ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_USERNAME TEXT,"
                + "$COLUMN_EMAIL TEXT,"
                + "$COLUMN_PASSWORD TEXT,"
                + "$COLUMN_CONFIRM_PASSWORD TEXT)")
        db.execSQL(createTable)

        val createVehicleTable = ("CREATE TABLE IF NOT EXISTS $TABLE_VEHICLE ("
                + "$COLUMN_VEHICLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_VEHICLE_USER_EMAIL TEXT,"
                + "$COLUMN_VEHICLE_NAME TEXT,"
                + "$COLUMN_VEHICLE_NUMBER TEXT UNIQUE,"
                + "$COLUMN_VEHICLE_TYPE TEXT,"
                + "$COLUMN_VEHICLE_BRAND TEXT,"
                + "$COLUMN_VEHICLE_FUEL TEXT)")
        db.execSQL(createVehicleTable)

        val createInsuranceTable = ("CREATE TABLE IF NOT EXISTS $TABLE_INSURANCE ("
                + "$COLUMN_INSURANCE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_INSURANCE_USER_EMAIL TEXT,"
                + "$COLUMN_INSURANCE_VEHICLE_NUMBER TEXT,"
                + "$COLUMN_INSURANCE_COMPANY TEXT,"
                + "$COLUMN_INSURANCE_POLICY_NUMBER TEXT,"
                + "$COLUMN_INSURANCE_EXPIRY_DATE TEXT,"
                + "$COLUMN_INSURANCE_PREMIUM_AMOUNT TEXT,"
                + "FOREIGN KEY($COLUMN_INSURANCE_VEHICLE_NUMBER) REFERENCES $TABLE_VEHICLE($COLUMN_VEHICLE_NUMBER) ON DELETE CASCADE)")
        db.execSQL(createInsuranceTable)

        val createServiceTable = ("CREATE TABLE IF NOT EXISTS $TABLE_SERVICE ("
                + "$COLUMN_SERVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_SERVICE_USER_EMAIL TEXT,"
                + "$COLUMN_SERVICE_VEHICLE_NUMBER TEXT,"
                + "$COLUMN_SERVICE_TYPE TEXT,"
                + "$COLUMN_SERVICE_DATE TEXT,"
                + "$COLUMN_SERVICE_COST TEXT,"
                + "FOREIGN KEY($COLUMN_SERVICE_VEHICLE_NUMBER) REFERENCES $TABLE_VEHICLE($COLUMN_VEHICLE_NUMBER) ON DELETE CASCADE)")
        db.execSQL(createServiceTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        val createVehicleTable = ("CREATE TABLE IF NOT EXISTS $TABLE_VEHICLE ("
                + "$COLUMN_VEHICLE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_VEHICLE_USER_EMAIL TEXT,"
                + "$COLUMN_VEHICLE_NAME TEXT,"
                + "$COLUMN_VEHICLE_NUMBER TEXT UNIQUE,"
                + "$COLUMN_VEHICLE_TYPE TEXT,"
                + "$COLUMN_VEHICLE_BRAND TEXT,"
                + "$COLUMN_VEHICLE_FUEL TEXT)")
        db.execSQL(createVehicleTable)

        val createInsuranceTable = ("CREATE TABLE IF NOT EXISTS $TABLE_INSURANCE ("
                + "$COLUMN_INSURANCE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_INSURANCE_USER_EMAIL TEXT,"
                + "$COLUMN_INSURANCE_VEHICLE_NUMBER TEXT,"
                + "$COLUMN_INSURANCE_COMPANY TEXT,"
                + "$COLUMN_INSURANCE_POLICY_NUMBER TEXT,"
                + "$COLUMN_INSURANCE_EXPIRY_DATE TEXT,"
                + "$COLUMN_INSURANCE_PREMIUM_AMOUNT TEXT,"
                + "FOREIGN KEY($COLUMN_INSURANCE_VEHICLE_NUMBER) REFERENCES $TABLE_VEHICLE($COLUMN_VEHICLE_NUMBER) ON DELETE CASCADE)")
        db.execSQL(createInsuranceTable)

        val createServiceTable = ("CREATE TABLE IF NOT EXISTS $TABLE_SERVICE ("
                + "$COLUMN_SERVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_SERVICE_USER_EMAIL TEXT,"
                + "$COLUMN_SERVICE_VEHICLE_NUMBER TEXT,"
                + "$COLUMN_SERVICE_TYPE TEXT,"
                + "$COLUMN_SERVICE_DATE TEXT,"
                + "$COLUMN_SERVICE_COST TEXT,"
                + "FOREIGN KEY($COLUMN_SERVICE_VEHICLE_NUMBER) REFERENCES $TABLE_VEHICLE($COLUMN_VEHICLE_NUMBER) ON DELETE CASCADE)")
        db.execSQL(createServiceTable)
    }

    fun insertUserData(username: String, email: String, password: String, confirmPassword: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_USERNAME, username.trim())
        contentValues.put(COLUMN_EMAIL, email.trim().lowercase())
        contentValues.put(COLUMN_PASSWORD, password)
        contentValues.put(COLUMN_CONFIRM_PASSWORD, confirmPassword)

        val result = db.insert(TABLE_NAME, null, contentValues)
        return result != -1L
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val columns = arrayOf(COLUMN_ID)
        val selection = "LOWER($COLUMN_EMAIL) = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(email.trim().lowercase(), password)

        val cursor = db.query(
            TABLE_NAME,
            columns,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val count = cursor.count
        cursor.close()
        return count > 0
    }

    fun getUsernameByEmail(email: String): String {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NAME, arrayOf(COLUMN_USERNAME), "LOWER($COLUMN_EMAIL) = ?", arrayOf(email.trim().lowercase()), null, null, null)
        var username = ""
        if (cursor.moveToFirst()) {
            username = cursor.getString(0)
        }
        cursor.close()
        return username
    }

    fun getUserDetails(email: String): Map<String, String>? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PASSWORD, COLUMN_CONFIRM_PASSWORD),
            "LOWER($COLUMN_EMAIL) = ?",
            arrayOf(email.trim().lowercase()),
            null,
            null,
            null
        )
        var userDetails: Map<String, String>? = null
        if (cursor.moveToFirst()) {
            userDetails = mapOf(
                "username" to cursor.getString(0),
                "email" to cursor.getString(1),
                "password" to cursor.getString(2),
                "confirm_password" to cursor.getString(3)
            )
        }
        cursor.close()
        return userDetails
    }

    fun updateUserData(oldEmail: String, name: String, newEmail: String, password: String, confirmPassword: String): Boolean {
        val db = this.writableDatabase
        val normalizedOld = oldEmail.trim().lowercase()
        val normalizedNew = newEmail.trim().lowercase()

        if (normalizedNew != normalizedOld) {
            val checkCursor = db.query(TABLE_NAME, arrayOf(COLUMN_ID), "LOWER($COLUMN_EMAIL) = ?", arrayOf(normalizedNew), null, null, null)
            val exists = checkCursor.count > 0
            checkCursor.close()
            if (exists) {
                return false
            }
        }

        val contentValues = ContentValues()
        contentValues.put(COLUMN_USERNAME, name.trim())
        contentValues.put(COLUMN_EMAIL, normalizedNew)
        contentValues.put(COLUMN_PASSWORD, password)
        contentValues.put(COLUMN_CONFIRM_PASSWORD, confirmPassword)

        val result = db.update(TABLE_NAME, contentValues, "LOWER($COLUMN_EMAIL) = ?", arrayOf(normalizedOld))
        if (result > 0) {
            if (normalizedNew != normalizedOld) {
                val vehicleValues = ContentValues()
                vehicleValues.put(COLUMN_VEHICLE_USER_EMAIL, normalizedNew)
                db.update(TABLE_VEHICLE, vehicleValues, "LOWER($COLUMN_VEHICLE_USER_EMAIL) = ?", arrayOf(normalizedOld))

                val insuranceValues = ContentValues()
                insuranceValues.put(COLUMN_INSURANCE_USER_EMAIL, normalizedNew)
                db.update(TABLE_INSURANCE, insuranceValues, "LOWER($COLUMN_INSURANCE_USER_EMAIL) = ?", arrayOf(normalizedOld))

                val serviceValues = ContentValues()
                serviceValues.put(COLUMN_SERVICE_USER_EMAIL, normalizedNew)
                db.update(TABLE_SERVICE, serviceValues, "LOWER($COLUMN_SERVICE_USER_EMAIL) = ?", arrayOf(normalizedOld))
            }
            return true
        }
        return false
    }

    fun deleteUserData(email: String): Boolean {
        val db = this.writableDatabase
        val normalizedEmail = email.trim().lowercase()
        db.delete(TABLE_SERVICE, "LOWER($COLUMN_SERVICE_USER_EMAIL) = ?", arrayOf(normalizedEmail))
        db.delete(TABLE_INSURANCE, "LOWER($COLUMN_INSURANCE_USER_EMAIL) = ?", arrayOf(normalizedEmail))
        db.delete(TABLE_VEHICLE, "LOWER($COLUMN_VEHICLE_USER_EMAIL) = ?", arrayOf(normalizedEmail))
        val result = db.delete(TABLE_NAME, "LOWER($COLUMN_EMAIL) = ?", arrayOf(normalizedEmail))
        return result > 0
    }

    fun insertVehicleData(userEmail: String, name: String, number: String, type: String, brand: String, fuel: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_VEHICLE_USER_EMAIL, userEmail.trim().lowercase())
        contentValues.put(COLUMN_VEHICLE_NAME, name.trim())
        contentValues.put(COLUMN_VEHICLE_NUMBER, number.trim())
        contentValues.put(COLUMN_VEHICLE_TYPE, type.trim())
        contentValues.put(COLUMN_VEHICLE_BRAND, brand.trim())
        contentValues.put(COLUMN_VEHICLE_FUEL, fuel.trim())

        val result = db.insert(TABLE_VEHICLE, null, contentValues)
        return result != -1L
    }

    fun getVehiclesByUser(userEmail: String): ArrayList<Vehicle> {
        val vehicleList = ArrayList<Vehicle>()
        val db = this.readableDatabase
        val cursor = if (userEmail.isNotEmpty()) {
            db.query(TABLE_VEHICLE, null, "LOWER($COLUMN_VEHICLE_USER_EMAIL) = ?", arrayOf(userEmail.trim().lowercase()), null, null, null)
        } else {
            db.query(TABLE_VEHICLE, null, null, null, null, null, null)
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_NAME))
                val number = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_NUMBER))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_TYPE))
                val brand = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_BRAND))
                val fuel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VEHICLE_FUEL))
                
                vehicleList.add(Vehicle(id, name, number, type, brand, fuel))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return vehicleList
    }

    fun updateVehicleData(vehicleId: Int, name: String, number: String, type: String, brand: String, fuel: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_VEHICLE_NAME, name)
        contentValues.put(COLUMN_VEHICLE_NUMBER, number)
        contentValues.put(COLUMN_VEHICLE_TYPE, type)
        contentValues.put(COLUMN_VEHICLE_BRAND, brand)
        contentValues.put(COLUMN_VEHICLE_FUEL, fuel)

        val result = db.update(TABLE_VEHICLE, contentValues, "$COLUMN_VEHICLE_ID = ?", arrayOf(vehicleId.toString()))
        return result > 0
    }

    fun deleteVehicleData(vehicleId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_VEHICLE, "$COLUMN_VEHICLE_ID = ?", arrayOf(vehicleId.toString()))
        return result > 0
    }

    // --- Insurance Operations ---

    fun doesVehicleExist(vehicleNumber: String, userEmail: String): Boolean {
        val db = this.readableDatabase
        val cursor = if (userEmail.isNotEmpty()) {
            db.query(
                TABLE_VEHICLE,
                arrayOf(COLUMN_VEHICLE_ID),
                "LOWER($COLUMN_VEHICLE_NUMBER) = ? AND LOWER($COLUMN_VEHICLE_USER_EMAIL) = ?",
                arrayOf(vehicleNumber.trim().lowercase(), userEmail.trim().lowercase()),
                null, null, null
            )
        } else {
            db.query(
                TABLE_VEHICLE,
                arrayOf(COLUMN_VEHICLE_ID),
                "LOWER($COLUMN_VEHICLE_NUMBER) = ?",
                arrayOf(vehicleNumber.trim().lowercase()),
                null, null, null
            )
        }
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun insertInsuranceData(
        userEmail: String,
        vehicleNumber: String,
        company: String,
        policyNumber: String,
        expiryDate: String,
        premiumAmount: String
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_INSURANCE_USER_EMAIL, userEmail.trim().lowercase())
        contentValues.put(COLUMN_INSURANCE_VEHICLE_NUMBER, vehicleNumber.trim().uppercase())
        contentValues.put(COLUMN_INSURANCE_COMPANY, company.trim())
        contentValues.put(COLUMN_INSURANCE_POLICY_NUMBER, policyNumber.trim())
        contentValues.put(COLUMN_INSURANCE_EXPIRY_DATE, expiryDate.trim())
        contentValues.put(COLUMN_INSURANCE_PREMIUM_AMOUNT, premiumAmount.trim())

        val result = db.insert(TABLE_INSURANCE, null, contentValues)
        return result != -1L
    }

    fun getInsurancesByUser(userEmail: String): ArrayList<Insurance> {
        val insuranceList = ArrayList<Insurance>()
        val db = this.readableDatabase
        val cursor = if (userEmail.isNotEmpty()) {
            db.query(
                TABLE_INSURANCE,
                null,
                "LOWER($COLUMN_INSURANCE_USER_EMAIL) = ?",
                arrayOf(userEmail.trim().lowercase()),
                null, null, null
            )
        } else {
            db.query(TABLE_INSURANCE, null, null, null, null, null, null)
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INSURANCE_ID))
                val vehicleNum = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSURANCE_VEHICLE_NUMBER))
                val company = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSURANCE_COMPANY))
                val policyNum = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSURANCE_POLICY_NUMBER))
                val expiry = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSURANCE_EXPIRY_DATE))
                val premium = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INSURANCE_PREMIUM_AMOUNT))

                insuranceList.add(Insurance(id, vehicleNum, company, policyNum, expiry, premium))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return insuranceList
    }

    fun updateInsuranceData(
        insuranceId: Int,
        vehicleNumber: String,
        company: String,
        policyNumber: String,
        expiryDate: String,
        premiumAmount: String
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_INSURANCE_VEHICLE_NUMBER, vehicleNumber.trim().uppercase())
        contentValues.put(COLUMN_INSURANCE_COMPANY, company.trim())
        contentValues.put(COLUMN_INSURANCE_POLICY_NUMBER, policyNumber.trim())
        contentValues.put(COLUMN_INSURANCE_EXPIRY_DATE, expiryDate.trim())
        contentValues.put(COLUMN_INSURANCE_PREMIUM_AMOUNT, premiumAmount.trim())

        val result = db.update(TABLE_INSURANCE, contentValues, "$COLUMN_INSURANCE_ID = ?", arrayOf(insuranceId.toString()))
        return result > 0
    }

    fun deleteInsuranceData(insuranceId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_INSURANCE, "$COLUMN_INSURANCE_ID = ?", arrayOf(insuranceId.toString()))
        return result > 0
    }

    // --- Service Operations ---

    fun insertServiceData(
        userEmail: String,
        vehicleNumber: String,
        serviceType: String,
        serviceDate: String,
        serviceCost: String
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_SERVICE_USER_EMAIL, userEmail.trim().lowercase())
        contentValues.put(COLUMN_SERVICE_VEHICLE_NUMBER, vehicleNumber.trim().uppercase())
        contentValues.put(COLUMN_SERVICE_TYPE, serviceType.trim())
        contentValues.put(COLUMN_SERVICE_DATE, serviceDate.trim())
        contentValues.put(COLUMN_SERVICE_COST, serviceCost.trim())

        val result = db.insert(TABLE_SERVICE, null, contentValues)
        return result != -1L
    }

    fun getServicesByUser(userEmail: String): ArrayList<ServiceRecord> {
        val serviceList = ArrayList<ServiceRecord>()
        val db = this.readableDatabase
        val cursor = if (userEmail.isNotEmpty()) {
            db.query(
                TABLE_SERVICE,
                null,
                "LOWER($COLUMN_SERVICE_USER_EMAIL) = ?",
                arrayOf(userEmail.trim().lowercase()),
                null, null, null
            )
        } else {
            db.query(TABLE_SERVICE, null, null, null, null, null, null)
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_ID))
                val vehicleNum = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_VEHICLE_NUMBER))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_TYPE))
                val date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_DATE))
                val cost = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SERVICE_COST))

                serviceList.add(ServiceRecord(id, vehicleNum, type, date, cost))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return serviceList
    }

    fun updateServiceData(
        serviceId: Int,
        vehicleNumber: String,
        serviceType: String,
        serviceDate: String,
        serviceCost: String
    ): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_SERVICE_VEHICLE_NUMBER, vehicleNumber.trim().uppercase())
        contentValues.put(COLUMN_SERVICE_TYPE, serviceType.trim())
        contentValues.put(COLUMN_SERVICE_DATE, serviceDate.trim())
        contentValues.put(COLUMN_SERVICE_COST, serviceCost.trim())

        val result = db.update(TABLE_SERVICE, contentValues, "$COLUMN_SERVICE_ID = ?", arrayOf(serviceId.toString()))
        return result > 0
    }

    fun deleteServiceData(serviceId: Int): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_SERVICE, "$COLUMN_SERVICE_ID = ?", arrayOf(serviceId.toString()))
        return result > 0
    }
}
