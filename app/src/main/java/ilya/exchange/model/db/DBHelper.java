package ilya.exchange.model.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table users(id integer primary key autoincrement, email text unique, pass text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //returning id
    public long addUser(String email, String pass) {
        ContentValues values = new ContentValues();
        values.put("email", email);
        values.put("pass", pass);
        return getWritableDatabase().insert("users", null, values);
    }

    public User getUser(String email) {
        Cursor cursor = getReadableDatabase().rawQuery("select * from users where email = ?", new String[]{email});

        if (cursor.moveToFirst()) return new User(
                cursor.getLong(cursor.getColumnIndex("id")),
                cursor.getString(cursor.getColumnIndex("email")),
                cursor.getString(cursor.getColumnIndex("pass"))
        );
        else return null;
    }
}
