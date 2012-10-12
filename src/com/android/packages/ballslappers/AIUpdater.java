package com.android.packages.ballslappers;

import org.andengine.engine.handler.IUpdateHandler;

import android.util.Log;

public class AIUpdater implements IUpdateHandler {
	AI ai;
	
	public AIUpdater(Slapper s) {
		this.ai = new AI(s);
	}

	public void onUpdate(float pSecondsElapsed) {
		MainActivity.AIBody.setTransform(ai.update(MainActivity.ballBody), 0);
		Log.i("AIUpdater", "Updated successfully");
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

}