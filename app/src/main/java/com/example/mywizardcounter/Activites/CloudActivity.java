package com.example.mywizardcounter.Activites;

import static com.example.mywizardcounter.Activites.GameActivity.PLAYER_NAMES_TOKEN;
import static com.example.mywizardcounter.Activites.GameActivity.POINT_TEXT_VIEWS_TOKEN;
import static com.example.mywizardcounter.Activites.GameActivity.ROUND_TOKEN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywizardcounter.R;

import java.util.ArrayList;

public class CloudActivity extends AppCompatActivity {

    public static String NEW_GUESS_NUMBER_TOKEN = "Neue Vorhersage";
    public static String PLAYER_INDEX_TOKEN = "SPIELERINDEX";

    LinearLayout layout;
    ArrayList<String> names;
    ArrayList<Integer> guesses;
    ArrayList<Button> buttons;
    int round;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        layout = findViewById(R.id.masterLayout);
        names = getIntent().getStringArrayListExtra(PLAYER_NAMES_TOKEN);
        guesses = getIntent().getIntegerArrayListExtra(POINT_TEXT_VIEWS_TOKEN);
        round = getIntent().getIntExtra(ROUND_TOKEN, -1);
        if(guesses == null || names == null) {
            Toast.makeText(this, "guesses or names are NULL.", Toast.LENGTH_SHORT).show();
            return;
        }
        buttons = new ArrayList<>();
        makeButtons();
    }

    private void makeButtons() {
        for(String name: names){
            Button button = new Button(this);
            button.setText(name);
            final Context context = this;
            button.setOnClickListener(v -> {
                int currentPlayer = buttons.indexOf((Button) v);
                final int[] guesses = {this.guesses.get(currentPlayer)};
                AlertDialog.Builder ad = new AlertDialog.Builder(context);
                if(guesses[0] == 0){
                    Toast.makeText(context, "Die Vorhersage von " + name + " wurde von 0 auf 1 gesetzt.", Toast.LENGTH_SHORT).show();
                    endActivity(1, currentPlayer);
                }
                if(guesses[0] == round){
                    Toast.makeText(context, "Die Vorhersage von " + name + " wurde von " + round + " auf " + (round - 1) + " gesetzt.", Toast.LENGTH_SHORT).show();
                    endActivity(round - 1, currentPlayer);
                }
                else {
                    ad.setMessage("Möchte " + name + " die Vorhersage erhöhen oder verkleinern?")
                            .setPositiveButton("Erhöhen", (dialog, which) -> endActivity(guesses[0] + 1, currentPlayer))
                            .setNeutralButton("Verkleinern", (dialog, which) -> endActivity(guesses[0] - 1, currentPlayer))
                            .show();
                }
            });
            layout.addView(button);
            buttons.add(button);
        }
    }

    private void endActivity(int newGuess, int playerIndex){
        Intent intent = getIntent();
        intent.putExtra(NEW_GUESS_NUMBER_TOKEN, newGuess);
        intent.putExtra(PLAYER_INDEX_TOKEN, playerIndex);
        setResult(RESULT_OK, intent);
        finish();
    }
}