package com.example.mywizardcounter;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.mywizardcounter.Activites.GuessesActivity;

public class AddGuessLayout extends androidx.appcompat.widget.LinearLayoutCompat{

    ImageButton minusButton;
    ImageButton plusButton ;
    TextView displayTextView;
    TextView nameTextView;
    GuessesActivity context;

    int value;
    int round;
    String name;

    public AddGuessLayout(@NonNull Context context, int round, String name, int guess) {
        super(context);

        this.setOrientation(LinearLayoutCompat.HORIZONTAL);
        this.context = (GuessesActivity) context;
        displayTextView = new TextView(context);
        displayTextView.setText(""+guess);
        displayTextView.setWidth(160);
        displayTextView.setTextSize(36);
        displayTextView.setGravity(Gravity.CENTER);

        nameTextView = new TextView(context);
        nameTextView.setText(name);
        nameTextView.setWidth(350);
        nameTextView.setTextSize(36);

        value = guess;
        this.round = round;
        this.name = name;

        minusButton = new ImageButton(context);
        minusButton.setImageResource(R.drawable.ic_baseline_remove_24);
        minusButton.setOnClickListener(v -> changeDisplayValue(-1));

        plusButton = new ImageButton(context);
        plusButton.setImageResource(R.drawable.ic_baseline_add_24);
        plusButton.setOnClickListener(v -> changeDisplayValue(1));

        this.addView(nameTextView);
        this.addView(minusButton);
        this.addView(displayTextView);
        this.addView(plusButton);
    }

    private void changeDisplayValue(int change){
        value += change;
        if(value < 0) value = round;
        if(value > round) value = 0;
        displayTextView.setText(""+value);
        context.changeTotalGuess();
    }

    public int getValue(){
        return value;
    }

    public void setTextColor(int color) {
        nameTextView.setTextColor(color);
    }
}
