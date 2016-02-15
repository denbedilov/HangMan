package denys.hangman;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String word;
    private ArrayList<String> words;
    private TextView wordField;
    private ProgressDialog dialog;
    private final String url = "http://api.wordnik.com:80/v4/words.json/randomWords?hasDictionaryDef=true&includePartOfSpeech=noun&minCorpusCount=10000&maxCorpusCount=-1&minDictionaryCount=20&maxDictionaryCount=-1&minLength=5&maxLength=10&limit=50&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
    private GameView gameView;
    private final int GAME_OVER = 6;
    private boolean isWon = false;
    private boolean inGame = false;
    private StopWatch watch;
    private Task task;
    private TextView time;
    private Button newGame;
    private String intTime;
    private Spinner lengthSpinner;
    private DAL dal;
    private String length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dal = new DAL(this);

        lengthSpinner = (Spinner) findViewById(R.id.lengthSpinner);


        wordField = (TextView) findViewById(R.id.GuessedWordField);
        gameView = (GameView)findViewById(R.id.gamePanel);
        time = (TextView) findViewById(R.id.currTime);

        newGame = (Button) findViewById(R.id.button27);


        //setting up progress dialog
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Getting word");

        // checking number of words in data base
        if(dal.getWordsCount() < 50)
            getWords();
        else
            fillSpinner();
    }

    private void getWords()
    {
        dialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request= new JsonArrayRequest(
                Request.Method.GET,
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            words = new ArrayList<String>();
                            for(int i=0; i<50; i++)
                                dal.addWord(Html.fromHtml((String) response.getJSONObject(i).getString("word")).toString());
                            fillSpinner();
                            dialog.cancel();

                        } catch (JSONException e) {
                            Log.v("JSONException: ", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        dialog.cancel();
                        Log.v("Volley error: ", error.getMessage());
                    }
                }
        );
        queue.add(request);

    }


    private boolean isOnlyASCII(String word) {
        for(int i=0; i<word.length(); i++)
            if((int) word.charAt(i) <97 || (int) word.charAt(i) > 122)
                return false;
        return true;
    }

    private void gameInit()
    {
        length = lengthSpinner.getSelectedItem().toString();
        fillSpinner();
        inGame = true;
        task=new Task();
        isWon = false;
        word = null;
        gameView.initI();
        getWord();
        fillGuessWord(word.length());
        enableButtons((ViewGroup) findViewById(android.R.id.content));

    }

    private void fillSpinner() {
        ArrayList<String> lengths = dal.getLengths();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, lengths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lengthSpinner.setAdapter(null);
        lengthSpinner.setAdapter(adapter);
    }

    private void getWord() {
        word = dal.getWord(length);
        if(word.equals(""))
            getWords();
    }

    private void enableButtons(ViewGroup v) {
        Button btn;
            for (int i = 0; i < v.getChildCount(); i++) {
                View child = v.getChildAt(i);
                if(child instanceof ViewGroup)
                    enableButtons((ViewGroup)child);
                else if(child instanceof Button) {
                    btn = (Button) child;
                    btn.setEnabled(true);
                }
            }
    }

    private void fillGuessWord(int wordSize)
    {
        if(!isOnlyASCII(word)) {
            getWord();
            return;
        }
        String guessedWord = "";
        for(int i=0; i<wordSize; i++)
            guessedWord += "◻";
        wordField.setText(guessedWord);
        //dialog.cancel();
        watch=new StopWatch();
        task.execute();
    }

    @Override
    public void onClick(View v) {

        Button btn=(Button)findViewById(v.getId());
        if(btn.getId() == newGame.getId()) {
            if(lengthSpinner.getSelectedItem().toString().equals("length"))
            {
                Toast.makeText(this, "Please choose word length", Toast.LENGTH_SHORT).show();
                return;
            }
            gameInit();
        }
        else {
            btn.setEnabled(false);
            check(btn);
            if (isWon) {
                gameOver("You WIN");
            }
        }
    }

    public void check(Button btn)
    {
        Boolean exists=false;
        int i;
        Character btnChar=btn.getText().toString().charAt(0);
        String guessedWord=wordField.getText().toString();
        for (int index = 0; index < word.length(); index++) {
            char c = word.charAt(index);
            if (Character.isLowerCase(c))
                c=Character.toUpperCase(c);
            if(btnChar.equals(c)){
                exists=true;
                char[] guessedWordChars = guessedWord.toCharArray();
                guessedWordChars[index] = c;
                guessedWord = String.valueOf(guessedWordChars);
                // check win case
                isWon = isWon(guessedWord);
            }

        }
        // no letter case
        if(!exists)
        {
            gameView.increaseI();
            i=gameView.getI();
            if(i==GAME_OVER)
                gameOver("Game Over, The correct word was " + word.toUpperCase());
        }
        else
            wordField.setText(guessedWord);
        // TODO: delete this line
        //Toast.makeText(getBaseContext(), "" + word , Toast.LENGTH_SHORT).show();
    }

    private boolean isWon(String guessedWord) {
        if(guessedWord.contains("◻"))
            return false;
        return true;
    }

    private void gameOver(String msg) {
        task.cancel(true);
        inGame = false;
        time.setText(intTime);
        if(msg.equals("You WIN")){
            msg += ", word guessed in " + intTime + " sec";
            if(Double.parseDouble(intTime) < Double.parseDouble(dal.getTime(Integer.valueOf(length))))
            {
                dal.updateTime(length, intTime);
            }
        }
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        gameInit();
    }

    //AsyncTask for timer
    public class Task extends AsyncTask<Void, Double, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            while(inGame){
                try {
                    Thread.sleep(100);
                    publishProgress(watch.elapsedTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }
        @Override
        protected void onProgressUpdate(Double... values) {
            time.setText(values[0].toString());
            intTime=values[0].toString();
        }

        @Override
        protected void onPostExecute(Integer res)
        {

        }
    }
}
