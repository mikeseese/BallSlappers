package com.android.packages.ballslappers;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
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
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IUpdateHandler {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;
	public static final int PADDLE_WIDTH = 200;
	public static final int PADDLE_HEIGHT = 20;
	public static final int BALL_SIZE = 15;
	public static final int BALL_RESET_DELAY = 3;
	public static final Vector2 start_position = new Vector2(CAMERA_WIDTH/(2*PIXEL_TO_METER_RATIO_DEFAULT), CAMERA_HEIGHT/(2*PIXEL_TO_METER_RATIO_DEFAULT));

	// ===========================================================
	// Fields
	// ===========================================================

	private Scene mScene;

	private PhysicsWorld mPhysicsWorld;

	private Body ballBody;
	private Rectangle ballShape;

	private Body paddleBody;
	private float diffX;
	private boolean fingerDown;

	private Paddle paddleAI;

	private Random randomNumGen = new Random();
	public static PhysicsConnector physics_conn;
	public boolean outOfBounds = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Here we go...", Toast.LENGTH_SHORT).show();

		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
	}

	@Override   
	public void onCreateResources() {

	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);
		
		// initialize the physics world with no gravity
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		// create all shapes to be painted on the scene
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		ballShape = new Rectangle(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2, BALL_SIZE, BALL_SIZE, vertexBufferObjectManager);
		ballShape.setColor(1, 0, 0);
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle paddleShape = new Rectangle(CAMERA_WIDTH / 2, 455, PADDLE_WIDTH, PADDLE_HEIGHT, vertexBufferObjectManager);

		// create wall bodies (left and right)
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		Body leftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		leftBody.setUserData("leftBody");
		Body rightBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);
		rightBody.setUserData("rightBody");
		
		// create bodies for goals (ground and roof)
		final FixtureDef outOfBoundsFixDef = PhysicsFactory.createFixtureDef(0, 0, 0);
		outOfBoundsFixDef.isSensor = true;
		Body groundBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, outOfBoundsFixDef);
		groundBody.setUserData("groundBody");
		Body roofBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, outOfBoundsFixDef);
		roofBody.setUserData("roofBody");

		// create ball body
		final FixtureDef ballDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		ballBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ballShape, BodyType.DynamicBody, ballDef);
		ballBody.setUserData("ballBody");	
		
		// create paddle body
		final FixtureDef paddleDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		paddleBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, paddleShape, BodyType.KinematicBody, paddleDef);
		paddleBody.setUserData("paddleBody");	

		// paint the shapes we want to paint on the scene
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		this.mScene.attachChild(ballShape);
		this.mScene.attachChild(paddleShape);

		// initialize the paddle for the AI
		paddleAI = new Paddle(CAMERA_HEIGHT/2, 470, PADDLE_WIDTH, PADDLE_HEIGHT, vertexBufferObjectManager, mPhysicsWorld, mScene, 0);

		// initialize the ball with a starting random velocity
		Vector2 unit = getUnitVector();
		ballBody.setLinearVelocity(getRandomVelocity() * unit.x, getRandomVelocity() * unit.y);
		
		// set a listener to determine if the ball is out of bounds
		mPhysicsWorld.setContactListener(new BallCollisionUpdate());
		
		// connect the shapes with the bodies for the physics engine
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ballShape, ballBody));
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(paddleShape, paddleBody));

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.registerUpdateHandler(this);
		
		return this.mScene;
	}

	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(fingerDown) {
				Log.i("paddle.x", Float.toString(paddleBody.getPosition().x));
				float nextX = pSceneTouchEvent.getX() - diffX;
				if(nextX < PADDLE_WIDTH/2)
					nextX = PADDLE_WIDTH/2;
				if(nextX > CAMERA_WIDTH - PADDLE_WIDTH/2)
					nextX = CAMERA_WIDTH - PADDLE_WIDTH/2;
				Vector2 v = new Vector2(nextX/PIXEL_TO_METER_RATIO_DEFAULT, 465/PIXEL_TO_METER_RATIO_DEFAULT);
				paddleBody.setTransform(v, 0);
			}

			if(pSceneTouchEvent.isActionDown()) {
				Vector2 current = paddleBody.getWorldPoint(new Vector2(0,0));
				diffX = pSceneTouchEvent.getX() - current.x*PIXEL_TO_METER_RATIO_DEFAULT;
				fingerDown = true;
				return true;
			}
			if(pSceneTouchEvent.isActionUp()) {
				fingerDown = false;
				return true;
			}
		}
		return false;
	}

	@Override
	public void onResumeGame() {
		super.onResumeGame();
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void ballReset() {
		Vector2 unit = getUnitVector();
		ballBody.setLinearVelocity(getRandomVelocity() * unit.x, getRandomVelocity() * unit.y);
		Log.i("ballBodyVelocity", ballBody.getLinearVelocity().toString());
	}

	public void onUpdate(final float pSecondsElapsed) {
		Log.i("Ball Position", ballShape.getX() + ", " + ballShape.getY());

		paddleAI.update(ballBody);

		if(outOfBounds) {
			outOfBounds = false;

			// delays the reset of the ball by BALL_RESET_DELAY seconds
			TimerHandler timerHandler;
	        this.getEngine().registerUpdateHandler(timerHandler = new TimerHandler(BALL_RESET_DELAY, new ITimerCallback()
	        {                      
	            public void onTimePassed(final TimerHandler pTimerHandler)
	            {
	            	ballBody.setTransform(start_position, 0f);
	    			ballReset();
	            }
	        }));
		}
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public Body getBallBody() {
		return ballBody;
	}

	public int getRandomVelocity() {
		int velocity = 0;
		while(Math.abs(velocity) < 5 || Math.abs(velocity) > 10)
			velocity = randomNumGen.nextInt() % 15;

		return velocity;
	}

	private Vector2 getUnitVector() {
		Vector2 unitVector = new Vector2(randomNumGen.nextFloat(), randomNumGen.nextFloat());
		return unitVector.nor();
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===================== ======================================

	class BallCollisionUpdate implements ContactListener {

		public void beginContact(Contact contact) {
			Body bodyA = contact.getFixtureA().getBody();
			Body bodyB = contact.getFixtureB().getBody();
			Object userAData = bodyA.getUserData();
			Object userBData = bodyB.getUserData();
			if(userAData.equals("ballBody") && userBData.equals("groundBody")
					|| userAData.equals("groundBody") && userBData.equals("ballBody")) {
				Log.i("Contact Made", "Ball contacted the ground");
				outOfBounds = true;
			}
			else if(userAData.equals("ballBody") && userBData.equals("roofBody")
					|| userAData.equals("roofBody") && userBData.equals("ballBody")) {
				Log.i("Contact Made", "Ball contacted the roof");
				outOfBounds = true;
			}
		}

		public void endContact(Contact contact) {
			// TODO Auto-generated method stub

		}

		public void preSolve(Contact contact, Manifold oldManifold) {
			// TODO Auto-generated method stub

		}

		public void postSolve(Contact contact, ContactImpulse impulse) {
			// TODO Auto-generated method stub

		}

	}

}