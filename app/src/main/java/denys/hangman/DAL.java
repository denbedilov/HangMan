package denys.hangman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Rita on 12/8/2015.
 */
public class DAL {
    private  DBHelper dbHelper;

    public DAL(Context context){
        dbHelper = new DBHelper(context);
    }

    public ArrayList<String> getLengths()
    {
        ArrayList<String> lengths = new ArrayList<>();


        String length;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] args = {};
        Cursor cursor = db.rawQuery("SELECT DISTINCT LENGTH FROM " + GameContract.WordsContract.TABLE_NAME, args);
        int lengthIndex = cursor.getColumnIndex(GameContract.WordsContract.LENGTH);
        while (cursor.moveToNext()) {
            length = cursor.getString(lengthIndex);
            lengths.add(length);
        }
        Collections.sort(lengths);
        if(lengths.get(0).equals("10")) {
            lengths.remove(0);
            lengths.add("10");
        }
        // TODO: change string!!!!!!!!!!!!
        lengths.add(0,"length");
        return lengths;
    }

    public void addWord(String word)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GameContract.WordsContract.WORD,word);
        values.put(GameContract.WordsContract.LENGTH,word.length());
        db.insert(GameContract.WordsContract.TABLE_NAME,null,values);

        db.close();
    }

    public String getTime(int length)
    {
        // get db
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // get cursor with relevant data
        String[] args = {length + ""};
        Cursor cursor = db.rawQuery("SELECT SCORE FROM " + GameContract.ScoresContract.TABLE_NAME +
                " WHERE " + GameContract.ScoresContract.LENGTH + "=?", args);
      //  int lengthIndex = cursor.getColumnIndex(GameContract.ScoresContract.SCORE);
        if(cursor == null)
            return "";
        cursor.moveToFirst();
        String w = cursor.getString(0);
        return w;
       // return cursor.getString(lengthIndex);
    }

    public void updateTime(String length, String time)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(GameContract.ScoresContract.SCORE,time);

        String where=GameContract.ScoresContract.LENGTH + "=?";
        String[] whereArgs = {length + ""};

        db.update(GameContract.ScoresContract.TABLE_NAME,values,where,whereArgs);
        db.close();
    }

    public int getWordsCount()
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] args = {};
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + GameContract.WordsContract.TABLE_NAME, args);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public String getWord(String length)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] args = {length};
        Cursor cursor = db.rawQuery("SELECT " + GameContract.WordsContract.WORD + " FROM " + GameContract.WordsContract.TABLE_NAME + " WHERE " + GameContract.WordsContract.LENGTH + "=?", args);
        if(cursor == null)
            return "";
        cursor.moveToFirst();
        String w = cursor.getString(0);
        deleteWord(w);
        return w;
    }

    private void deleteWord(String word) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = {word};
        db.delete(GameContract.WordsContract.TABLE_NAME, GameContract.WordsContract.WORD+"=?", args);
        db.close();
    }
}
