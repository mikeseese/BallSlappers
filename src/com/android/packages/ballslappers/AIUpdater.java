package com.android.packages.ballslappers;

import org.andengine.engine.handler.IUpdateHandler;

import android.util.Log;

public class AIUpdater implements IUpdateHandler {
	Slapper slapper;
	AI ai;
	int type=0;
	
	public AIUpdater(Slapper s, int t) {
		ai = new AI(s);
		this.type = t;
	}

	public void onUpdate(float pSecondsElapsed) {
		//if (type == 0) {
			MainActivity.AIBody.setTransform(ai.update(MainActivity.ballBody, MainActivity.topAI,0), 0);
		//}
		//else if (type==1) {
			MainActivity.AIBody1.setTransform(ai.update(MainActivity.ballBody, MainActivity.leftAI,1), 0);
		//}
		//else {
			MainActivity.AIBody2.setTransform(ai.update(MainActivity.ballBody, MainActivity.rightAI,2), 0);
		//}
		//
		
		//Log.i("AIUpdater", "Updated successfully");
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

}