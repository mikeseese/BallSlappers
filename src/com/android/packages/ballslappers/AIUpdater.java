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
		MainActivity.AIBody.setTransform(ai.update(MainActivity.ballBody, MainActivity.topAI,0), 0);
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

}