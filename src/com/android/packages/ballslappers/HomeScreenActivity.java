package com.android.packages.ballslappers;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class HomeScreenActivity extends Activity {
	public static boolean SOUND_ENABLED;
	public static MediaPlayer mediaPlayer;
	public static SharedPreferences settings;
	public static Context mContext;
	
	public void initScores() {
		SharedPreferences.Editor e = settings.edit();
		String difficulty = "easy";
		
		while(true)
		{
			int size = settings.getInt("highscore_scores_"+difficulty+"_size", 0);
			if(size == 0)
			{
				e.putInt("highscore_scores_"+difficulty+"_size", 9);
			}
			size = settings.getInt("highscore_names_"+difficulty+"_size", 0);
			if(size == 0)
			{
				e.putInt("highscore_names_"+difficulty+"_size", 9);
			}
			for(int i = 0; i < 10; i++)
			{
				int score = settings.getInt("highscore_scores_"+difficulty+"_"+i, 0);
				if(score == 0)
				{
					e.putInt("highscore_scores_"+difficulty+"_"+i, score);
					e.putString("highscore_names_"+difficulty+"_"+i, "slap");
				}
			}
			
			if(difficulty.equals("easy"))
				difficulty = "medium";
			else if(difficulty.equals("medium"))
				difficulty = "hard";
			else if(difficulty.equals("hard"))
				break;
		}
		
		e.commit();
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        
        settings = getPreferences(Activity.MODE_PRIVATE);
	    SOUND_ENABLED = settings.getBoolean("sound_enabled", true);
	    
	    mContext = getApplicationContext();
	    
        mediaPlayer = MediaPlayer.create(mContext, R.raw.homescreen);
        mediaPlayer.setLooping(true);
	    
	    if(HomeScreenActivity.SOUND_ENABLED)
	    {
	        mediaPlayer.start(); // no need to call prepare(); create() does that for you
	    }
	    else
	    {
	    	
	    }
	    
	    initScores();
    }
    
    protected void onResume(){
    	super.onResume();
    	ToggleButton tb = (ToggleButton) findViewById(R.id.toggleSound);
        tb.setChecked(HomeScreenActivity.SOUND_ENABLED);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home_screen, menu);
        return true;
    }
    
    public void SinglePlayerCreate(View view){
    	Intent intent = new Intent(this, SinglePlayerCreateActivity.class);
    	startActivity(intent);
    }
    
    public void MultiplayerCreate(View view){
    	Intent intent = new Intent(this, MultiplayerLobbyActivity.class);
    	startActivity(intent);
    }
    
    public void TutorialScreen(View view){
    	Intent intent = new Intent(this, TutorialScreenActivity.class);
    	startActivity(intent);
    }
    
    public void OptionsScreen(View view){
    	Intent intent = new Intent(this, OptionsScreenActivity.class);
    	startActivity(intent);
    }
    
    public void toggleMusic(View view){
    	if(HomeScreenActivity.SOUND_ENABLED)
    		HomeScreenActivity.mediaPlayer.stop();
    	else
    	{
    		try
			{
				HomeScreenActivity.mediaPlayer.prepare();
			} catch (IllegalStateException e1)
			{
				e1.printStackTrace();
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
    		HomeScreenActivity.mediaPlayer.start();
    	}
    	
    	HomeScreenActivity.SOUND_ENABLED = !HomeScreenActivity.SOUND_ENABLED;
    	SharedPreferences.Editor e = HomeScreenActivity.settings.edit();
    	e.putBoolean("sound_enabled", HomeScreenActivity.SOUND_ENABLED);
    	e.commit();
    }
    
    protected void onDestroy(){
    	HomeScreenActivity.mediaPlayer.release();
    	
    	super.onDestroy();
    }
}
