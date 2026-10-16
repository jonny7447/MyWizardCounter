package com.example.mywizardcounter.Activites;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.mywizardcounter.Activites.CloudActivity.NEW_GUESS_NUMBER_TOKEN;
import static com.example.mywizardcounter.Activites.CloudActivity.PLAYER_INDEX_TOKEN;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mywizardcounter.Data.Game;
import com.example.mywizardcounter.PointsLayout;
import com.example.mywizardcounter.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    public static final String ROUND_TOKEN = "ROUND";
    public static final String GUESSES_TOKEN = "GUESSES";
    public static final String X_AXIS_TOKEN = "x axis";
    public static final String Y_AXIS_TOKEN = "y axis";
    public static final String PLAYER_NAMES_TOKEN = "PlayerNamesXXX_TOKEN";
    public static final String ACTIVE_PLAYER_TOKEN = "ACTIVE_PLAYER_TOKEN";
    public static final String REASON_FOR_CALL = "REASONf0RCa11";
    public static final String REASON_ADD = "Add";
    public static final String REASON_CONFIRM = "Confirm";
    public static final String REASON_CONFIRM_FOR_EDIT = "Confirm for edit";
    public static final String REASON_EDIT = "Edit";
    public static final String POINT_TEXT_VIEWS_TOKEN = "PointTextViews";
    public final static String STORAGE_FILE = "myWizardCounterSaves.txt";
    public static final String PLAYER_COLORS_TOKEN = "Das hier sind die SpielerFarben. Bam.";
    public static final int ADD_GUESSES_REQUEST_CODE = 17;
    public static final int CONFIRM_GUESSES_REQUEST_CODE = 18;
    public static final int EDIT_GUESSES_REQUEST_CODE = 19;
    public static final int NORMAL_GAME = 0;
    public static final int CONTINUE_GAME = 1;
    public final static int REQUEST_CODE_CLOUD_ACTION = 117;
    public static final int ONLY_SHOW_GAME = 118;

    int numberOfPlayers;
    int screenWidth;
    int screenHeight;
    int totalRounds;
    private final int guessesWidth = 55;
    int nextRound;
    int activePlayer;
    int numberOfGuesses;
    public static int highlightTextColor = 0x99FF0033;
    int normalTextColor;
    int modus = NORMAL_GAME;
    int[] playerColors;
    boolean backWasPressed = false;
    Game game = null;

    ArrayList<ArrayList<PointsLayout>> pointTextViewList;
    ArrayList<TextView> nameTextViews;
    ArrayList<String> playersNames;
    ArrayList<TextView> totalPointTextViews;
    ArrayList<LinearLayout> roundLinearLayout;
    ArrayList<Integer> activeGuesses;
    List<Game> gameList;

    LinearLayout container;
    LinearLayout nameLayout;
    LinearLayout pointsLayout;
    FloatingActionButton guessesButton;
    FloatingActionButton cloudButton;
    FloatingActionButton bombButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        if(getIntent().hasExtra(ChooseNumberOfPlayersActivity.PLAYER_TOKEN)){
            numberOfPlayers = getIntent().getIntExtra(ChooseNumberOfPlayersActivity.PLAYER_TOKEN,0);
        }
        if(getIntent().hasExtra(GetNamesActivity.NAME_TOKEN)){
            playersNames = getIntent().getStringArrayListExtra(GetNamesActivity.NAME_TOKEN);
        }
        if(getIntent().hasExtra(StatisticsActivity.KEEP_PLAYING_TOKEN)) modus = CONTINUE_GAME;
        if(getIntent().hasExtra(StatisticsActivity.ONLY_SHOW_GAME_STRING_TOKEN)) {
            modus = ONLY_SHOW_GAME;
            findViewById(R.id.guessFloatingButton).setVisibility(View.GONE);
        }
        if(getIntent().hasExtra(StatisticsActivity.SHOW_GAME_TOKEN)){
            game = (Game) getIntent().getSerializableExtra(StatisticsActivity.SHOW_GAME_TOKEN);
            if(game != null){
                playersNames = new ArrayList<>();
                playersNames.addAll(game.getNames());
                numberOfPlayers = playersNames.size();
            }
        }
        if(getIntent().hasExtra(PLAYER_COLORS_TOKEN)){playerColors = getIntent().getIntArrayExtra(GameActivity.PLAYER_COLORS_TOKEN);}

        pointTextViewList   = new ArrayList<>();
        roundLinearLayout   = new ArrayList<>();
        nameTextViews       = new ArrayList<>();
        totalPointTextViews = new ArrayList<>();
        activeGuesses       = null;

        loadGames();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        container = findViewById(R.id.containerLinearLayout);
        guessesButton = findViewById(R.id.guessFloatingButton);
        cloudButton = findViewById(R.id.cloudButton);
        bombButton = findViewById(R.id.bombButton);
        nameLayout = findViewById(R.id.nameLayout);
        pointsLayout = findViewById(R.id.pointsLayout);

        cloudButton.setVisibility(View.INVISIBLE);
        bombButton.setVisibility(View.INVISIBLE);
        nextRound = 1;
        numberOfGuesses = 1;
        activePlayer = -1;

        createPointTable();

        if(game != null) restoreGame(game);

        totalRounds = 60 / numberOfPlayers;
        normalTextColor = nameTextViews.get(0).getCurrentTextColor();

        changeActivePlayer();
        calcAllTotalSums();

//        prefillFirstNRounds(totalRounds - 2);
    }

    private void createPointTable() {
        int paddingDefinedInXML = 40;
        int textSize = Math.min(25, screenWidth / numberOfPlayers / 10);
        int sizeOfRoundCounterTextView = 90;
        int pointsWidth = (screenWidth - sizeOfRoundCounterTextView - 2 * paddingDefinedInXML)/numberOfPlayers - guessesWidth;

        // create PlayerNameTVs
        TextView emptyTextView = new TextView(this);
        emptyTextView.setWidth(sizeOfRoundCounterTextView);
        nameLayout.addView(emptyTextView);

        for(int name = 0; name < numberOfPlayers; name++){
            TextView playerNameTextView = new TextView(this);
            playerNameTextView.setText(playersNames.get(name));
            playerNameTextView.setTextSize(textSize);
            playerNameTextView.setWidth(pointsWidth + guessesWidth);
            playerNameTextView.setGravity(Gravity.CENTER);
            playerNameTextView.setBackgroundResource(R.drawable.rectangle_back);
//            playerNameTextView.setBackgroundColor(playerColors[name]);
            nameTextViews.add(name, playerNameTextView);

            nameLayout.addView(playerNameTextView);
        }

        for(int round = 0; round < (60/numberOfPlayers); round++) {
            LinearLayout roundLayout = new LinearLayout(this);
            roundLinearLayout.add(round,roundLayout);

            // create round numbers
            TextView roundCounterTextView = new TextView(this);
            roundCounterTextView.setBackgroundColor(getResources().getColor(R.color.lachs));
            roundCounterTextView.setText(""+(round+1));
            roundCounterTextView.setTextSize(textSize);
            roundCounterTextView.setWidth(sizeOfRoundCounterTextView);
            roundCounterTextView.setGravity(Gravity.CENTER);
            roundCounterTextView.setOnClickListener(v -> {
                if(modus != ONLY_SHOW_GAME) inflateEditDialog(Integer.parseInt(((TextView) v).getText().toString()));
            });

            roundLayout.addView(roundCounterTextView);

            ArrayList<PointsLayout> roundArray = new ArrayList<>();
            for(int player = 0; player < numberOfPlayers; player++) {
                // create point TextViews per round
                PointsLayout pointsLayout = new PointsLayout(this);
                pointsLayout.setTextSize(Math.min(25, (int) (screenWidth / numberOfPlayers / 10)));
                pointsLayout.setWidth(pointsWidth, guessesWidth);
                if((round % numberOfPlayers) == player){
                    pointsLayout.setBackgroundColor(getResources().getColor(R.color.yellow));
                }
                roundArray.add(player, pointsLayout);
                roundLayout.addView(pointsLayout);
            }
            pointTextViewList.add(round, roundArray);
            container.addView(roundLayout);
        }
    }

    private int calcTotalSum(int player){
        int sum = 0;
        for(int round = 0; round < totalRounds; round++){
            PointsLayout pointsTV = pointTextViewList.get(round).get(player);
            sum += pointsTV.getPoints();
        }
        return sum;
    }

    private void calcAllTotalSums(){
        for(int player = 0; player < numberOfPlayers; player++){
            nameTextViews.get(player).setText(playersNames.get(player) + "\n" + calcTotalSum(player));
        }
    }

    public void onAddGuessesFloatingButtonClick(View view) {
        if(activeGuesses != null){
            Toast.makeText(this,"Bitte zuerst die erreichten Stiche angeben.", LENGTH_SHORT).show();
            return;
        }
        if(nextRound > totalRounds) return; // game end
        Intent intent = new Intent(this, GuessesActivity.class);
        intent.putExtra(ROUND_TOKEN, nextRound);
        intent.putExtra(PLAYER_NAMES_TOKEN, playersNames);
        intent.putExtra(REASON_FOR_CALL, REASON_ADD);
        intent.putExtra(ACTIVE_PLAYER_TOKEN, activePlayer);
        startActivityForResult(intent, ADD_GUESSES_REQUEST_CODE);
    }

    public void onConfirmGuessesFloatingButtonClick(View view) {
        if(activeGuesses == null){
            onAddGuessesFloatingButtonClick(view);
            return;
        }
        if(nextRound > totalRounds) return;
        Intent intent = new Intent(this, GuessesActivity.class);
        intent.putExtra(ROUND_TOKEN, numberOfGuesses);
        intent.putExtra(PLAYER_NAMES_TOKEN, playersNames);
        intent.putExtra(GUESSES_TOKEN, activeGuesses);
        intent.putExtra(REASON_FOR_CALL, REASON_CONFIRM);
        startActivityForResult(intent, CONFIRM_GUESSES_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null) return;

        if(resultCode != RESULT_OK) return;

        if(data.hasExtra(GUESSES_TOKEN))
            activeGuesses = data.getIntegerArrayListExtra(GUESSES_TOKEN);

        switch (requestCode) {
            case ADD_GUESSES_REQUEST_CODE:
                guessesButton.setForeground(getResources().getDrawable(R.drawable.ic_done_black_36));
                cloudButton.setVisibility(View.VISIBLE);
                bombButton .setVisibility(View.VISIBLE);
                setGuesses(nextRound - 1, activeGuesses);
                numberOfGuesses = nextRound;
                break;
            case EDIT_GUESSES_REQUEST_CODE:
                guessesButton.setForeground(getResources().getDrawable(R.drawable.ic_baseline_add_24));
                ArrayList<Integer> confirmation = data.getIntegerArrayListExtra(GuessesActivity.CONFIRMATION_TOKEN);
                int roundToEdit = data.getIntExtra(ROUND_TOKEN, 1) - 1;
                for (int i = 0; i < numberOfPlayers; i++) {
                    PointsLayout pTV = pointTextViewList.get(roundToEdit).get(i);
                    setGuesses(roundToEdit, activeGuesses);
                    pTV.calcPoints(confirmation.get(i));
                }
                if(roundToEdit + 1 == nextRound) nextRound++;
                activeGuesses = null;
                calcAllTotalSums();
                if (nextRound > totalRounds) { // END OF GAME
                    guessesButton.setVisibility(View.GONE);
                }
                changeActivePlayer();
                break;
            case CONFIRM_GUESSES_REQUEST_CODE:
                guessesButton.setForeground(getResources().getDrawable(R.drawable.ic_baseline_add_24));
                cloudButton.setVisibility(View.INVISIBLE);
                bombButton .setVisibility(View.INVISIBLE);
                for (int i = 0; i < numberOfPlayers; i++) {
                    PointsLayout pTV = pointTextViewList.get(nextRound - 1).get(i);
                    pTV.calcPoints(activeGuesses.get(i));
                }
                activeGuesses = null;
                nextRound++;
                numberOfGuesses = nextRound;
                calcAllTotalSums();
                if (nextRound > totalRounds) { // END OF GAME
                    guessesButton.setVisibility(View.GONE);
                }
                else{
                    changeActivePlayer();
                }
                break;
            case REQUEST_CODE_CLOUD_ACTION:
                int playerIndex = data.getIntExtra(PLAYER_INDEX_TOKEN, -1);
                int newGuess = data.getIntExtra(NEW_GUESS_NUMBER_TOKEN, -1);
                pointTextViewList.get(nextRound - 1).get(playerIndex).setGuess(newGuess);
                cloudButton.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void setGuesses(int round, ArrayList<Integer> guesses){
        for(int i = 0; i < numberOfPlayers; i++){
            pointTextViewList.get(round).get(i).setGuess(guesses.get(i));
        }
    }

    private void inflateEditDialog(int roundNumber) {
        Log.d("LOG_TAG", "roundNumber = " + roundNumber + " guess = " + pointTextViewList.get(nextRound - 1).get(0).getGuess());
        if(roundNumber > nextRound) return;
        if(roundNumber == nextRound){
            // check if guesses were already set
            if(pointTextViewList.get(nextRound - 1).get(0).getGuess() < 0) {
                return;
            } // meaning no guess was set
        }
        new AlertDialog.Builder(this)
                .setMessage("Runde "+roundNumber+" bearbeiten?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> editRound(roundNumber))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void  onBackPressed() {
        String message = "";
        backWasPressed = true;
        if(modus == NORMAL_GAME) {
            message = "Möchtest du das Spiel speichern?";
        }
        else if(modus == CONTINUE_GAME) {
            message = "Möchtest du den Fortschritt speichern?";
        }
        else if(modus == ONLY_SHOW_GAME) {
            super.onBackPressed();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ja", (dialog, which) -> {
                    storeThisGame();
                    super.onBackPressed();
                })
                .setNegativeButton("Nein", (dialog, which) -> {super.onBackPressed();})
                .setNeutralButton("Abbrechen", null)
                .show();
    }

    private void editRound(int roundNumber) {
        ArrayList<Integer> guesses = new ArrayList<>();
        for(int i = 0; i < numberOfPlayers; i++){
            guesses.add(pointTextViewList.get(roundNumber - 1).get(i).getGuess());
        }
        Intent intent = new Intent(this, GuessesActivity.class);
        intent.putExtra(ROUND_TOKEN, roundNumber);
        intent.putExtra(PLAYER_NAMES_TOKEN, playersNames);
        intent.putExtra(GUESSES_TOKEN, guesses);
        intent.putExtra(ACTIVE_PLAYER_TOKEN, (roundNumber - 1) % numberOfPlayers);
        intent.putExtra(REASON_FOR_CALL, REASON_EDIT);
        startActivityForResult(intent, EDIT_GUESSES_REQUEST_CODE);
    }

    private void storeThisGame(){
        if(modus == NORMAL_GAME) storeGames(getGameFromData());
        else updateGame(getGameFromData()); // modus == CONTINUE_GAME
    }

    private Game getGameFromData() {
        if(nextRound <= 0) return null;

        List<List<Integer>> pointList = new ArrayList<>();
        List<List<Integer>> guessList = new ArrayList<>();
        for(int round = 0; round < pointTextViewList.size(); round++){
            List<Integer> pointsInRound  = new ArrayList<>();
            List<Integer> guessesInRound = new ArrayList<>();
            for(int player = 0; player < pointTextViewList.get(0).size(); player++){
                int playerPoints = pointTextViewList.get(round).get(player).getPoints();
                int guess = pointTextViewList.get(round).get(player).getGuess();
                if(playerPoints != 0) pointsInRound.add(playerPoints);
                if(guess >= 0) guessesInRound.add(guess);
            }
            pointList.add(pointsInRound );
            guessList.add(guessesInRound);
        }

        if(this.game == null) {
            if(gameList.size() == 0) this.game = new Game(0);
            else this.game = new Game(gameList.get(gameList.size() - 1).getUID() + 1);
        }
        this.game.setNames(playersNames);
        this.game.setScoredPoints(pointList);
        this.game.setGuesses(guessList);
        return this.game;
    }

    private void storeGames(Game game){
        if(game == null) return;
        if(game.getNumberOfPlayedRounds() > 1) this.gameList.add(game);
        else Toast.makeText(this, "Spiel wurde nicht gespeichert.", LENGTH_SHORT).show();
        try (FileOutputStream fileOutputStream = this.openFileOutput(STORAGE_FILE, Context.MODE_PRIVATE)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(this.gameList);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateGame(Game game) {
        List<List<Integer>> pointList = new ArrayList<>();
        List<List<Integer>> guessList = new ArrayList<>();
        for(int round = 0; round < pointTextViewList.size(); round++){
            List<Integer> pointsInRound  = new ArrayList<>();
            List<Integer> guessesInRound = new ArrayList<>();
            for(int player = 0; player < pointTextViewList.get(0).size(); player++){
                int playerPoints = pointTextViewList.get(round).get(player).getPoints();
                int guess = pointTextViewList.get(round).get(player).getGuess();
                if(playerPoints != 0) pointsInRound.add(playerPoints);
                if(guess >= 0) guessesInRound.add(guess);
            }
            pointList.add(pointsInRound );
            guessList.add(guessesInRound);
        }
        game.setScoredPoints(pointList);
        game.setGuesses(guessList);

        // update GameList
        int index = 0;
        for (Game tempGame: gameList){
            if(tempGame.getUID() == game.getUID()){
//                gameList.remove(index);
                gameList.set(index, game);
                // store GameList
                try (FileOutputStream fileOutputStream = this.openFileOutput(STORAGE_FILE, Context.MODE_PRIVATE)) {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    objectOutputStream.writeObject(this.gameList);
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            index++;
        }
    }

    private void loadGames(){
        try (FileInputStream fileInputStream = this.openFileInput(STORAGE_FILE)){
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            // Does a game list exist?
            if(fileInputStream.available() != 0){
                this.gameList = ((List<Game>) objectInputStream.readObject());
                Log.d("LOGTAG", "GameActivity loadGames() - games succesfull loaded");
            }
            objectInputStream.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        // No game list found
        if(gameList == null) gameList = new ArrayList<>();
    }

    private void changeActivePlayer(){
        if(activePlayer >= 0)       // very first round
            nameTextViews.get(activePlayer).setTextColor(normalTextColor);
        activePlayer = (nextRound - 1) % numberOfPlayers;
        nameTextViews.get(activePlayer).setTextColor(highlightTextColor);
    }

    public void onGraphFloatingButtonClick(View view) {
        ArrayList<ArrayList<Integer>> allXAxises = new ArrayList<>();
        ArrayList<ArrayList<Integer>> allYAxises = new ArrayList<>();
        for(int player = 0; player < numberOfPlayers; player++){
            ArrayList<Integer> xaxis = new ArrayList<>();
            ArrayList<Integer> yaxis = new ArrayList<>();
            for(int i = 0; i < 60/numberOfPlayers; i++){
                if(i < nextRound){
                    xaxis.add(i+1);
                    yaxis.add(pointTextViewList.get(i).get(player).getPoints());
                }
            }
            allXAxises.add(xaxis);
            allYAxises.add(yaxis);
        }
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra(PLAYER_NAMES_TOKEN, playersNames);
        intent.putExtra(X_AXIS_TOKEN, allXAxises);
        intent.putExtra(Y_AXIS_TOKEN, allYAxises);
        intent.putExtra(PLAYER_COLORS_TOKEN, playerColors);
        startActivity(intent);
    }

    public void prefillFirstNRounds(int rounds){
        for(int round = 0; round < rounds; round++){
            ArrayList<Integer> temp = new ArrayList<>();
            for(int player = 0; player < numberOfPlayers; player++){
                temp.add(player);
            }
            setGuesses(round, temp);
            for(int player = 0; player < numberOfPlayers; player++){
                PointsLayout pTV = pointTextViewList.get(round).get(player);
                pTV.calcPoints((int)Math.floor((round+1)*Math.random()));
            }
            calcAllTotalSums();
            nextRound++;
            changeActivePlayer();
        }
    }

    private void restoreGame(Game game) {
        List<List<Integer>> points  = game.getScoredPoints();
        List<List<Integer>> guesses = game.getGuesses();
        nextRound = 1;
        boolean guessesDoneButNoFinalPoints = false;
        for(int round = 0; round < game.getScoredPoints().size(); round++){
            if(points.get(round).size() > 0) nextRound++;
            for(int player = 0; player < numberOfPlayers; player++){
                PointsLayout pointsLayout = pointTextViewList.get(round).get(player);
                if(points.get(round) .size() > 0) pointsLayout.setPoints(points.get(round).get(player));
                if(guesses.get(round).size() > 0) pointsLayout.setGuess(guesses.get(round).get(player));
            }
            if(guesses.get(round).size() > 0 && points.get(round) .size() == 0) guessesDoneButNoFinalPoints = true;
        }
        if(guessesDoneButNoFinalPoints){
            activeGuesses = (ArrayList<Integer>) guesses.get(nextRound - 1);
            guessesButton.setForeground(getResources().getDrawable(R.drawable.ic_done_black_36));
            cloudButton.setVisibility(View.VISIBLE);
            setGuesses(nextRound - 1, activeGuesses);
            numberOfGuesses = nextRound;
        }
        if(game.hasEnded()){
            guessesButton.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        if(!backWasPressed) storeThisGame();
        super.onDestroy();
    }

    public void onCloudFloatingButtonClick(View view) {
        Intent intent = new Intent(this, CloudActivity.class);
        intent.putExtra(PLAYER_NAMES_TOKEN, playersNames);
        ArrayList<Integer> guesses = getCurrentGuesses();
        intent.putExtra(POINT_TEXT_VIEWS_TOKEN, guesses);
        intent.putExtra(ROUND_TOKEN, nextRound);
        startActivityForResult(intent, REQUEST_CODE_CLOUD_ACTION);
    }

    private ArrayList<Integer> getCurrentGuesses() {
        ArrayList<Integer> guesses = new ArrayList<>();
        for(int player = 0; player < numberOfPlayers; player++){
            guesses.add(pointTextViewList.get(nextRound - 1).get(player).getGuess());
        }
        return guesses;
    }

    public void onBombButtonClick(View view) {
        if(numberOfGuesses != nextRound) {
            // Bombe wurde deaktiviert
            numberOfGuesses++;
            Toast.makeText(this,"Bombe wurde deaktiviert", LENGTH_SHORT).show();
            bombButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bomb_background_passive)));
        }
        else {
            // Bombe wurde aktiviert
            numberOfGuesses--;
            Toast.makeText(this,"Bombe wurde aktiviert", LENGTH_SHORT).show();
//            bombButton.setBackgroundColor(getResources().getColor(R.color.bomb_backgorund_active));
            bombButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bomb_backgorund_active)));
        }
    }
}