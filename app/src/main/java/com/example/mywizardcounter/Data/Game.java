package com.example.mywizardcounter.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {
    private static final long serialVersionUID = 1;

    private int numberOfPlayedRounds;
    private List<String> names;
    private List<List<Integer>> scoredPoints;
    private List<List<Integer>> guesses;
    private int uid = -1;
    private int maxRounds = 20;

    private boolean hasEnded = false;

    public Game(){
        numberOfPlayedRounds = 0;
        names = new ArrayList<>();
        scoredPoints = new ArrayList<>();
        guesses = new ArrayList<>();
        uid = (int) Math.floor(Math.random() * 742354861); // try to be random
    }

    public Game(int uid) {
        this();
        this.uid = uid;
    }

    public int getNumberOfPlayedRounds() {
        return numberOfPlayedRounds;
    }

    public void setNumberOfPlayedRounds(int numberOfPlayedRounds) {
        this.numberOfPlayedRounds = numberOfPlayedRounds;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
        this.maxRounds = 60 / names.size();
    }

    public List<List<Integer>> getScoredPoints() {
        return scoredPoints;
    }

    public void setScoredPoints(List<List<Integer>> scoredPoints) {
        this.scoredPoints = scoredPoints;
        int numberOfFinishedRounds = 0;
        for(int i = 0; i < scoredPoints.size(); i++){
            if(scoredPoints.get(i).size() > 0) numberOfFinishedRounds++;
        }
        this.setNumberOfPlayedRounds(numberOfFinishedRounds);
        if(numberOfFinishedRounds == maxRounds){ // game has ended
            hasEnded = true;
        }
    }

    public String toString(){
        String output = "ID: " + getUID() + "\t";
        output = output + "Gespielte Runden: " + numberOfPlayedRounds + "\nEs haben gespielt: " + stringFromList(names);
        return output;
    }

    private String stringFromList(List<String> names) {
        String output = "";
        for(int i = 0; i < names.size(); i++){
            output = output + names.get(i) + " ";
        }
        return output;
    }

    static public Game createGame(){
        Game game = new Game();
        game.setNumberOfPlayedRounds(4);
        game.setScoredPoints(new ArrayList<>());

        List<String> names = new ArrayList<>();
        names.add("Lisa");
        names.add("Laura");
        names.add("Nele");
        game.setNames(names);

        List<List<Integer>> pointList = new ArrayList<>();
        List<List<Integer>> guessList = new ArrayList<>();
        for(int round = 0; round < 4; round++){
            List<Integer> pointsInRound  = new ArrayList<>();
            List<Integer> guessesInRound = new ArrayList<>();
            for(int player = 0; player < 3; player++){
                pointsInRound.add(player*10);
                guessesInRound.add(player);
            }
            pointList.add(pointsInRound );
            guessList.add(guessesInRound);
        }
        game.setScoredPoints(pointList);
        game.setGuesses(guessList);
        return game;
    }

    public int getUID(){
        return uid;
    }

    public List<List<Integer>> getGuesses() {
        return guesses;
    }

    public boolean hasEnded(){
        return hasEnded;
    }

    public void setGuesses(List<List<Integer>> guesses) {
        this.guesses = guesses;
    }

    public String toLongString() {
        String str = "";
        str += "ID = " + getUID() + "\n";
        str += "numberOfPlayedRounds: " + getNumberOfPlayedRounds() + "\n";
        str += "Namen: ";
        for(String name : getNames()) {
            str += name + " ";
        }
        str += "\nPunkte: ";
        for(List<Integer> pointsInRound : getScoredPoints()){
            str += "\nPunkte: ";
            for(int point: pointsInRound){
                str += point + " ";
            }
            str += "\n";
        }
        for(List<Integer> guessesInRound : getGuesses()){
            str += "\nGuesses: ";
            for(int guess: guessesInRound){
                str += guess + " ";
            }
            str += "\n";
        }
        return str;
    }
}
