package com.android.packages.ballslappers;

import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.color.Color;

import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IUpdateHandler, 
																	IOnMenuItemClickListener {
	// ===========================================================
	// Constants
	// ===========================================================

	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;
	public static final int PADDLE_WIDTH = 200;
	public static final int PADDLE_HEIGHT = 20;
	public static final int BALL_SIZE = 15;
	public static final int BALL_RESET_DELAY = 3; // in seconds
	public static final Vector2 start_position = new Vector2(CAMERA_WIDTH/(2*PIXEL_TO_METER_RATIO_DEFAULT), 
															 CAMERA_HEIGHT/(2*PIXEL_TO_METER_RATIO_DEFAULT));
	
	public static final int PAUSE_MENU_RESUME = 0;
	public static final int PAUSE_MENU_RESTART = 1;
	public static final int PAUSE_MENU_QUIT = 2;

	// ===========================================================
	// Fields
	// ===========================================================

	private Scene mScene;
	private Camera mCamera;
	private MenuScene mPauseMenuScene;
	
	private BitmapTextureAtlas mPauseMenuFontTexture;
    private Font mPauseMenuFont;

	private PhysicsWorld mPhysicsWorld;

	static Body ballBody;
	private BitmapTextureAtlas mBallBitmapTextureAtlas;
    private TiledTextureRegion mBallTextureRegion;

	private Body paddleBody;
	static Body AIBody;
	private float diffX;
	private boolean fingerDown;

	//private Paddle paddleAI;
	private Slapper slapperAI;

	private Random randomNumGen = new Random();

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
		Toast.makeText(this, "Let the battle begin...", Toast.LENGTH_SHORT).show();

		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
	}

	@Override   
	public void onCreateResources() {
		/* Load Font/Textures. */
        this.mPauseMenuFontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, 
        		 											TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        this.mPauseMenuFont = new Font(this.getFontManager(), (ITexture) this.mPauseMenuFontTexture, 
        							   Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48.0f, true, Color.WHITE);
              
        this.mEngine.getTextureManager().loadTexture(this.mPauseMenuFontTexture);
        this.getFontManager().loadFont(this.mPauseMenuFont);
        
        /* Texture/Texture regions for ball */
        this.mBallBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, 
        													  TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBallBitmapTextureAtlas, 
        																			this, "orange_ball.png", 0, 32, 2, 1); // 64x32
        this.mEngine.getTextureManager().loadTexture(this.mBallBitmapTextureAtlas);
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mScene = new Scene();
		this.mScene.setBackground(new Background(0, 0, 0));
		this.mScene.setOnSceneTouchListener(this);
		
		this.mPauseMenuScene = this.createPauseMenuScene();
		
		// initialize the physics world with no gravity
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		// create all shapes to be painted on the scene
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle paddleShape = new Rectangle(CAMERA_WIDTH / 2, 455, PADDLE_WIDTH, PADDLE_HEIGHT, vertexBufferObjectManager);
		slapperAI = new Slapper(CAMERA_HEIGHT/2, 470, PADDLE_WIDTH, PADDLE_HEIGHT, vertexBufferObjectManager, 0);

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
		final AnimatedSprite ball = new AnimatedSprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, this.mBallTextureRegion, 
													   this.getVertexBufferObjectManager());
		ball.animate(100);
        ballBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ball, BodyType.DynamicBody, ballDef);
		ballBody.setUserData("ballBody");
		
		// create paddle body
		final FixtureDef paddleDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		paddleBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, paddleShape, BodyType.KinematicBody, paddleDef);
		paddleBody.setUserData("paddleBody");

		// initialize the paddle for the AI
		final FixtureDef AIFixtureDef = PhysicsFactory.createFixtureDef(0,1.0f,0.0f);
		AIBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, slapperAI, BodyType.KinematicBody, AIFixtureDef);
		AIBody.setUserData("AIBody");
		
		// paint the shapes we want to paint on the scene
		this.mScene.attachChild(left);
		this.mScene.attachChild(right);
		this.mScene.attachChild(ball);
		this.mScene.attachChild(paddleShape);
		this.mScene.attachChild(slapperAI);

		// initialize the ball with a starting random velocity
		Vector2 unit = getUnitVector();
		ballBody.setLinearVelocity(getRandomVelocity() * unit.x, getRandomVelocity() * unit.y);
		
		// set a listener to determine if the ball is out of bounds
		mPhysicsWorld.setContactListener(new BallCollisionUpdate());

		// connect the shapes with the bodies for the physics engine
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ball, ballBody));
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(paddleShape, paddleBody));
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(slapperAI, AIBody));

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.registerUpdateHandler(this);
		this.mScene.registerUpdateHandler(new AIUpdater(slapperAI));
		
		return this.mScene;
	}

	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(fingerDown) {
				//Log.i("paddle.x", Float.toString(paddleBody.getPosition().x));
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
    public boolean onKeyDown(final int pKeyCode, final KeyEvent pEvent) {
		if(pKeyCode == KeyEvent.KEYCODE_MENU && pEvent.getAction() == KeyEvent.ACTION_DOWN) {
			if(this.mScene.hasChildScene()) {
				// remove the menu
				this.mPauseMenuScene.back();
            } else {
            	// attach the menu
            	this.mScene.setChildScene(this.mPauseMenuScene, false, true, true);
            }
            return true;
        } else {
        	return super.onKeyDown(pKeyCode, pEvent);
        }
    }
	
	//@Override
    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, 
    								 final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case PAUSE_MENU_RESUME:
				// resume the game by just removing the menu
				this.mScene.clearChildScene();
	            this.mPauseMenuScene.reset();
	            return true;
			case PAUSE_MENU_RESTART:
	            // restart the game, for now just reset the ball
				this.ballReset();
	            // remove the menu
	            this.mScene.clearChildScene();
	            this.mPauseMenuScene.reset();
	            return true;
            case PAUSE_MENU_QUIT:
                // end the current activity (MainActivity)
                this.finish();
                return true;
            default:
                return false;
            }
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

	protected MenuScene createPauseMenuScene() {
        final MenuScene tempMenuScene = new MenuScene(this.mCamera);

        final IMenuItem resumeMenuItem = new ColorMenuItemDecorator(new TextMenuItem(PAUSE_MENU_RESUME, this.mPauseMenuFont, 
																   "RESUME", this.getVertexBufferObjectManager()), 
																   Color.RED, Color.WHITE);
        resumeMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(resumeMenuItem);
        
        final IMenuItem restartMenuItem = new ColorMenuItemDecorator(new TextMenuItem(PAUSE_MENU_RESTART, this.mPauseMenuFont, 
        														   "RESTART", this.getVertexBufferObjectManager()), 
        														   Color.RED, Color.WHITE);
        restartMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(restartMenuItem);

        final IMenuItem quitMenuItem = new ColorMenuItemDecorator(new TextMenuItem(PAUSE_MENU_QUIT, this.mPauseMenuFont, 
																  "QUIT", this.getVertexBufferObjectManager()), 
																  Color.RED, Color.WHITE);
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(quitMenuItem);
        
        tempMenuScene.buildAnimations();
        tempMenuScene.setBackgroundEnabled(false);
        tempMenuScene.setOnMenuItemClickListener(this);
        
        return tempMenuScene;
}
	
	private void ballReset() {
    	ballBody.setTransform(start_position, 0f);
		Vector2 unit = getUnitVector();
		ballBody.setLinearVelocity(getRandomVelocity() * unit.x, getRandomVelocity() * unit.y);
		//Log.i("ballBodyVelocity", ballBody.getLinearVelocity().toString());
	}

	public void onUpdate(final float pSecondsElapsed) {
		if (ballStuck()) {
			/* keep the x velocity the same but alter the y velocity in the appropriate direction to
			 * make it seem like it's accurately bouncing
			 * this is just temporary because it won't be an issue when the trajectory of the ball is 
			 * dependent solely on the position it hits the paddle */
			int tempYVel;
			if (ballBody.getPosition().y > CAMERA_HEIGHT / (2 * PIXEL_TO_METER_RATIO_DEFAULT)) {
				Log.i("Ball stuck", "stuck on ground");
				tempYVel = -1;
			}
			else {
				Log.i("Ball stuck", "stuck on roof");
				tempYVel = 1;
			}
			
			ballBody.setLinearVelocity(new Vector2(ballBody.getLinearVelocity().x, tempYVel));				
		}
		
		if(outOfBounds) {
			outOfBounds = false;

			// delays the reset of the ball by BALL_RESET_DELAY seconds
			TimerHandler timerHandler;
	        this.getEngine().registerUpdateHandler(timerHandler = new TimerHandler(BALL_RESET_DELAY, new ITimerCallback()
	        {                      
	            public void onTimePassed(final TimerHandler pTimerHandler)
	            {
	    			ballReset();
	            }
	        }));
		}
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public boolean ballStuck() {
		return ballBody.getLinearVelocity().y == 0;
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