package com.android.packages.ballslappers;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MultiplayerCreateActivity extends Activity {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_create);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_multiplayer_create, menu);
        return true;
    }

}
