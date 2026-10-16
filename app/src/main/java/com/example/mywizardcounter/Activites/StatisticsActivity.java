package com.example.mywizardcounter.Activites;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mywizardcounter.Data.Game;
import com.example.mywizardcounter.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {

    public static final String SHOW_GAME_TOKEN = "please show me the clicked game, thanks";
    public static final String KEEP_PLAYING_TOKEN = "Weiterspielen";
    public static final String ONLY_SHOW_GAME_STRING_TOKEN = "Nur gucken, nicht anfassen!";

    List<Game> gameList;
    ArrayAdapter gameAdapter;
    TextView testTextView;
    ListView gamesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        testTextView = findViewById(R.id.testTextView);
        loadGames();
        calculateStatistics();

        gameAdapter = new ArrayAdapter<>(this, R.layout.statistic_list_layout, gameList.toArray());

        gamesListView = findViewById(R.id.game_list_view);
        gamesListView.setAdapter(gameAdapter);
        gamesListView.setOnItemClickListener((parent, view, position, id) -> {
            Game game = gameList.get(position);
            if(game == null){
                Log.d("LOGTAG", "StatisticsActivity onCreate/OnItemClick GAME NOT FOUND");
                return;
            }
            inflateDialog("Weiterspielen", "Löschen", "Angucken", game);
        });
    }

    private void inflateDialog(String posText, String neuText, String negText, Game game){
        new AlertDialog.Builder(this)
                .setMessage("Was möchtest du mit diesem Spieleintrag tun?")
                .setNeutralButton(neuText, (dialog, which) -> deleteGame(game))
                .setPositiveButton(posText, (dialog, which) -> showGame(game, KEEP_PLAYING_TOKEN))
                .setNegativeButton(negText, (dialog, which) -> showGame(game, ONLY_SHOW_GAME_STRING_TOKEN))
                .show();
    }

    private void showGame(Game game, String token) {
        storeGames();

        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(SHOW_GAME_TOKEN, game);
        intent.putExtra(token, token);
        startActivity(intent);

        super.onDestroy();
    }

    private void deleteGame(Game game) {
        gameList.remove(game);
        calculateStatistics();
//        gameAdapter.notifyDataSetChanged(); for some reason this does not work... so the work arround:
        recreate();
    }

    private void calculateStatistics(){
        String statString = "";
        statString += "Es sind bisher " + gameList.size() + " Spiele gespeichert.\n";
        statString += "Davon wurden " + countGamesPlayedTillEnd() + " zu ende gespielt.\n";
        statString += "Im Schnitt wurden " + calcEverageRounds() + " Runden gespielt.\n";
        statString += "Es wurden\t"  + countGamesNumberOfPlayers(3) + " Spiel(e) mit 3 Spielern,\n\t\t\t\t\t\t\t\t\t"
                                    + countGamesNumberOfPlayers(4) + " Spiel(e) mit 4 Spielern,\n\t\t\t\t\t\t\t\t\t"
                                    + countGamesNumberOfPlayers(5) + " Spiel(e) mit 5 Spielern,\n\t\t\t\t\t\t\t\t\t"
                                    + countGamesNumberOfPlayers(6) + " Spiel(e) mit 6 Spielern gespielt.\n";

        testTextView.setText(statString);
    }

    private void loadGames(){
        try (FileInputStream fileInputStream = this.openFileInput(GameActivity.STORAGE_FILE)){
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            // Does a game list exist?
            if(fileInputStream.available() != 0){
                this.gameList = ((ArrayList<Game>) objectInputStream.readObject());
                Log.d("LOGTAG", "StatisticsActivity loadGames() - games succesfull loaded");
            }
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        // No game list found
        if(gameList == null) gameList = new ArrayList<>();
    }

    public void onClearStatisticsPressed(View view) {
        new AlertDialog.Builder(this)
                .setMessage("Möchtest du wirklich ALLE Statistiken löschen?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteAllGames())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteAllGames() {
        this.gameList = new ArrayList<>();
        storeGames();
        Toast.makeText(this,"Alle Spiele wurden gelöscht",Toast.LENGTH_SHORT).show();
        recreate();
    }

    private void storeGames(){
        try (FileOutputStream fileOutputStream = this.openFileOutput(GameActivity.STORAGE_FILE, Context.MODE_PRIVATE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.gameList);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fakeStatistics() {
        for(int i = 0 ; i < 10; i++){
            gameList.add(Game.createGame());
        }
    }

    @Override
    protected void onStop() {
        storeGames();
        super.onStop();
    }

    private int countGamesPlayedTillEnd(){
        int count = 0;
        for(Game game: gameList){
            if(game.getNumberOfPlayedRounds() == 60/game.getNames().size()) count++;
        }
        return count;
    }

    private int countGamesNumberOfPlayers(int numberOfPlayers) {
        int count = 0;
        for(Game game: gameList){
            if(game.getNames().size() == numberOfPlayers) count++;
        }
        return count;
    }

    private double calcEverageRounds() {
        double count = 0.0;
        for(Game game: gameList){
            count += game.getNumberOfPlayedRounds();
        }
        return Math. round(count/gameList.size()*100.0)/100.0;
    }
}