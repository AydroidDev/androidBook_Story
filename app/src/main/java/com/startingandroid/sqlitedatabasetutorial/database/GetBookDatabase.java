package com.startingandroid.sqlitedatabasetutorial.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.startingandroid.sqlitedatabasetutorial.Model.Book;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class GetBookDatabase extends SQLiteOpenHelper {

    String DB_PATH = "data/data/com.startingandroid.sqlitedatabasetutorial/databases/";
    static String DB_NAME = "Books";
    SQLiteDatabase sqLiteDatabase;
    Context context;
    private DatabaseContract databaseContract = new DatabaseContract();
    private String TAG = "GetBookDatabase";

    public GetBookDatabase(Context context) {
        super(context, DB_NAME, null, 1);
        this.context = context;
        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        try {
            createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDatabase();
        Log.d(TAG, "db is " + dbExist);
        if (!dbExist) {

            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public boolean checkDatabase() {
        File databasePath = context.getDatabasePath(DB_NAME);
        return databasePath.exists();
    }

    public void copyDatabase() throws IOException {

        InputStream inputStream = context.getAssets().open("Book.sqlite");

        String path = DB_PATH + DB_NAME;

        OutputStream outputStream = new FileOutputStream(path);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    @Override
    public synchronized void close() {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();

    }

    String book = " CREATE TABLE IF NOT EXISTS " + databaseContract.TABLE_USER + " ("
            + databaseContract.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + databaseContract.KEY_NAME + " TEXT ,"
            + databaseContract.KEY_CONTENT + " TEXT ," + databaseContract.KEY_ICON +
            " TEXT ," + databaseContract.KEY_BOOKMARK + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(book);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
    public int updateBookmark(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(databaseContract.KEY_ID, book.getId());
        values.put(databaseContract.KEY_NAME, book.getName());
        values.put(databaseContract.KEY_CONTENT, book.getContent());
        values.put(databaseContract.KEY_ICON, book.getIcon());
        values.put(databaseContract.KEY_BOOKMARK, 1);

        // updating record
        return db.update(databaseContract.TABLE_USER, values,
                databaseContract.KEY_ID + " = ?",
                // update query to make changes to the existing record
                new String[]{String.valueOf(book.getId())});

    }
    /*addUser() will add a new Book to database*/
    public void addBookmark(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(databaseContract.KEY_NAME, book.getName());
        values.put(databaseContract.KEY_CONTENT, book.getContent());
        values.put(databaseContract.KEY_ICON, book.getIcon());
        values.put(databaseContract.KEY_BOOKMARK, 1);

        db.insert(databaseContract.TABLE_USER, null, values);//Insert query to store the record in the database
        db.close();
    }

    /*getUser() will return he user's object if id matches*/
    public Book getUser(int user_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Book book = null;
        Cursor cursor = db.query(databaseContract.TABLE_USER, new String[]{databaseContract.KEY_ID,
                        databaseContract.KEY_NAME, databaseContract.KEY_CONTENT, databaseContract.KEY_BOOKMARK}, databaseContract.KEY_ID + "=?",
                new String[]{String.valueOf(user_id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

            cursor.close();
        }
        return book;
    }

    /*getAllUsers() will return the list of all users*/
    public ArrayList<Book> getAllUsers() {
        ArrayList<Book> usersList = new ArrayList<Book>();
        String selectQuery = "SELECT  * FROM " + databaseContract.TABLE_USER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                usersList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return usersList;
    }

    public ArrayList<Book> getAllBookmarkedUsers() {
        ArrayList<Book> bookmarkList = new ArrayList<Book>();
        String selectQuery = "SELECT  * FROM " + databaseContract.TABLE_USER+" WHERE bookmark = 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Book book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                bookmarkList.add(book);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bookmarkList;
    }

    /*getUsersCount() will give the total number of records in the table*/
    public int getUsersCount() {
        String countQuery = "SELECT * FROM " + databaseContract.TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /*updateUser() will be used to update the existing book record*/



    /*deleteContact() to delete the record from the table*/
    public void deleteContact(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(databaseContract.TABLE_USER, databaseContract.KEY_ID + " = ?",
                new String[]{String.valueOf(book.getId())});
        db.close();

    }

    public Book getNextBook(int id) {
        String sql = "SELECT * FROM " + databaseContract.TABLE_USER + " WHERE " + databaseContract.KEY_ID + " = ("
                + "SELECT MIN(" + databaseContract.KEY_ID + ") from " + databaseContract.TABLE_USER + " WHERE "
                + databaseContract.KEY_ID + "> " + id + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        Book book = null;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return book;
    }

    public Book getPreviousBook(int id) {
        String sql = "SELECT * FROM " + databaseContract.TABLE_USER + " WHERE " + databaseContract.KEY_ID + " = ("
                + "SELECT MAX(" + databaseContract.KEY_ID + ") from " + databaseContract.TABLE_USER + " WHERE "
                + databaseContract.KEY_ID + " < " + id + ")";
        SQLiteDatabase db = this.getWritableDatabase();
        Book book = null;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                book = new Book(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));

            } while (cursor.moveToNext());
        }
        cursor.close();
        return book;
    }

}
