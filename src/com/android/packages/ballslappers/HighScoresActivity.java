package com.android.packages.ballslappers;

import java.io.IOException;
import java.util.ArrayList;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

public class HighScoresActivity extends Activity {
	public static boolean SOUND_ENABLED;
	public static MediaPlayer mediaPlayer;
	public static SharedPreferences settings;
	public static Context mContext;
	
	public ArrayList<Integer> loadScores(String arrayName, Context mContext) {  
	    SharedPreferences prefs = HomeScreenActivity.settings; 
	    int size = prefs.getInt(arrayName + "_size", 0);  
	    ArrayList<Integer> a = new ArrayList<Integer>();
	    for(int i=0;i<size;i++)  
	        a.add(prefs.getInt(arrayName + "_" + i, 0));  
	    return a;  
	}
	
	public ArrayList<String> loadNames(String arrayName, Context mContext) {  
	    SharedPreferences prefs = HomeScreenActivity.settings; 
	    int size = prefs.getInt(arrayName + "_size", 0);
	    ArrayList<String> a = new ArrayList<String>();
	    for(int i=0;i<size;i++)  
	        a.add(prefs.getString(arrayName + "_" + i, null));  
	    return a;  
	}
	
	public boolean saveScores(ArrayList<Integer> array, String arrayName, Context mContext) {   
	    SharedPreferences prefs = HomeScreenActivity.settings;
	    SharedPreferences.Editor editor = prefs.edit();  
	    editor.putInt(arrayName +"_size", array.size());  
	    for(int i=0;i<array.size();i++)  
	        editor.putInt(arrayName + "_" + i, array.get(i));
	    return editor.commit();  
	}
	
	public boolean saveNames(ArrayList<String> array, String arrayName, Context mContext) {   
	    SharedPreferences prefs = HomeScreenActivity.settings;
	    SharedPreferences.Editor editor = prefs.edit();  
	    editor.putInt(arrayName +"_size", array.size());  
	    for(int i=0;i<array.size();i++)  
	        editor.putString(arrayName + "_" + i, array.get(i));
	    return editor.commit();  
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_screen);
		Bundle bundle = getIntent().getExtras();
        
        int score = 0;
        String difficulty = "easy";
        String name = HomeScreenActivity.settings.getString("userName", "AAA");
        
        if(!bundle.isEmpty())
        {
        	score = bundle.getInt("score");
        	difficulty = bundle.getString("difficulty");
        }

        ArrayList<String> highScoreNames = loadNames("highscore_names_" + difficulty, getApplicationContext());
        ArrayList<Integer> highScoreScores = loadScores("highscore_scores_" + difficulty, getApplicationContext());

		ListView high_names = (ListView) findViewById(R.id.high_names);
		ListView high_scores = (ListView) findViewById(R.id.high_scores);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(HighScoresActivity.this, android.R.layout.simple_list_item_1);
        ArrayAdapter<String> scoreAdapter = new ArrayAdapter<String>(HighScoresActivity.this, android.R.layout.simple_list_item_1);
        high_names.setAdapter(nameAdapter);
        high_scores.setAdapter(scoreAdapter);

        /*highScoreNames.add("aaa");
        highScoreNames.add("bbb");
        highScoreNames.add("ccc");
        highScoreNames.add("ddd");
        highScoreScores.add(4);
        highScoreScores.add(3);
        highScoreScores.add(2);
        highScoreScores.add(1);*/
        
        int pos = -1;
        boolean done = false;
        int size = highScoreNames.size();
        for(int i = 0; i < size; i++)
        {
        	nameAdapter.add(highScoreNames.get(i));
        	if(score > highScoreScores.get(i) && !done)
        	{
        		pos = i;
        		done = true;
        	}
        	scoreAdapter.add(highScoreScores.get(i) + "");
        }
        if(pos != -1)
        {
        	highScoreScores.add(pos, score);
        	highScoreNames.add(pos, name);
        	nameAdapter.insert(name, pos);
        	scoreAdapter.insert(score+"", pos);

        	//nameAdapter.remove(nameAdapter.getItem(10));
        	//nameAdapter.remove(scoreAdapter.getItem(10));
        }

        saveNames(highScoreNames, "highscore_names_" + difficulty, getApplicationContext());
        saveScores(highScoreScores, "highscore_scores_" + difficulty, getApplicationContext());
    }
    
    protected void onResume(){
    	super.onResume();
    	//ToggleButton tb = (ToggleButton) findViewById(R.id.toggleSound);
        //tb.setChecked(HomeScreenActivity.SOUND_ENABLED);
    }
}