package com.android.packages.ballslappers;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class AI {
	/* Vector2 for the position the ball should move to */
	private Vector2 newAIPos = new Vector2();
	private static final float speed = 20;
	private Slapper slapper;
	
	public AI(Slapper s) {
		this.slapper = s;
	}
	
	public Slapper getSlapper() {
		return slapper;
	}
	
	public Vector2 update(Body ball, Slapper slapper2) {
		this.newAIPos = ball.getPosition();
		float ballx = PIXEL_TO_METER_RATIO_DEFAULT*newAIPos.x;
		float bally = PIXEL_TO_METER_RATIO_DEFAULT*newAIPos.y;
		float orientation = slapper.getSlapperOrientation();
		float xmove = (float)(speed*Math.cos(orientation));
		float ymove = (float)(speed*Math.sin(orientation));
		float slapperX = slapper.getSlapperX();
		float slapperY = slapper.getSlapperY();
		Log.i("ball X,Y", Float.toString(ballx) + ", " + Float.toString(bally));
		Log.i("slapper X,Y", Float.toString(slapperX) + ", " + Float.toString(slapperY));
		
		if(orientation !=0) { //if it is not horizontal then it should move based on y axis solely
			if(slapperY > bally - ymove) {
				slapperX = slapperX-xmove;
				slapperY = slapper.bound(slapperY-ymove); // if NUM_SLAPPERS == 3 then bound just returns slapperY
														  // because I'm not sure what's going on with these #s below
														  // (I think they are bounding with them)
				
				if (slapperY <= -518.475+40 && MainActivity.NUM_SLAPPERS == 3){
					slapperX= slapperX+xmove;
					slapperY = slapperY+ymove;
				}
			}
			else if(slapperY <bally+ymove) {
				slapperX = slapperX+xmove;
				slapperY = slapper.bound(slapperY+ymove);
				if (slapperY >= 223.53-80 && MainActivity.NUM_SLAPPERS==3){
					slapperX= slapperX-xmove;
					slapperY = slapperY-ymove;
				}
			}			
		}
		else if (orientation==0) {	//if it is horizontal it should move based on x axis only
			if(slapperX + slapper.getWidth()/2 < ballx - MainActivity.BALL_RADIUS) { // slapper needs to move to the right
				slapperX = slapper.bound(slapperX + xmove);
			}
			else if(slapperX - slapper.getWidth()/2 > ballx + MainActivity.BALL_RADIUS) { // slapper needs to move to the left
				slapperX = slapper.bound(slapperX - xmove);
			}	
		}	
				
		slapper.setSlapperX(slapperX);
		slapper.setSlapperY(slapperY);
		
		newAIPos.x = slapper.getSlapperX()/PIXEL_TO_METER_RATIO_DEFAULT;
		newAIPos.y = slapper.getSlapperY()/PIXEL_TO_METER_RATIO_DEFAULT;
		return newAIPos;
	}
}
