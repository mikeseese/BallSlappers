package com.android.packages.ballslappers;



/*IMPORTS*/
import java.util.HashMap;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
/*AndEngine Imports*/
import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
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
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
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
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.R.string;
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

public class MainActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IUpdateHandler, IOnMenuItemClickListener {

	/* SCREEN REFERENCE
	 * Meaning of dimensions when phone is in landscape:
	 * 
	 *  -------------------------------------------------	_________
	 *  |0,0                                        |b	|       |
	 *  |                                           |u  |       |
	 *  |                                           |t  |  CAMERA_HEIGHT
	 *  |                                           |t  |       |
	 *  |                                           |on |       |
	 *  |________________________________________max|s__|   ____|____
	 *  
	 *  |----------------- CAMERA_WIDTH ----------------|
	 *  
	 *  In reality anything at CAMERA_HEIGHT/WIDTH is off my Droid RAZR screen though
	 */
	public static final int CAMERA_WIDTH = 800;
	public static final int CAMERA_HEIGHT = 480;
	/* **************************************************************************** */ 
	 //CURRENT GAME MODES
	
	
	//Options
			public static final int NUM_SLAPPERS = 4;
	
			public static final int NUM_LIVES = 1;
			
			public static final boolean PAINBOW = true; //troll stuff
			
			public static final boolean POWERUPS = false; //powerups
		
	







	/* ***************************************************************************** */
		//Constants\\
	
	//Paddle Constants
	public static final int PADDLE_WIDTH = 100;
	public static final int PADDLE_HEIGHT = 20;
	
	//Ball Constants
	public float ballAngle = 0;
	public double ballAngleDiff = .001;
	public static final int BALL_SIZE = 15;
	public static final int BALL_RESET_DELAY = 3; // in seconds
	public static final Vector2 start_position = new Vector2(CAMERA_WIDTH/(2*PIXEL_TO_METER_RATIO_DEFAULT), 
															 CAMERA_HEIGHT/(2*PIXEL_TO_METER_RATIO_DEFAULT));
	
	//Pause Menu
	public static final int PAUSE_MENU_RESUME = 0;
	public static final int PAUSE_MENU_RESTART = 1;
	public static final int PAUSE_MENU_QUIT = 2;

	// ===========================================================
	// FIELDS / PARAMETERS
	// ===========================================================
	
		//Main
	//This is deals with physic and image world.
	protected TimerHandler timerHandler;
	private PhysicsWorld mPhysicsWorld;
	private Scene mScene;
	private Camera mCamera;
	
		//Pause Menu
	private MenuScene mPauseMenuScene;
	
	private BitmapTextureAtlas mPauseMenuFontTexture;
    private Font mPauseMenuFont;
    private Font mLivesFont;
    private Font mGameResetFont;
    private Text playerLives;
    private Text computerLives;
    private Text gameResetMessage;
    private Text countDownTimer;
    
    	//Game UI Implementations
    private int numPlayerLives = NUM_LIVES;
    private int numComputerLives = NUM_LIVES;
    protected boolean gameOver = false;
    protected boolean gameStarting = false;
    protected boolean resuming = false;
    protected boolean timerCountOn = false;
    protected String loserMessage = "";
    	
    	//Sprites / Images
    //Texture Atlases
    private String texChoice = "";
    private BitmapTextureAtlas mPaddleBitmapTextureAtlas;
    private BitmapTextureAtlas mAIBitmapTextureAtlas;
    private BitmapTextureAtlas mCollisionBitmapTextureAtlas;
    private BitmapTextureAtlas mBallBitmapTextureAtlas;
    private BitmapTextureAtlas mBgBitmapTextureAtlas;
    
    //Texture Regions
    private ITextureRegion mBgTexture;
    private TiledTextureRegion mBallTextureRegion;
    private TiledTextureRegion mPaddleTextureRegion;
    private TiledTextureRegion mAITextureRegion;
    private TiledTextureRegion mCollisionTextureRegion;
    private TiledTextureRegion mBgTextureRegion;
    
    
    	//Boundaries and parameters
	private HashMap<String, Rectangle> boundaryShapes;
	public boolean outOfBounds = false;
		
		//Ball
	static Body ballBody;
	static AnimatedSprite ball;
	
    	//Paddle
	//User Paddle and Parameters
    static AnimatedSprite playerSlapperShape;
	private Body paddleBody; 
	private float diffX;
	private boolean fingerDown;
	
	//AI Paddle and Parameters
	private double orient = 0;
	private Body[] aiBody = new Body[4];
	private Slapper[] aiSlapper = new Slapper[4];
	FixtureDef[] aiDef = new FixtureDef[4];
	
	//Misc.
	private Random randomNumGen = new Random();

	
	/* *****************************************************
	 * Creating the android scene / environment for gameplay
	 *******************************************************/

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
        final BitmapTextureAtlas mLivesTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, 
					TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final BitmapTextureAtlas mGameResetTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, 
					TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        this.mPauseMenuFont = new Font(this.getFontManager(), (ITexture) this.mPauseMenuFontTexture, 
        							   Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 48.0f, true, Color.WHITE);
        this.mLivesFont = new Font(this.getFontManager(), (ITexture) mLivesTexture, 
				   Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 20.0f, true, Color.RED);
        this.mGameResetFont = new Font(this.getFontManager(), (ITexture) mGameResetTexture, 
				   Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 30.0f, true, Color.WHITE);
              
        this.mEngine.getTextureManager().loadTexture(this.mPauseMenuFontTexture);
        this.mEngine.getTextureManager().loadTexture(mLivesTexture);
        this.mEngine.getTextureManager().loadTexture(mGameResetTexture);
        this.getFontManager().loadFont(this.mPauseMenuFont);
        this.getFontManager().loadFont(this.mLivesFont);
        this.getFontManager().loadFont(this.mGameResetFont);
        
        	/* Texture Regions */
        //Ball Textures
        if (PAINBOW){ texChoice = "pony.png"; } else {texChoice="ball.png";}
        this.mBallBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, 
        													  TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBallBitmapTextureAtlas, 
        																			this, texChoice, 0, 32, 2, 1); // 64x32
        this.mEngine.getTextureManager().loadTexture(this.mBallBitmapTextureAtlas);
        
        //Paddle Textures
        if (PAINBOW){ texChoice = "painbow.png"; } else {texChoice="normal.png";}
        this.mPaddleBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 512, 512, 
				  TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        this.mPaddleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPaddleBitmapTextureAtlas, 
										this, texChoice, 0, 0, 1, 1); // Base
        this.mEngine.getTextureManager().loadTexture(this.mPaddleBitmapTextureAtlas);
        
        //Paddle Collision Textures
        if (PAINBOW){ texChoice = "painbowhit.png"; } 
        	else {
        	texChoice="ai.png";
        	
        	}
        this.mCollisionBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 512, 512, 
				  TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        this.mCollisionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mCollisionBitmapTextureAtlas, 
										this, texChoice, 0, 0, 2, 1); // Base
        this.mEngine.getTextureManager().loadTexture(this.mCollisionBitmapTextureAtlas);
        
        
        //BG Textures
       this.mBgBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
       BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
       this.mBgTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBgBitmapTextureAtlas, this, "bg1.png", 0, 0);
       this.mEngine.getTextureManager().loadTexture(this.mBgBitmapTextureAtlas);
        
        // Text for resetting the game
        // 40 is the max size for text. Currently a magic #
		this.gameResetMessage = new Text(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, this.mGameResetFont, "Game over. " + loserMessage + "Resetting.",
				40, this.getVertexBufferObjectManager());
		this.countDownTimer = new Text(CAMERA_WIDTH/2, (int)(CAMERA_HEIGHT*.1), this.mGameResetFont, "Starting in: " + BALL_RESET_DELAY,
				"Starting in: X".length(), this.getVertexBufferObjectManager());
	}

	@Override
	/* Initialization*/
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.mScene = new Scene();
		
		//Background Creation
		if (PAINBOW) {
		Sprite bgSprite = new Sprite(0,0, CAMERA_WIDTH, CAMERA_HEIGHT, mBgTexture, this.getVertexBufferObjectManager());
		SpriteBackground background=new SpriteBackground(bgSprite);
		this.mScene.setBackground(background);
		}
		else { this.mScene.setBackground(new Background(0, 0, 0)); }
		
		
		this.mScene.setOnSceneTouchListener(this);
		
		// Initialize Pause Menu
		this.mPauseMenuScene = this.createPauseMenuScene();
		
		// Initialize Physics World - No Gravity
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		//  boundaries (walls & goals)
		this.boundaryShapes = this.createBoundaryShapes();
		this.createBoundaryBodies();
		
			/* Setting Up the Game */
		//TEST
	
		
		//*******************************************
		
		
		// Localized Player and paints
		
		
		playerSlapperShape = new AnimatedSprite(CAMERA_WIDTH/2, 455, this.mPaddleTextureRegion, this.getVertexBufferObjectManager());
		final FixtureDef playerDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		paddleBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, playerSlapperShape, BodyType.KinematicBody, playerDef);
		paddleBody.setUserData("paddleBody");
		this.mScene.attachChild(playerSlapperShape);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(playerSlapperShape, paddleBody));

		
		// initialize the paddle for the AI, and paints them
		
		for (int i = 0; i<NUM_SLAPPERS-1; i++) {
			if (i>0 && NUM_SLAPPERS==4){if (i==2){orient = 3*Math.PI/2;}else{orient = Math.PI/2;}}
			if (PAINBOW) {
				aiSlapper[i] = new Slapper(CAMERA_HEIGHT/2, 470, PADDLE_WIDTH, PADDLE_HEIGHT, this.mPaddleTextureRegion, this.getVertexBufferObjectManager(), (float) orient);
			}
			else {
				aiSlapper[i] = new Slapper(CAMERA_HEIGHT/2, 470, PADDLE_WIDTH, PADDLE_HEIGHT, this.mCollisionTextureRegion, this.getVertexBufferObjectManager(), (float) orient);
			}
			aiDef[i] = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
			aiBody[i] = PhysicsFactory.createBoxBody(this.mPhysicsWorld, aiSlapper[i], BodyType.KinematicBody, aiDef[i]);
			aiBody[i].setUserData(aiBody[i]);
			this.mScene.attachChild(aiSlapper[i]);
			mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(aiSlapper[i], aiBody[i]));
			this.mScene.registerUpdateHandler(new AIUpdater(aiBody[i],aiSlapper[i],i));
		}
		
		// initialize the ball
		final FixtureDef ballDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		ball = new AnimatedSprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, this.mBallTextureRegion, this.getVertexBufferObjectManager());
        ballBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ball, BodyType.DynamicBody, ballDef);
		ballBody.setUserData("ballBody");
		this.mScene.attachChild(ball);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ball, ballBody));
		ballReset();
		
		// set a listener to determine if the ball is out of bounds
		mPhysicsWorld.setContactListener(new BallCollisionUpdate());
		
		// paint the boundaries
		
		
		//boundary physics
		
		

		//Updates physics world, etc etc.
		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.registerUpdateHandler(this);

		showPlayerLives(this.mLivesFont, this.numPlayerLives);
		showComputerLives(this.mLivesFont, this.numComputerLives);
		
		this.gameStarting = true;
		startTimer();
		
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
	            // set up the count down timer
	            setLoserMessage("");
	            this.resuming = true;
	            startTimer();

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

    public boolean onMenuItemClicked(final MenuScene pMenuScene, final IMenuItem pMenuItem, final float pMenuItemLocalX, final float pMenuItemLocalY) {
		switch(pMenuItem.getID()) {
			case PAUSE_MENU_RESUME:
				// resume the game by just removing the menu
				this.mScene.clearChildScene();
	            this.mPauseMenuScene.reset();
	            
	            // set up the count down timer
	            setLoserMessage("");
	            this.resuming = true;
	            startTimer();
	            
	            return true;
			case PAUSE_MENU_RESTART:
				// restart the game, for now just reset the ball
				this.ballReset();
				// reset the players lives
				this.resetLives();
	            // remove the menu
	            this.mScene.clearChildScene();
	            
	            // set up the count down timer
	            setLoserMessage("");
	            this.gameStarting = true;
	            startTimer();
	            
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

	@SuppressWarnings("unused")
	protected HashMap<String, Rectangle> createBoundaryShapes() {
		HashMap<String, Rectangle> boundaries = new HashMap<String, Rectangle>();
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
			if (NUM_SLAPPERS==3) {
				float triWidth = (float) Math.sqrt(((CAMERA_WIDTH/2)*(CAMERA_WIDTH/2))+((CAMERA_HEIGHT*CAMERA_HEIGHT)));
				final Rectangle btri = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, this.getVertexBufferObjectManager());
				final Rectangle ltri = new Rectangle(400, 400, 625, 2, this.getVertexBufferObjectManager());
				final Rectangle rtri = new Rectangle(0, 0, triWidth, 2, this.getVertexBufferObjectManager());
				boundaries.put("btri", btri);
				boundaries.put("ltri", ltri);
				boundaries.put("rtri", rtri);
			}
			if (NUM_SLAPPERS==2 || NUM_SLAPPERS==4) { // 2 players
				final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
				final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
				final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
				final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
				boundaries.put("ground", ground); 
				boundaries.put("roof", roof); 
				boundaries.put("left", left); 
				boundaries.put("right", right);
			}
		
		return boundaries;
	}
	
	@SuppressWarnings("unused")
	protected void createBoundaryBodies() {
		final FixtureDef wFD = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		final FixtureDef obFD = PhysicsFactory.createFixtureDef(0, 0, 0);
		FixtureDef set;
		obFD.isSensor = true;
			if (NUM_SLAPPERS==5) {
				
			}
			
			if (NUM_SLAPPERS==3) {
				float hyp = (float) Math.sqrt(((CAMERA_WIDTH/2)*(CAMERA_WIDTH/2))+((CAMERA_HEIGHT*CAMERA_HEIGHT)));
				double ang = ((Math.PI * Math.tan(480/hyp))/180);
				Body lefttri = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("ltri"), BodyType.StaticBody, wFD);
				lefttri.setUserData("ltri");
				Body righttri = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("rtri"), BodyType.StaticBody, wFD);
				righttri.setUserData("rtri");
				Vector2 pos = new Vector2();
				pos.x = 600/PIXEL_TO_METER_RATIO_DEFAULT; pos.y = 240/PIXEL_TO_METER_RATIO_DEFAULT;
				lefttri.setTransform(pos, (float) (Math.PI*.27875));
				pos.x = 200/PIXEL_TO_METER_RATIO_DEFAULT; pos.y = 240/PIXEL_TO_METER_RATIO_DEFAULT;
				righttri.setTransform(pos, (float) (Math.PI*.721256));
					
				Body bottri = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("btri"), BodyType.StaticBody, wFD);
				bottri.setUserData("btri");	
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("rtri"), righttri));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("ltri"), lefttri));
				this.mScene.attachChild(boundaryShapes.get("rtri"));
				this.mScene.attachChild(boundaryShapes.get("ltri"));
			}
			
			if (NUM_SLAPPERS == 2 || NUM_SLAPPERS==4) { // 2 players
				if (NUM_SLAPPERS==2){ set = wFD; this.mScene.attachChild(boundaryShapes.get("left")); this.mScene.attachChild(boundaryShapes.get("right")); } else { set = obFD; }
				// create wall bodies (left and right) 
				Body leftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("left"), BodyType.StaticBody, set);
				leftBody.setUserData("leftBody");
				Body rightBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("right"), BodyType.StaticBody, set);
				rightBody.setUserData("rightBody");
				
				
				// create bodies for goals (ground and roof)
				Body groundBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("ground"), BodyType.StaticBody, obFD);
				groundBody.setUserData("groundBody");
				Body roofBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("roof"), BodyType.StaticBody, obFD);
				roofBody.setUserData("roofBody");	
			}
	}

	
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
	
	/* Functions for fun */
	
 	private void ballReset() {
    	ballBody.setTransform(start_position, 0f);
		Vector2 unit = getUnitVector();
		ballBody.setLinearVelocity(getRandomVelocity() * unit.x, getRandomVelocity() * unit.y);
		//Log.i("ballBodyVelocity", ballBody.getLinearVelocity().toString());
	}

	public void onUpdate(final float pSecondsElapsed) {
		ballBody.setTransform(ballBody.getPosition(),ballAngle);
		if (ballAngle == 360){ ballAngle = 0;}
		ballAngle += ballAngleDiff;
		
		for (int j = 0; j<NUM_SLAPPERS-1; j++) {
			if (aiSlapper[j].getHit()==true) {
				aiSlapper[j].setTextureRegion(mCollisionTextureRegion);	
			}
		}
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
		
		if(timerCountOn) {
			if(timerHandler != null) {
				setTimerMessage();
			}
			
			setResetMessage();
		}
		
		if(outOfBounds) {
			outOfBounds = false;
			
			if(gameOver) {
				this.countDownTimer.setPosition((float)(CAMERA_WIDTH/2 - (countDownTimer.getWidth()*.5)), (float)(CAMERA_HEIGHT*.1));
				setResetMessage();
			}
			
			startTimer();
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
	
	private void showPlayerLives(Font font, int numLives) {
		this.playerLives = new Text((int)(CAMERA_WIDTH*.01), (int)(CAMERA_HEIGHT-(CAMERA_HEIGHT*.1)),
				font, ("Lives: " + numLives), "Lives: X".length(), this.getVertexBufferObjectManager());
		this.mScene.attachChild(playerLives);
	}
	
	private void showComputerLives(Font font, int numLives) {
		this.computerLives = new Text((int)(CAMERA_WIDTH*.01), (int)(CAMERA_HEIGHT*.06),
				font, ("Lives: " + numLives), "Lives: X".length(), this.getVertexBufferObjectManager());
		this.mScene.attachChild(computerLives);
	}
	
	private void resetLives() {
		this.numComputerLives = NUM_LIVES;
		this.numPlayerLives = NUM_LIVES;
		playerLives.setText("Lives: " + numPlayerLives);
		computerLives.setText("Lives: " + numComputerLives);
	}
	
	/*
	 * Naive way to reset the game.
	 */
	protected void resetGame() {
		this.ballReset();
		this.resetLives();
		//this.resetPaddles(); // This needs to be a thing
	}

	private void setLoserMessage(String loserMessage) {
		this.loserMessage = loserMessage;
	}
	
	private String getLosingMessage() {
		return this.loserMessage + "Game about to start.";
	}
	
	private void startTimer() {
		if(timerCountOn) {
			Log.i("timerCountOn", "True");
			return;
		}
		else {
			Log.i("timerCountOn", "False -> Setting true");
			this.timerCountOn = true;
		}
		
		if(!gameResetMessage.hasParent())
			this.mScene.attachChild(gameResetMessage);
		if(!countDownTimer.hasParent())
			this.mScene.attachChild(countDownTimer);
		
        this.getEngine().registerUpdateHandler(timerHandler = new TimerHandler(BALL_RESET_DELAY, new ITimerCallback()
        {                      
            public void onTimePassed(final TimerHandler pTimerHandler)
            {			
                mScene.setIgnoreUpdate(false);
    			// if someone just lost then reset

    			if(resuming) {
    				if(gameResetMessage.hasParent())
    					mScene.detachChild(gameResetMessage);
    				if(countDownTimer.hasParent())
    					mScene.detachChild(countDownTimer);
    				
    				ballBody.setActive(true);
    			}
    			else if(gameOver || gameStarting) {
    				resetGame();
    				if(gameResetMessage.hasParent())
    					mScene.detachChild(gameResetMessage);
    				if(countDownTimer.hasParent())
    					mScene.detachChild(countDownTimer);
    				
    				ballBody.setActive(true);
    			}
    			else {
    				ballReset();
    			}
    			
				clearBooleans();
            }
        }));
        
        ballBody.setActive(false);
	}
	
	/* One method to set booleans to false */
	private void clearBooleans() {
		this.outOfBounds = false;
		this.timerCountOn = false;
		this.gameStarting = false;
		this.resuming = false;
	}

	/* 
	 * Note: This  will result in a null point exception if you do not call
	 * startTimer() before calling this method
	 */
	private void setTimerMessage() {
		this.countDownTimer.setText("Starting in: " + (int)(3-timerHandler.getTimerSecondsElapsed()));
		this.countDownTimer.setPosition((float)(CAMERA_WIDTH/2 - (countDownTimer.getWidth()*.5)), (float)(CAMERA_HEIGHT*.1));
		this.countDownTimer.setHorizontalAlign(HorizontalAlign.CENTER);
	}
	
	private void setResetMessage() {
		this.gameResetMessage.setText(getLosingMessage());
		this.gameResetMessage.setPosition((float)(CAMERA_WIDTH/2 - gameResetMessage.getWidth()*.5), CAMERA_HEIGHT/2);
		this.gameResetMessage.setHorizontalAlign(HorizontalAlign.CENTER);
	}
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	class BallCollisionUpdate implements ContactListener{

		public void beginContact(Contact contact) {
			Body bodyA = contact.getFixtureA().getBody();
			Body bodyB = contact.getFixtureB().getBody();
			Object userAData = bodyA.getUserData();
			Object userBData = bodyB.getUserData();
					
			//Rotation / Speed / Hit
			if(userAData.equals("ballBody") && userBData.equals("paddleBody")
					|| userAData.equals("paddleBody") && userBData.equals("ballBody")) {
				Log.i("Contact Made", "Ball contacted the paddle");
				ballBody.setLinearVelocity(ballBody.getLinearVelocity().x+1,ballBody.getLinearVelocity().y +1);
				if (ballAngleDiff!=1){
				ballAngleDiff = 1;
				}
				else
				{
					ballAngleDiff = Math.random()/10;
				}
			}
			
			for (int j = 0; j<NUM_SLAPPERS-1; j++) {
				if(userAData.equals("ballBody") && userBData.equals(aiBody[j])
						|| userAData.equals(aiBody[j]) && userBData.equals("ballBody")) {
					Log.i("Contact Made", "Ball contacted the paddle");
					aiSlapper[j].setHit(true);
					ballBody.setLinearVelocity(ballBody.getLinearVelocity().x+1,ballBody.getLinearVelocity().y +1);
					if (ballAngleDiff!=1){
					ballAngleDiff = 1;
					}
					else
					{
						ballAngleDiff = Math.random()/10;
					}
				}
			}
			
			
			//Boundary Collision
			if(userAData.equals("ballBody") && userBData.equals("groundBody")
					|| userAData.equals("groundBody") && userBData.equals("ballBody")) {
				Log.i("Contact Made", "Ball contacted the ground");
				outOfBounds = true;
				playerLives.setText("Lives: " + --numPlayerLives);
				
				if(numPlayerLives == 0) {
					gameOver = true;
					setLoserMessage("You lose. ");
				}
			}
			else if(userAData.equals("ballBody") && userBData.equals("roofBody")
					|| userAData.equals("roofBody") && userBData.equals("ballBody")) {
				Log.i("Contact Made", "Ball contacted the roof");
				outOfBounds = true;
				computerLives.setText("Lives: " + --numComputerLives);
				
				if(numComputerLives == 0) {
					gameOver = true;
					setLoserMessage("The Computer loses. ");
				}
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