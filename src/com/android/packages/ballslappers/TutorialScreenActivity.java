package com.android.packages.ballslappers;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TutorialScreenActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_screen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tutorial_screen, menu);
        return true;
    }

}
