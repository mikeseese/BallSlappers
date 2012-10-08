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
	public static float speed = 20;
	public float orientation = 0; //in degrees
	private Vector2 paddle_velocity;
	
	//need to get the vertexbufferobjectmanager
	
	Body paddleBody;
	Rectangle padShape;
	final FixtureDef paddlefix = PhysicsFactory.createFixtureDef(0,1.0f,0.0f);
		
	public Paddle(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager vertexBufferObjectManager,PhysicsWorld mPhysicsWorld, Scene mScene) {
		//x,y,xwidth,xheight,objectmanager		//
		padShape = new Rectangle(pX, pY, pWidth, pHeight, vertexBufferObjectManager);
		paddleBody = PhysicsFactory.createBoxBody(mPhysicsWorld, padShape, BodyType.StaticBody, paddlefix);
		paddleBody.setUserData("paddleBody");
		//dy(this.mPhysicsWorld, ballShape, BodyType.DynamicBody, ballDef);
		mScene.attachChild(padShape);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(padShape, paddleBody));
		mScene.registerUpdateHandler(mPhysicsWorld);
		x=pX;
		y=pY;
		orientation=(float) Math.atan(y/x);
		width=pWidth;
		height=pHeight;
	}
		
	public void update(Body ball) {
		paddle_velocity = ball.getPosition();
		float ballx = 32*paddle_velocity.x;
		//float bally = 32*paddle_velocity.y;
		
		if(this.x>ballx+speed+height) { //ensures that tip of
			this.x = this.x - speed;
			paddle_velocity.x = this.x/32;
			paddle_velocity.y = this.y/32;
			//Vector2 temp2 = new Vector2(this.x/32,this.y/32);
			paddleBody.setTransform(paddle_velocity,0);
		} else if(this.x<ballx-speed) {
			this.x = this.x + speed;
			paddle_velocity.x = this.x/32;
			paddle_velocity.y = this.y/32;
			//Vector2 temp2 = new Vector2(this.x/32,this.y/32);
			paddleBody.setTransform(paddle_velocity,0);
		} else {
			//this.y=bally+height/2; //puts paddle in middle
			
		}	
	}
} 