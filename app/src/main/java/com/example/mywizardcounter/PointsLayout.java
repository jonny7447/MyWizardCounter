package com.example.mywizardcounter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import java.io.Serializable;

public class PointsLayout extends androidx.appcompat.widget.LinearLayoutCompat implements Serializable {

    private int guess = -1;
    private int points;
    private int guessesWidth = -1;

    private final TextView pointsTextView;
    private final TextView guessesTextView;

    public PointsLayout(Context context){
        super(context);
        this.setOrientation(LinearLayoutCompat.HORIZONTAL);

        pointsTextView  = new TextView(context);
        guessesTextView = new TextView(context);

        guessesTextView.setBackgroundColor(getResources().getColor(R.color.grey));
        guessesTextView.setTextColor(getResources().getColor(R.color.white));
        pointsTextView.setGravity(Gravity.RIGHT);
        guessesTextView.setGravity(Gravity.CENTER);

        this.addView(pointsTextView );
        this.addView(guessesTextView);
        this.setBackgroundResource(R.drawable.rectangle_back);
    }

    public TextView getPointsTextView(){
        return this.pointsTextView;
    }
    public TextView getGuessesTextView(){
        return this.guessesTextView;
    }

    public void setGuess(int guess){
        this.guess = guess;
        guessesTextView.setText(""+guess);
        if(guess > 9) {
            guessesTextView.setWidth(2 * guessesWidth);
            pointsTextView.setWidth(pointsTextView.getWidth() - guessesWidth);
        }
    }

    public int getGuess(){
        return guess;
    }

    public void calcPoints(int wonRounds){
        int pointsAchieved = 0;
        if(wonRounds == guess) pointsAchieved = 10*(2+guess);
        else pointsAchieved = -10*(Math.abs(guess-wonRounds));
        pointsTextView.setText(pointsAchieved+" ");
        this.points = pointsAchieved;
    }

    public void setTextSize(int size){
        pointsTextView .setTextSize(size);
        guessesTextView.setTextSize(size);
    }

    public void setWidth(int pointsWidth, int guessesWidth){
        if(pointsWidth  > 0) pointsTextView.setWidth(pointsWidth);
        if(guessesWidth > 0) {
            guessesTextView.setWidth(guessesWidth);
            this.guessesWidth = guessesWidth;
        }
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points){
        this.points = points;
        pointsTextView.setText(points+" ");
    }

    @Override
    public void setBackgroundColor(int color) {
        pointsTextView.setBackgroundColor(color);
    }
}
