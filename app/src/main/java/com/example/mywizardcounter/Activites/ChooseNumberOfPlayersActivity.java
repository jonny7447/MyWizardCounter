package com.example.mywizardcounter.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mywizardcounter.R;

import java.util.ArrayList;

public class ChooseNumberOfPlayersActivity extends AppCompatActivity {

    public static final String PLAYER_TOKEN = "Number Of Players";
    public static final String LONG_CLICK_TOKEN = "this was a long click";

    private ArrayList<Integer> buttonIDs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_number_of_players);
        buttonIDs.add(R.id.threePlayerButton);
        buttonIDs.add(R.id.fourPlayerButton );
        buttonIDs.add(R.id.fivePlayerButton );
        buttonIDs.add(R.id.sixPlayerButton  );

        for(int buttonID: buttonIDs){
            Button button = findViewById(buttonID);
            button.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPlayerNumberSelectedClick(v, true);
                    return true;
                }
            });
        }
    }

    public void onPlayerNumberSelectedClick(View view) {
        onPlayerNumberSelectedClick(view, false);
    }

    private void onPlayerNumberSelectedClick(View view, boolean wasLongClick){
        int numberOfPlayers = 3 + buttonIDs.indexOf(view.getId());
        // Toast.makeText(this,numberOfPlayers+" Spieler",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, GetNamesActivity.class);
        intent.putExtra(LONG_CLICK_TOKEN, wasLongClick);
        intent.putExtra(PLAYER_TOKEN, numberOfPlayers);
        startActivity(intent);
    }
}