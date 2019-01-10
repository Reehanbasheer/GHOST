package com.reehan2906.ghost;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Random;

public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    //private Button challengeButton;
    //private Button restartButton;
    private boolean userTurn = false;
    private Random random = new Random();
    //private TextView ghostTextView = (TextView) findViewById(R.id.ghostText);
    //private TextView gameStatus = (TextView) findViewById(R.id.gameStatus);
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            dictionary = new SimpleDictionary(getAssets().open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        onStart(null);

        final Button challengeButton = findViewById(R.id.challengeButton);
        final Button restartButton = findViewById(R.id.restartButton);
        final TextView ghostTextView  = findViewById(R.id.ghostText);
        final TextView gameStatus = findViewById(R.id.gameStatus);

        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word=ghostTextView.getText().toString();
                if(word=="")
                {
                    return;
                }
                if(word.length()>=4 && dictionary.isWord(word))
                {
                    gameStatus.setText("You Win!");
                }
                else
                {
                    gameStatus.setText("Computer Wins!");
                }
            }
            //Pendning: challenge player 2's word if they think that no word can be formed with the current fragment. Then, player 2 must provide a valid word starting with the current fragment or lose.
        });

        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStart(null);// NSFW
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = findViewById(R.id.ghostText);
        text.setText("");
        TextView label = findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = findViewById(R.id.gameStatus);
        TextView ghostTextView = findViewById(R.id.ghostText);
        String prefix = ghostTextView.getText().toString();
        //String wordSelected = dictionary.getAnyWordStartingWith(prefix);
        String wordSelected = dictionary.getGoodWordStartingWith(prefix);
        Log.d("Word Selected", "" + wordSelected);
        if ((dictionary.isWord(prefix) & prefix.length() >= 4) || (wordSelected == null && prefix != "")) {
            label.setText("Computer Wins!");

        } else {
            int prefixSize = prefix.length();
            ghostTextView.setText(prefix + wordSelected.charAt(prefixSize));
            userTurn = true;
            label.setText(USER_TURN);
        }
    }



    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        TextView ghostTextView = findViewById(R.id.ghostText);
        TextView gameStatus = findViewById(R.id.gameStatus);
        if(keyCode>=29 && keyCode<=54)
        {
            ghostTextView.setText(ghostTextView.getText().toString()+(char)(keyCode+68));
            final android.os.Handler handler=new android.os.Handler();
            gameStatus.setText(COMPUTER_TURN);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    computerTurn();
                }
            }, 500);
            return true;
        }
        else
            return super.onKeyUp(keyCode, event);
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        TextView ghostTextView = findViewById(R.id.ghostText);
        TextView gameStatus = findViewById(R.id.gameStatus);
        outState.putString("ghost_text",ghostTextView.getText().toString());
        outState.putString("game_status",gameStatus.getText().toString());
        outState.putBoolean("userTurn",userTurn);
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        TextView ghostTextView = findViewById(R.id.ghostText);
        TextView gameStatus = findViewById(R.id.gameStatus);
        userTurn = savedInstanceState.getBoolean("userTurn");
        ghostTextView.setText(savedInstanceState.getString("ghost_text"));
        gameStatus.setText(savedInstanceState.getString("game_status"));
    }
}
