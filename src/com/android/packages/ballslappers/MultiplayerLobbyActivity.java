package com.android.packages.ballslappers;

import java.io.IOException;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ToggleButton;

public class MultiplayerLobbyActivity extends ListActivity {
	ArrayAdapter<String> mArrayAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        setContentView(R.layout.activity_multiplayer_lobby);
		
		ListView listDevicesFound = (ListView) findViewById(android.R.id.list);
        mArrayAdapter = new ArrayAdapter<String>(MultiplayerLobbyActivity.this, android.R.layout.simple_list_item_1);
        listDevicesFound.setAdapter(mArrayAdapter);
    }
    
    protected void onResume(){
    	super.onResume();
    	ToggleButton tb = (ToggleButton) findViewById(R.id.toggleSound);
        tb.setChecked(HomeScreenActivity.SOUND_ENABLED);
    }
    
    public void CreateMultiplayerGame(View v){
    	Intent intent = new Intent(this, MultiplayerCreateActivity.class);
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
}
