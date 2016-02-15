package denys.hangman;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Rita on 12/7/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "hangman.db";
    public final String  MAX_TIME = "99999.999";

    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + GameContract.WordsContract.TABLE_NAME + " ( " +
                        GameContract.WordsContract.WORD + " TEXT, " +
                        GameContract.WordsContract.LENGTH + " TEXT " +
                        ");"
        );

        db.execSQL(
                "CREATE TABLE " + GameContract.ScoresContract.TABLE_NAME + " ( " +
                        GameContract.ScoresContract.LENGTH + " TEXT, " +
                        GameContract.ScoresContract.SCORE + " TEXT " +
                        ");"
        );

        for(int j = 5; j <=10; j++)
        {
            db.execSQL(
                    "INSERT INTO " + GameContract.ScoresContract.TABLE_NAME + " VALUES (" +
                            j + "," + MAX_TIME +");"
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST " + GameContract.ScoresContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXIST " + GameContract.WordsContract.TABLE_NAME);
        onCreate(db);
    }

}
