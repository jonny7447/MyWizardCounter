package com.example.mywizardcounter.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import top.defaults.colorpicker.ColorPickerPopup;

import com.example.mywizardcounter.R;

import java.util.ArrayList;

public class GetNamesActivity extends AppCompatActivity {

    public static final String NAME_TOKEN = "NAMES";

    ArrayList<EditText> nameEditTexts;
    ArrayList<View> colorViews;
    int numberOfPlayers;
    int[] playerColors = new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.BLACK};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_names);
        nameEditTexts = new ArrayList<>();
        nameEditTexts.add(findViewById(R.id.name1EditText));
        nameEditTexts.add(findViewById(R.id.name2EditText));
        nameEditTexts.add(findViewById(R.id.name3EditText));
        nameEditTexts.add(findViewById(R.id.name4EditText));
        nameEditTexts.add(findViewById(R.id.name5EditText));
        nameEditTexts.add(findViewById(R.id.name6EditText));

        colorViews = new ArrayList<>();
        colorViews.add(findViewById(R.id.preview_selected_color_1));
        colorViews.add(findViewById(R.id.preview_selected_color_2));
        colorViews.add(findViewById(R.id.preview_selected_color_3));
        colorViews.add(findViewById(R.id.preview_selected_color_4));
        colorViews.add(findViewById(R.id.preview_selected_color_5));
        colorViews.add(findViewById(R.id.preview_selected_color_6));

        numberOfPlayers = getIntent().getIntExtra(ChooseNumberOfPlayersActivity.PLAYER_TOKEN,2);
        for(int i = 0; i < numberOfPlayers; i++){
            colorViews.get(i).setBackgroundColor(playerColors[i]);
        }
        for(int i = numberOfPlayers; i < 6; i++){
            nameEditTexts.get(i).setVisibility(View.GONE);
            colorViews.get(i).setVisibility(View.GONE);
        }

        // if this got not called by a long click
        if(!getIntent().getBooleanExtra(ChooseNumberOfPlayersActivity.LONG_CLICK_TOKEN,false)){
            findViewById(R.id.showStatisticsButton).setVisibility(View.GONE);
        }

//        prefillNames();
    }

    public void onWeiterButtonClicked(View view) {
        ArrayList<String> names = new ArrayList<>();
        // extract names
        for(int i = 0; i < numberOfPlayers; i++){
            String name = nameEditTexts.get(i).getText().toString();
            if(name.isEmpty()){
                Toast.makeText(this,"Bitte fülle alle Namen aus.",Toast.LENGTH_SHORT).show();
                return;
            }
            names.add(name);
        }
        // check if every name is unique
        for(int i = 0; i < numberOfPlayers; i++) {
            for (int j = 0; j < i; j++) {
                String name = names.get(i);
                if(name.compareTo(names.get(j)) == 0){
                    Toast.makeText(this,"Zwei Namen sind gleich.",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(NAME_TOKEN, names);
        intent.putExtra(ChooseNumberOfPlayersActivity.PLAYER_TOKEN, numberOfPlayers);
        intent.putExtra(GameActivity.PLAYER_COLORS_TOKEN, playerColors);
        startActivity(intent);
    }

    public void onStatisticButtonClicked(View view) {
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }

    private void prefillNames(){
        for(int i = 0 ; i < 6; i++){
            nameEditTexts.get(i).setText("Sp_" + i);
        }
    }

    public void onColorViewClick(View view) {
        int index = colorViews.indexOf(view);
        new ColorPickerPopup.Builder(GetNamesActivity.this).initialColor(playerColors[index]).enableBrightness(true).enableAlpha(true).okTitle("Speichern").cancelTitle("Abbrechen").showIndicator(true).showValue(false).build().show(view, new ColorPickerPopup.ColorPickerObserver() {
            @Override
            public void onColorPicked(int color) {
                playerColors[index] = color;
                colorViews.get(index).setBackgroundColor(color);
            }
        });
    }
}