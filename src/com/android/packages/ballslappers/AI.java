
package com.android.packages.ballslappers;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class AI {
	/* Vector2 for the position the ball should move to */
	private Vector2 newAIPos = new Vector2();
	private static final float speed = 5;
	private Slapper slapper;
	
	public AI(Slapper s) {
		this.slapper = s;
	}
	
	public Slapper getSlapper() {
		return slapper;
	}
	public Vector2 update(Body ball, Slapper s) {
		this.newAIPos = ball.getPosition();
		int ballx = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*newAIPos.x);
		int bally = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*newAIPos.y);

		int paddleconv = (int) Math.sqrt((slapper.getSlapperX()*slapper.getSlapperX()) + (slapper.getSlapperY()*slapper.getSlapperY()));
		int ballconv = (int) Math.sqrt((ballx*ballx) + (bally*bally));

		if(paddleconv > ballconv + speed + slapper.getHeight()) {
			paddleconv -= speed;
		}
		//else if(slapper.getX() < ballx - speed) {
		else if (slapper.getSlapperX() < ballx - speed) {
			paddleconv += speed;
		}

		// 800 is camera_width, note that bound might not work as you
		// want it to, these low, high parameters might not be appropriate 
		// for y orientation
		slapper.setSlapperX(slapper.bound(slapper.getWidth()/2, 800 - slapper.getWidth()/2, (float) (paddleconv*Math.cos(slapper.getSlapperOrientation()))));
		slapper.setSlapperY(slapper.bound(slapper.getHeight(), 480 - slapper.getHeight()/2, (float) (paddleconv*Math.sin(slapper.getSlapperOrientation()))));

		newAIPos.x = slapper.getSlapperX()/PIXEL_TO_METER_RATIO_DEFAULT;
		newAIPos.y = slapper.getSlapperY()/PIXEL_TO_METER_RATIO_DEFAULT;
		return newAIPos;
	}
}
