package com.android.packages.ballslappers;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class OptionsScreenActivity extends Activity {

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//    	super.onCreate(savedInstanceState);
//        TextView textView = new TextView(this);
//        textView.setTextSize(40);
//        textView.setText("Options Screen Activity");
//        setContentView(textView);
//    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option_screen);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_option_screen, menu);
        return true;
    }

}
