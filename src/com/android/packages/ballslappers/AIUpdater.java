package com.android.packages.ballslappers;

import org.andengine.engine.handler.IUpdateHandler;

import com.badlogic.gdx.physics.box2d.Body;

import android.util.Log;

public class AIUpdater implements IUpdateHandler {
	Slapper slapper;
	AI ai;
	Body aibody;
	int type=0;
	float o = 0;
	
	public AIUpdater(Body a, Slapper s, int t) {
		this.ai = new AI(s);
		this.slapper = s;
		this.aibody = a;
		this.type = t;
		this.o = slapper.getSlapperOrientation();
	}

	public void onUpdate(float pSecondsElapsed) {
		
			aibody.setTransform(ai.update(MainActivity.ballBody, slapper,type), o);
		
		
		
		//Log.i("AIUpdater", "Updated successfully");
	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}

}