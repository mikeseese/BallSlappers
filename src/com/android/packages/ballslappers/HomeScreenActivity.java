package com.android.packages.ballslappers;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class HomeScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
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
    	Intent intent = new Intent(this, MultiplayerCreateActivity.class);
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
}
