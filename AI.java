
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
	private int t = 0;
	
	public AI(Slapper s) {
		this.slapper = s;
	}
	
	public Slapper getSlapper() {
		return slapper;
	}
	
	public Vector2 update(Body ball, Slapper slapper2, int type) {
		this.t = type;
		this.newAIPos = ball.getPosition();
		int ballx = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*newAIPos.x);
		int bally = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*newAIPos.y);
		float orientation = slapper.getSlapperOrientation();
		float xmove = (float)(speed*Math.cos(orientation));
		float ymove = (float)(speed*Math.sin(orientation));
		float slapperX = slapper.getSlapperX();
		float slapperY = slapper.getSlapperY();
		float height = slapper.getHeight()*(float)Math.sin(orientation);
		//float width = slapper.getHeight()*(float)Math.cos(orientation);
		
		if(orientation !=0) { //if it is not horizontal then it should move based on y axis solely
			
			if(slapperY > bally - ymove) {
				//paddleconverted -= speed;
				slapperX = slapperX-xmove;
				slapperY = slapperY-ymove;
				if (slapperY <= -518.475+40){ //trial and error niggas suck a dick coined by james(mike)
				slapperX= slapperX+xmove;
				slapperY = slapperY+ymove;
				}
			
			}
			//else if(slapper.getX() < ballx - speed) {
			else if(slapperY <bally+ymove) {
				//paddleconverted += speed;
				slapperX = slapperX+xmove;
				slapperY = slapperY+ymove;
				if (slapperY >= 223.53-80 && MainActivity.NUM_SLAPPERS==3){ //trial and error niggas suck a dick coined by james(mike)
					slapperX= slapperX-xmove;
					slapperY = slapperY-ymove;
					}
				//slapper.setSlapperX(slapperX+xmove);
				//slapper.setSlapperY(slapperY+ymove);
			}			
		} else if (orientation==0) {	//if it is horizontal it should move based on x axis only
			if(slapperX > ballx - xmove) {
				//paddleconverted -= speed;
				slapperX = slapperX-xmove;
				//slapperY = slapperY-ymove;
			}
			//else if(slapper.getX() < ballx - speed) {
			else if(slapperX <ballx+xmove) {
				//paddleconverted += speed;
				slapperX = slapperX+xmove;
				//slapperY = slapperY+ymove;
				//slapper.setSlapperX(slapperX+xmove);
				//slapper.setSlapperY(slapperY+ymove);
			}	
		}	
		
		slapper.setSlapperX(slapperX);
		slapper.setSlapperY(slapperY);
		/*
		if (t ==0 && MainActivity.NUM_SLAPPERS==3) {
			slapper.setSlapperY(slapper.bound((-518.475f+50f), (223.53f-50f), slapperY));
			slapper.setSlapperX(slapper.bound((-176.47f+50f), (174.345f-50f), slapperX));
		}
		if (t ==1 && MainActivity.NUM_SLAPPERS==3) {
			slapper.setSlapperY(slapper.bound((-518.475f+50f), (223.53f-50f), slapperY));
			slapper.setSlapperX(slapper.bound((800f+50f), (800f+174.345f-50f), slapperX));
		}
		*/
		newAIPos.x = slapper.getSlapperX()/PIXEL_TO_METER_RATIO_DEFAULT;
		newAIPos.y = slapper.getSlapperY()/PIXEL_TO_METER_RATIO_DEFAULT;
		return newAIPos;	
		
		
		
	}
	
	private Vector2 bound(float x, float y) {
		int num = MainActivity.NUM_SLAPPERS;
		
		
		
		return new Vector2(x,y); //change before testing
	}
	
	
}
