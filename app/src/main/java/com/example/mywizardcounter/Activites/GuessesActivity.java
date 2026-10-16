package com.example.mywizardcounter.Activites;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.mywizardcounter.Activites.CloudActivity.NEW_GUESS_NUMBER_TOKEN;
import static com.example.mywizardcounter.Activites.CloudActivity.PLAYER_INDEX_TOKEN;
import static com.example.mywizardcounter.Activites.GameActivity.GUESSES_TOKEN;
import static com.example.mywizardcounter.Activites.GameActivity.PLAYER_NAMES_TOKEN;
import static com.example.mywizardcounter.Activites.GameActivity.POINT_TEXT_VIEWS_TOKEN;
import static com.example.mywizardcounter.Activites.GameActivity.REASON_ADD;
import static com.example.mywizardcounter.Activites.GameActivity.REASON_CONFIRM;
import static com.example.mywizardcounter.Activites.GameActivity.REASON_CONFIRM_FOR_EDIT;
import static com.example.mywizardcounter.Activites.GameActivity.REASON_EDIT;
import static com.example.mywizardcounter.Activites.GameActivity.REASON_FOR_CALL;
import static com.example.mywizardcounter.Activites.GameActivity.REQUEST_CODE_CLOUD_ACTION;
import static com.example.mywizardcounter.Activites.GameActivity.ROUND_TOKEN;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywizardcounter.AddGuessLayout;
import com.example.mywizardcounter.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GuessesActivity extends AppCompatActivity {

    public static final String CONFIRMATION_TOKEN = "confirmation_token";
    public static final int EDIT_TOKEN_TWO = 2;

    LinearLayout container;
    ArrayList<AddGuessLayout> textViews;
    ArrayList<String> names;
    ArrayList<Integer> guessesList;
    Button saveButton;
    Button abbortionButton;
    FloatingActionButton bombButton;
    boolean isBombActivated = false;
    FloatingActionButton cloudButton;
    boolean isCloudActivated = false;
    TextView totalGuessesTextView;
    int round;
    int activePlayer;
    String reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guesses);
        textViews            = new ArrayList<>();
        container            = findViewById(R.id.containerGuessesActivity);
        totalGuessesTextView = findViewById(R.id.totalGuessTextView);
        saveButton           = findViewById(R.id.saveButton);
        abbortionButton      = findViewById(R.id.abbortionButton);
        bombButton           = findViewById(R.id.bombButton);
        cloudButton          = findViewById(R.id.cloudButton);

        bombButton .setVisibility(View.INVISIBLE);
        cloudButton.setVisibility(View.INVISIBLE);

        round        = getIntent().getIntExtra(ROUND_TOKEN,0);
        names        = getIntent().getStringArrayListExtra(PLAYER_NAMES_TOKEN);
        reason       = getIntent().getStringExtra(REASON_FOR_CALL);
        guessesList  = getIntent().getIntegerArrayListExtra(GUESSES_TOKEN);
        activePlayer = getIntent().getIntExtra(GameActivity.ACTIVE_PLAYER_TOKEN,-1);

        setHeader();

        for(int i = 0; i < names.size(); i++){
            int alreadyGuessed = 0;
            if(reason.equals(REASON_CONFIRM) || reason.equals(REASON_EDIT) || reason.equals(REASON_CONFIRM_FOR_EDIT)) {
                alreadyGuessed = guessesList.get(i);
            }
            AddGuessLayout addGuessLayout = new AddGuessLayout(this, round, names.get(i), alreadyGuessed);
            textViews.add(addGuessLayout);
            container.addView(addGuessLayout);
        }
        if(activePlayer >= 0 && activePlayer < names.size() && !reason.equals(REASON_CONFIRM) && !reason.equals(REASON_CONFIRM_FOR_EDIT)){
            textViews.get(activePlayer).setTextColor(GameActivity.highlightTextColor);
        }
        changeTotalGuess();
    }

    private void setHeader(){
        TextView headerTextView = new TextView(this);
        headerTextView.setTextSize(36);
        if(reason.equals(GameActivity.REASON_EDIT)){
            headerTextView.setText("Runde "+round+" anpassen\nVorhersagen eintragen");
            saveButton.setText("Weiter");
        }
        if(reason.equals(REASON_CONFIRM_FOR_EDIT)) {
            headerTextView.setText("Runde "+round+" anpassen\nVorhersagen bestätigen");
            abbortionButton.setText("Zurück");
            cloudButton.setVisibility(View.VISIBLE);
            bombButton.setVisibility(View.VISIBLE);
        }
        if(reason.equals(REASON_ADD)){
            headerTextView.setText("Vorhersagen eintragen\n");
        }
        if(reason.equals(REASON_CONFIRM)){
            headerTextView.setText("Vorhersagen bestätigen\n");
        }
        container.addView(headerTextView);
    }

    public void onSaveButtonPressed(View view) {
        ArrayList<Integer> guesses = new ArrayList<>();
        int sum = 0;
        for(AddGuessLayout addGuessLayout: textViews){
            guesses.add(addGuessLayout.getValue());
            sum += addGuessLayout.getValue();
        }
        if(sum == round && (reason.equals(GameActivity.REASON_ADD) || reason.equals(GameActivity.REASON_EDIT))){
            Toast.makeText(this,"Rundenzahl ist gleich der Vorhersagen.",Toast.LENGTH_SHORT).show();
            return;
        }
        if((sum != round) && (reason.equals(REASON_CONFIRM) || reason.equals(REASON_CONFIRM_FOR_EDIT))) {
            Toast.makeText(this, "Rundenzahl ist ungleich der Vorhersagen.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(reason.equals(GameActivity.REASON_EDIT)){
            Intent intentToStart = new Intent(this, GuessesActivity.class);
            intentToStart.putExtra(ROUND_TOKEN, round);
            intentToStart.putExtra(PLAYER_NAMES_TOKEN, names);
            intentToStart.putExtra(GUESSES_TOKEN, guesses);
            intentToStart.putExtra(REASON_FOR_CALL, REASON_CONFIRM_FOR_EDIT);
            startActivityForResult(intentToStart, EDIT_TOKEN_TWO);
            Intent intent = new Intent(this, WaitingActivity.class);
            startActivity(intent);
            return;
        }
        if(reason.equals(REASON_CONFIRM_FOR_EDIT)){
            if(isBombActivated) round++;
            Intent intent = getIntent();
            intent.putExtra(GUESSES_TOKEN, guessesList);    // already guessed
            intent.putExtra(CONFIRMATION_TOKEN, guesses);   // confirmation
            intent.putExtra(ROUND_TOKEN, round);            // edited round
            setResult(RESULT_OK, intent);
            finish();
            return;
        }
        Intent intent = getIntent();
        intent.putExtra(GUESSES_TOKEN, guesses);
        setResult(RESULT_OK, intent);
        finish();
    }

    public void onAbbortionButtonPressed(View view) {
        setResult(RESULT_CANCELED, null);
        finish();
    }

    public void changeTotalGuess(){
        int sum = 0;
        for(AddGuessLayout addGuessLayout: textViews){
            sum += addGuessLayout.getValue();
        }
        totalGuessesTextView.setText(sum+" (von "+round+")");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == REQUEST_CODE_CLOUD_ACTION) {
                int playerIndex = data.getIntExtra(PLAYER_INDEX_TOKEN, -1);
                int newGuess = data.getIntExtra(NEW_GUESS_NUMBER_TOKEN, -1);
                cloudButton.setVisibility(View.INVISIBLE);
                guessesList.set(playerIndex, newGuess);
            }
            else{
                setResult(RESULT_OK, data);
                finish();
            }
        }
    }

    public void onBombButtonClick(View view) {
        if(isBombActivated) {
            isBombActivated = false;
            // Bombe wurde deaktiviert
            round++;
            Toast.makeText(this,"Bombe wurde deaktiviert", LENGTH_SHORT).show();

            bombButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bomb_background_passive)));
        }
        else {
            isBombActivated = true;
            // Bombe wurde aktiviert
            round--;
            Toast.makeText(this,"Bombe wurde aktiviert", LENGTH_SHORT).show();
            bombButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bomb_backgorund_active)));
        }
        changeTotalGuess();
    }

    public void onCloudFloatingButtonClick(View view) {
        Intent intent = new Intent(this, CloudActivity.class);
        intent.putExtra(PLAYER_NAMES_TOKEN, names);
        intent.putExtra(POINT_TEXT_VIEWS_TOKEN, guessesList);
        intent.putExtra(ROUND_TOKEN, round);
        startActivityForResult(intent, REQUEST_CODE_CLOUD_ACTION);
    }
}