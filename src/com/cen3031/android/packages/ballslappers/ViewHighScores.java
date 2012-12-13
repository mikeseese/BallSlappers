package com.cen3031.android.packages.ballslappers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class ViewHighScores extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_high_scores);
    }
    
    public void viewEasy(View v) {
    	Bundle bundle = new Bundle();
    	bundle.putString("difficulty", "easy");
    	bundle.putBoolean("view", true);
    	Intent intent = new Intent(this, HighScoresActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);
    }
    
    public void viewMedium(View v) {
    	Bundle bundle = new Bundle();
    	bundle.putString("difficulty", "medium");
    	bundle.putBoolean("view", true);
    	Intent intent = new Intent(this, HighScoresActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);
    }
    
    public void viewHard(View v) {
    	Bundle bundle = new Bundle();
    	bundle.putString("difficulty", "hard");
    	bundle.putBoolean("view", true);
    	Intent intent = new Intent(this, HighScoresActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);
    }
    
    public void toggleMusic(View view){
    	if(HomeScreenActivity.SOUND_ENABLED)
    		HomeScreenActivity.mediaPlayer.pause();
    	else
    	{
       		HomeScreenActivity.mediaPlayer.start();
    	}
    	
    	HomeScreenActivity.SOUND_ENABLED = !HomeScreenActivity.SOUND_ENABLED;
    	SharedPreferences.Editor e = HomeScreenActivity.settings.edit();
    	e.putBoolean("sound_enabled", HomeScreenActivity.SOUND_ENABLED);
    	e.commit();
    }
}
