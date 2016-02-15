package denys.hangman;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String word;
    private TextView wordField;
    private ProgressDialog dialog;
    private final String url = "http://api.wordnik.com/v4/words.json/randomWords?hasDictionaryDef=true&minCorpusCount=0&minLength=5&maxLength=10&limit=1&api_key=a2a73e7b926c924fad7001ca3111acd55af2ffabf50eb4ae5";
    private GameView gameView;
    private final int GAME_OVER = 6;
    private boolean isWon = false;

    private Button newGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wordField = (TextView) findViewById(R.id.GuessedWordField);
        gameView = (GameView)findViewById(R.id.gamePanel);


        newGame = (Button) findViewById(R.id.button27);


        //setting up progress dialog
        dialog=new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Getting word");
        gameInit();
    }

    private void getWord()
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
                            word = (Html.fromHtml((String) response.getJSONObject(0).getString("word")).toString());
                            fillGuessWord(word.length());
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
        isWon = false;
        word = null;
        gameView.initI();
        getWord();
        enableButtons((ViewGroup) findViewById(android.R.id.content));
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
        dialog.cancel();
    }

    @Override
    public void onClick(View v) {

        Button btn=(Button)findViewById(v.getId());
        if(btn.getId() == newGame.getId())
            gameInit();
        else {
            btn.setEnabled(false);
            check(btn);
            if (isWon) {
                Toast.makeText(this, "You WIN", Toast.LENGTH_SHORT).show();
                gameInit();
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
                gameOver();
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

    private void gameOver() {
        Toast.makeText(this,"Game Over, The correct word was " + word, Toast.LENGTH_SHORT).show();
        gameInit();
    }
}
