import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DatabaseConfig.DATABASE_NAME, null, DatabaseConfig.DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """CREATE TABLE ${DatabaseConfig.TABLE_NAME} (
            ${DatabaseConfig.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${DatabaseConfig.COLUMN_TEXT} TEXT NOT NULL,
            ${DatabaseConfig.COLUMN_IMAGE_URL} TEXT NOT NULL UNIQUE
        )"""
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseConfig.TABLE_NAME}")
        onCreate(db)
    }

    fun insertTextAndImageUrl(text: String, imageUrl: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(DatabaseConfig.COLUMN_TEXT, text)
            put(DatabaseConfig.COLUMN_IMAGE_URL, imageUrl)
        }
        val newRowId = db.insert(DatabaseConfig.TABLE_NAME, null, contentValues)
        db.close()
        return newRowId
    }

    fun getAllRecords(): List<Map<String, Any>> {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${DatabaseConfig.TABLE_NAME}", null)
        val result = mutableListOf<Map<String, Any>>()

        if (cursor.moveToFirst()) {
            do {
                val record = HashMap<String, Any>()
                record[DatabaseConfig.COLUMN_ID] = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConfig.COLUMN_ID))
                record[DatabaseConfig.COLUMN_TEXT] = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConfig.COLUMN_TEXT))
                record[DatabaseConfig.COLUMN_IMAGE_URL] = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConfig.COLUMN_IMAGE_URL))
                result.add(record)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return result
    }

}
