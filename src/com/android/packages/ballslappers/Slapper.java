package com.android.packages.ballslappers;

/* Slappers indicates AI paddles */

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Slapper extends Rectangle {
	private float x; //position of bottom of paddle
	private float y;
	private float width;
	private float height;
	private float orientation = 0; //(float) (Math.PI/2) in rad
	public boolean hit = false;

	public Slapper(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager vertexBufferObjectManager, float orientation) {
		super(pX, pY, pWidth, pHeight, vertexBufferObjectManager);
		this.x = pX;
		this.y = pY;
		this.orientation = orientation;
		this.width = pWidth;
		this.height = pHeight;
	}

	public float getSlapperX() {
		return x;
	}
	
	public void setSlapper(Vector2 a) {
		this.y = a.y;
		this.x = a.x;
	}
	public void setHit(boolean t){
		hit = t;
	}
	
	public boolean getHit(){
		return hit;
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

	// keeps the paddle from going into and past the wall
	public float bound(float number) {
		if (MainActivity.NUM_SLAPPERS == 4) {
			if (orientation == 0) {
				float low = (MainActivity.CAMERA_WIDTH - MainActivity.bumperSideLength*2 - MainActivity.sideLength)/2 + MainActivity.bumperSideLength + this.width/2;
				float high = low + MainActivity.sideLength - this.width;
				if(number < low)
					number = low;
				if(number > high)
					number = high;
			}
			else { // orientation == Math.PI/2
				float low = MainActivity.bumperSideLength + this.width/2;
				float high = low + MainActivity.sideLength - this.width;
				if(number < low)
					number = low;
				if(number > high)
					number = high;
			}
		}
		else if (MainActivity.NUM_SLAPPERS == 3) {
			if (orientation == 0) {
				float low = (float) ((MainActivity.CAMERA_WIDTH - 2*MainActivity.bumperLength * Math.cos(Math.PI / 3) - MainActivity.sideLength)/2 + MainActivity.bumperLength*Math.cos(Math.PI/3) + this.width/2);
				float high = low + MainActivity.sideLength - this.width;
				if(number < low)
					number = low;
				if(number > high)
					number = high;
			}
			else {
				float low = MainActivity.WALL_WIDTH;
				float high = (float) (low + MainActivity.sideLength*Math.sin(Math.PI/3) - this.width*Math.sin(Math.PI/3)/2);
				if(number < low)
					number = low;
				if(number > high)
					number = high;
			}
		}
		else { //if (MainActivity.NUM_SLAPPERS == 2) {
			if (orientation == 0) {
				float low = this.width/2;
				float high = low + MainActivity.CAMERA_WIDTH - this.width;
				if(number < low)
					number = low;
				if(number > high)
					number = high;
			}
		}
		return number;
	}
} 