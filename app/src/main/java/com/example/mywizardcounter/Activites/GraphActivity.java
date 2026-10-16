package com.example.mywizardcounter.Activites;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.mywizardcounter.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Collections;

public class GraphActivity extends AppCompatActivity {

    ArrayList<ArrayList<Integer>> allXAxises = new ArrayList<>();
    ArrayList<ArrayList<Integer>> allYAxises = new ArrayList<>();
    ArrayList<String> playerNames;
    GraphView graph;
    ArrayList<Integer> colorInts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        int[] colors = new int[]{Color.GREEN, Color.RED, Color.BLUE, Color.MAGENTA, Color.CYAN, Color.BLACK};
        if(getIntent().hasExtra(GameActivity.PLAYER_COLORS_TOKEN)){
            if(getIntent().getIntArrayExtra(GameActivity.PLAYER_COLORS_TOKEN) != null)
                colors = getIntent().getIntArrayExtra(GameActivity.PLAYER_COLORS_TOKEN);
        }
        colorInts = new ArrayList<>();
        for(int i = 0; i < 6; i++)      // 6 is max number of players
            colorInts.add(colors[i]);

        this.graph = findViewById(R.id.graphView);
        graph.setVisibility(View.VISIBLE);

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(15);

        allXAxises = (ArrayList<ArrayList<Integer>>) getIntent().getSerializableExtra(GameActivity.X_AXIS_TOKEN);
        allYAxises = (ArrayList<ArrayList<Integer>>) getIntent().getSerializableExtra(GameActivity.Y_AXIS_TOKEN);
        playerNames = getIntent().getStringArrayListExtra(GameActivity.PLAYER_NAMES_TOKEN);

        for(int player = 0; player < allYAxises.size(); player++){
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
            series.appendData(new DataPoint(0,0),true,25);
            int sum = player;
            for(int i = 0; i < allXAxises.get(0).size(); i++){
                sum += allYAxises.get(player).get(i);
                series.appendData(new DataPoint(allXAxises.get(player).get(i),sum),true,250);
            }
            series.setTitle(playerNames.get(player));
            series.setColor(colorInts.get(player));
            graph.getLegendRenderer().setVisible(true);
            graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
            graph.addSeries(series);
        }
    }
}