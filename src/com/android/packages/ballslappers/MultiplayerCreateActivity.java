package com.android.packages.ballslappers;

import com.android.packages.ballslappers.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class MultiplayerCreateActivity extends Activity {
	
	
	private boolean powerupsen = false;
	private int numberofplayers;
	private int lives = 1;
	private int tempLives = 0;
	private String newGameName = "New Game";
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_create);
	     
	     
	     Spinner NUMPLAYERS = (Spinner) findViewById(R.id.PlayerSelect);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
	    		 R.array.NUMPLAYERS, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     NUMPLAYERS.setAdapter(adapter2);
	     
	     NUMPLAYERS.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	 public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
    		 	switch(pos){
	    		 	case 0:
	    		 		numberofplayers=1;
	    				break;
	    			case 1:
	    				numberofplayers=2;
	    				break;
	    			case 2:
	    				numberofplayers=3;
	    				break;
	    			case 3:
	    				numberofplayers=4;
	    				break;
	    			default:
	    				break;
	    		}
    		 	
			 } 
	    	 
	    	 public void onNothingSelected(AdapterView<?> arg0) {

	         }
	     });
	     
    }
	
	public void minusOneLife(View view){
     	EditText editText = (EditText) findViewById(R.id.Edit_Lives_Text);
     	tempLives = Integer.parseInt(editText.getText().toString());
     	if(tempLives == 1){
     		tempLives = 100;
     	}
     	lives = tempLives - 1;
     	editText.setText(String.valueOf(lives));
    }
	
	public void plusOneLife(View view){
     	EditText editText = (EditText) findViewById(R.id.Edit_Lives_Text);
     	tempLives = Integer.parseInt(editText.getText().toString());
     	if(tempLives == 99){
     		tempLives = 0;
     	}
     	lives = tempLives + 1;
     	editText.setText(String.valueOf(lives));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_multiplayer_create, menu);
        return true;
    }
    
    public void PlaySinglePlayer(View view){
     	EditText editText = (EditText) findViewById(R.id.GameName);
     	newGameName = editText.getText().toString();
     	editText = (EditText) findViewById(R.id.Edit_Lives_Text);
     	lives = Integer.parseInt(editText.getText().toString());
     	
    	Bundle bundle = new Bundle();
    	bundle.putString("gameName", newGameName);
    	bundle.putInt("numberLives", lives);
    	bundle.putInt("playerNumber", numberofplayers);
    	Intent intent = new Intent(this, HomeScreenActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);//, bundle);
    }
    
    public void poweruptoggle(View view) {
    	powerupsen = !powerupsen;
    }
}
