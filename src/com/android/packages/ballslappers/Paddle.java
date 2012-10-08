package com.android.packages.ballslappers;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Ellipse;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.hardware.SensorManager;
import android.util.Log;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;


public class Paddle{
	public float x; //position of bottom of paddle
	public float y;
	public float width;
	public float height;
	public static float speed = 25;
	public float orientation = 0; //(float) (Math.PI/2) in rad
	private Vector2 tempVelocity;
	
	//need to get the vertexbufferobjectmanager
	
	Body paddleBody;
	Rectangle padShape;
	final FixtureDef paddlefix = PhysicsFactory.createFixtureDef(0,1.0f,0.0f);
		
	public Paddle(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager vertexBufferObjectManager,PhysicsWorld mPhysicsWorld, Scene mScene, float orientation) {
		//x,y,xwidth,xheight,objectmanager	
		padShape = new Rectangle(pX, pY, pWidth, pHeight, vertexBufferObjectManager);
		paddleBody = PhysicsFactory.createBoxBody(mPhysicsWorld, padShape, BodyType.StaticBody, paddlefix);
		paddleBody.setUserData("paddleBody");
		//dy(this.mPhysicsWorld, ballShape, BodyType.DynamicBody, ballDef);
		mScene.attachChild(padShape);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(padShape, paddleBody));
		mScene.registerUpdateHandler(mPhysicsWorld);
		x=pX;
		y=pY;
		this.orientation = orientation;
		width=pWidth;
		height=pHeight;
	}
		
	public void update(Body ball) {
		tempVelocity = ball.getPosition();
		int ballx = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*tempVelocity.x);
		int bally = Math.round(PIXEL_TO_METER_RATIO_DEFAULT*tempVelocity.y);
		
		int paddleconv = (int)Math.sqrt(this.x*this.x+this.y*this.y);
		//float angle1 = (float) (orientation - Math.atan(bally/ballx));
		int ballconv = (int) Math.sqrt(ballx*ballx+bally*bally);
		
		if(paddleconv > ballconv + speed + height) {
			paddleconv -= speed;
		} else if(this.x < ballx - speed) {
			paddleconv += speed;
		} 
		
		// 800 is camera_width, note that bound might not work as you
		// want it to, these low, high parameters might not be appropriate 
		// for y orientation
		this.x = bound(this.width/2, 800 - this.width/2, (float) (paddleconv*Math.cos(orientation)));
		this.y = bound(this.height, 480 - this.height/2, (float) (paddleconv*Math.sin(orientation)));
		
		tempVelocity.x = this.x/PIXEL_TO_METER_RATIO_DEFAULT;
		tempVelocity.y = this.y/PIXEL_TO_METER_RATIO_DEFAULT;
		paddleBody.setTransform(tempVelocity,0);
	}
	
	// eventually this needs to be a common function between multiple classes
	// keeps the paddle from going into and past the wall
	public float bound(float low, float high, float number) {
		if(number < low)
			number = low;
		// 800 is the CAMERA_WIDTH from MainActivity
		if(number > high)
			number = high;
		return number;
	}
} 