package com.android.packages.ballslappers;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Slapper extends Rectangle {
	private float x; //position of bottom of paddle
	private float y;
	private float width;
	private float height;
	private float speed = 15;
	private float orientation = 0; //(float) (Math.PI/2) in rad
	private Vector2 tempVelocity;

	public Slapper(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager vertexBufferObjectManager, float orientation) {
		super(pX, pY, pWidth, pHeight, vertexBufferObjectManager);
		this.x = pX;
		this.y = pY;
		this.orientation = orientation;
		this.width = pWidth;
		this.height = pHeight;
	}

	// this is never called?
	/*public Vector2 update(Body ball) {
		this.tempVelocity = ball.getPosition();
		int ballx = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*tempVelocity.x);
		int bally = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*tempVelocity.y);

		int paddleconv = (int) Math.sqrt((this.x*this.x) + (this.y*this.y));
		int ballconv = (int) Math.sqrt((ballx*ballx) + (bally*bally));

		if(paddleconv > ballconv + speed + height) {
			paddleconv -= speed;
		}
		else if(this.x < ballx - speed) {
			paddleconv += speed;
		}

		// 800 is camera_width, note that bound might not work as you
		// want it to, these low, high parameters might not be appropriate 
		// for y orientation
		this.x = bound(this.width/2, 800 - this.width/2, (float) (paddleconv*Math.cos(orientation)));
		this.y = bound(this.height, 480 - this.height/2, (float) (paddleconv*Math.sin(orientation)));

		tempVelocity.x = this.x/PIXEL_TO_METER_RATIO_DEFAULT;
		tempVelocity.y = this.y/PIXEL_TO_METER_RATIO_DEFAULT;
		return tempVelocity;
	}*/

	public float getSlapperX() {
		return x;
	}

	public void setSlapperX(float x) {
		this.x = x;
	}

	public float getSlapperY() {
		return y;
	}

	public void setSlapperY(float y) {
		this.y = y;
	}

	public float getSlapperWidth() {
		return width;
	}

	public float getSlapperHeight() {
		return height;
	}

	public float getSlapperOrientation() {
		return orientation;
	}

	// eventually this needs to be a common function between multiple classes
	// keeps the paddle from going into and past the wall
	public float bound(float low, float high, float number) {
		if(number < low)
			number = low;
		if(number > high)
			number = high;
		return number;
	}
	
} 