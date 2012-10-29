package com.android.packages.ballslappers;

//import com.example.ballslappers.R;

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
import android.widget.Spinner;

public class SinglePlayerCreateActivity extends Activity {
	
	
	private boolean powerupsen = false;
	private String difficultySelected = "easy";
	private int numberofcpu;
	private int hundreds,tens,ones;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_create);
        
        
	     Spinner difficulty = (Spinner) findViewById(R.id.DifficultySelect);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	    		 R.array.Difficulties, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     difficulty.setAdapter(adapter);
	     
	     
	     difficulty.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	 @Override
			 public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
    		 	switch(pos){
		 			case 0:
		 				difficultySelected = "easy";
		 				break;
		 			case 1:
		 				difficultySelected = "medium";
		 				break;
		 			case 2:
		 				difficultySelected = "hard";
		 				break;
		 			default:
		 				break;
		 		}
			 } 
	    	 
	    	 @Override
	         public void onNothingSelected(AdapterView<?> arg0) {

	         }
	     });
	     
	     
	     
	     
	     
	     Spinner NUMCPU = (Spinner) findViewById(R.id.CPUSelect);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
	    		 R.array.NUMCPU, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     NUMCPU.setAdapter(adapter2);
	     
	     NUMCPU.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	 @Override
			 public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
    		 	switch(pos){
	    		 	case 0:
	    				numberofcpu=1;
	    				break;
	    			case 1:
	    				numberofcpu=2;
	    				break;
	    			case 2:
	    				numberofcpu=3;
	    				break;
	    			case 3:
	    				numberofcpu=4;
	    				break;
	    			default:
	    				break;
	    		}
    		 	
			 } 
	    	 
	    	 @Override
	         public void onNothingSelected(AdapterView<?> arg0) {

	         }
	     });
	     
	     Spinner Hundreds = (Spinner) findViewById(R.id.hundredsLives);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter10 = ArrayAdapter.createFromResource(this,
	    		 R.array.NumericDial, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     Hundreds.setAdapter(adapter10);
	     
	     
	     Hundreds.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	 @Override
	    	 public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
   		 	switch(pos){
		 			case 0:
		 				hundreds = 0;
		 				break;
		 			case 1:
		 				hundreds = 1;
		 				break;
		 			case 2:
		 				hundreds = 2;
		 				break;
		 			case 3:
		 				hundreds = 3;
		 				break;
		 			case 4:
		 				hundreds = 4;
		 				break;
		 			case 5:
		 				hundreds = 5;
		 				break;
		 			case 6:
		 				hundreds = 6;
		 				break;
		 			case 7:
		 				hundreds = 7;
		 				break;
		 			case 8:
		 				hundreds = 8;
		 				break;
		 			case 9:
		 				hundreds = 9;
		 				break;
		 			default:
		 				break;
		 		}
			 } 
	    	 @Override
	    	 public void onNothingSelected(AdapterView<?> arg0) {

	         }
	     });
	     
	     Spinner Tens = (Spinner) findViewById(R.id.tensLives);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter11 = ArrayAdapter.createFromResource(this,
	    		 R.array.NumericDial, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     Tens.setAdapter(adapter11);
	     
	     
	     Tens.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	 @Override
	    	 public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
   		 	switch(pos){
		 			case 0:
		 				tens = 0;
		 				break;
		 			case 1:
		 				tens = 1;
		 				break;
		 			case 2:
		 				tens = 2;
		 				break;
		 			case 3:
		 				tens = 3;
		 				break;
		 			case 4:
		 				tens = 4;
		 				break;
		 			case 5:
		 				tens = 5;
		 				break;
		 			case 6:
		 				tens = 6;
		 				break;
		 			case 7:
		 				tens = 7;
		 				break;
		 			case 8:
		 				tens = 8;
		 				break;
		 			case 9:
		 				tens = 9;
		 				break;
		 			default:
		 				break;
		 		}
			 } 
	    	 @Override
	    	 public void onNothingSelected(AdapterView<?> arg0) {

	         }
	     });
	     
	     Spinner Ones = (Spinner) findViewById(R.id.onesLives);
	     // Create an ArrayAdapter using the string array and a default spinner layout
	     ArrayAdapter<CharSequence> adapter12 = ArrayAdapter.createFromResource(this,
	    		 R.array.NumericDial, android.R.layout.simple_spinner_item);
	     // Specify the layout to use when the list of choices appears
	     adapter12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	     // Apply the adapter to the spinner
	     Ones.setAdapter(adapter12);
	     
	     
	     Ones.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	 @Override
	    	 public void onItemSelected(AdapterView<?> arg0, View view, int pos, long id) {
   		 	switch(pos){
		 			case 0:
		 				ones = 0;
		 				break;
		 			case 1:
		 				ones = 1;
		 				break;
		 			case 2:
		 				ones = 2;
		 				break;
		 			case 3:
		 				ones = 3;
		 				break;
		 			case 4:
		 				ones = 4;
		 				break;
		 			case 5:
		 				ones = 5;
		 				break;
		 			case 6:
		 				ones = 6;
		 				break;
		 			case 7:
		 				ones = 7;
		 				break;
		 			case 8:
		 				ones = 8;
		 				break;
		 			case 9:
		 				ones = 9;
		 				break;
		 			default:
		 				ones=1;
		 				break;
		 		}
			 } 
	    	 @Override
	    	 public void onNothingSelected(AdapterView<?> arg0) {

	         }
	     });
	     
	     
	     
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_single_player_create, menu);
        return true;
    }
    
    public void PlaySinglePlayer(View view){
    	int lives = hundreds*100+tens*10+ones;
    	if(lives<1) {
    		lives=1;
    	}
    	Bundle bundle = new Bundle();
    	bundle.putString("difficulty", difficultySelected);
    	bundle.putInt("numberLives", lives);
    	bundle.putInt("cpunumber", numberofcpu);
    	Intent intent = new Intent(this, MainActivity.class);
    	intent.putExtras(bundle);
    	startActivity(intent);//, bundle);
    }
    
    public void poweruptoggle(View view) {
    	powerupsen = !powerupsen;
    }
}
