package denys.hangman;

import android.provider.BaseColumns;

/**
 * Created by Rita on 12/7/2015.
 */
public class GameContract {
    public GameContract(){}

    public static abstract class WordsContract implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String WORD = "word";
        public static final String LENGTH = "length";
    }

    public static abstract class ScoresContract implements BaseColumns {
        public static final String TABLE_NAME = "scores";
        public static final String SCORE = "score";
        public static final String LENGTH = "length";
    }
}
