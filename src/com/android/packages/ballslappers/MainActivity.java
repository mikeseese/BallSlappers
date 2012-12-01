package com.android.packages.ballslappers;



/*IMPORTS*/
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;
/*AndEngine Imports*/
import static org.andengine.extension.physics.box2d.util.constants.PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
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
import org.andengine.entity.scene.menu.animator.SlideMenuAnimator;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
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
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;

import android.R.string;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Debug;
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
	public static float CAMERA_WIDTH;
	public static float CAMERA_HEIGHT;
	
	public static float bumperSideLength;
	public static float bumperLength = 150; // 134 default
	public static float sideLength;
	
	public static float fingerBuffer = 100; // 100 default
	public static float SLAPPER_WALL_BUFFER = 15;
	public static float WALL_WIDTH = 3;
	
	public static Color GOAL_COLOR = new Color(25f/255f, 25f/255f, 25f/255f); // grey
	public static Color AI_SLAPPER_COLOR = new Color(150f/255f, 150f/255f, 150f/255f); // lighter grey
	
	//Options
	public static int NUM_SLAPPERS = 4;
	public static int NUM_LIVES = 1;
	public static boolean POWERUPS = false; //powerups
	public static final int START_SPEED = 10;
	public static String difficulty;
	public static float ballSpeedDifficultyIncrease;

	//Paddle Constants
	public static final int PADDLE_WIDTH = 115;
	public static final int PADDLE_HEIGHT = 20;
	
	//Ball Constants
	public float ballAngle = 0;
	public double ballAngleDiff = .001;
	public static float BALL_RADIUS;
	public static final int BALL_RESET_DELAY = 3; // in seconds
	
	// Scoring System
	public static int current_score = 0;
	public final int AI_KILL_SCORE = 100;
	
	// Pause Menu
	public static final int PAUSE_MENU_RESUME = 0;
	public static final int PAUSE_MENU_RESTART = 1;
	public static final int PAUSE_MENU_QUIT = 2;
	public static final int PAUSE_MENU_HELP = 3;
	public static final int PAUSE_MENU_SOUND = 4;
	public static final int HELP_MENU_HOWTOPLAY = 5;
	public static final int HELP_MENU_GOBACK = 6;
	public static final int SOUND_MENU_SETTINGS = 7;
	public static final int SOUND_MENU_GOBACK = 8;
	
	// Game Over Menu
	public static final int GAME_OVER_MENU_MAIN = 9;
	public static final int GAME_OVER_MENU_REPLAY = 10;

	// ===========================================================
	// FIELDS / PARAMETERS
	// ===========================================================
	
	//This deals with physic and image world.
	protected TimerHandler timerHandler;
	private PhysicsWorld mPhysicsWorld;
	private Scene mScene;
	private Camera mCamera;
	
	// Pause Menu
	protected MenuScene mPauseMenuScene, mHelpMenuScene, mSoundMenuScene;
	private BitmapTextureAtlas mPauseMenuResumeBitmapTextureAtlas;
    private ITextureRegion mPauseMenuResumeTextureRegion;
    private BitmapTextureAtlas mPauseMenuRestartBitmapTextureAtlas; 
    private ITextureRegion mPauseMenuRestartTextureRegion;
    private BitmapTextureAtlas mPauseMenuQuitBitmapTextureAtlas;
    private ITextureRegion mPauseMenuQuitTextureRegion; 
    private BitmapTextureAtlas mPauseMenuHelpBitmapTextureAtlas;
    private ITextureRegion mPauseMenuHelpTextureRegion;
    private BitmapTextureAtlas mPauseMenuSoundBitmapTextureAtlas;
    private ITextureRegion mPauseMenuSoundTextureRegion; 
    private BitmapTextureAtlas mHelpMenuHowToPlayBitmapTextureAtlas; 
    private ITextureRegion mHelpMenuHowToPlayTextureRegion; 
    private BitmapTextureAtlas mHelpMenuGoBackBitmapTextureAtlas;
    private ITextureRegion mHelpMenuGoBackTextureRegion; 
    private BitmapTextureAtlas mSoundMenuSettingsBitmapTextureAtlas;
    private ITextureRegion mSoundMenuSettingsTextureRegion;
	
    // GameOver Menu
    protected MenuScene mGameOverMenuScene, mScoreMenuScene;
    private BitmapTextureAtlas mGameOverMenuReplayBitmapTextureAtlas;
    private ITextureRegion mGameOverMenuReplayTextureRegion;
    private BitmapTextureAtlas mGameOverMenuGameOverBitmapTextureAtlas;
    private ITextureRegion mGameOverMenuGameOverTextureRegion;
    private BitmapTextureAtlas mGameOverMenuQuitBitmapTextureAtlas;
    private ITextureRegion mGameOverMenuQuitTextureRegion;
    
    
    private Font mLivesFont;
    private Font mGameResetFont;
    private Font mGameOverFont;
    private Text playerLives;
    private Text currentScoreText;
    private Text gameOverScore;
    private Text gameResetMessage;
    private Text countDownTimer;
    
    //Game UI Implementations
    private int numPlayerLives;
    protected boolean gameOver = false;
    protected boolean gameStarting = false;
    protected boolean resuming = false;
    protected boolean timerCountOn = false;
    protected String loserMessage = "";
    	
    // Texture Atlases for paddles, ball, background
    private String texChoice = "";
    private BitmapTextureAtlas mPaddleBitmapTextureAtlas;
    private BitmapTextureAtlas mAIBitmapTextureAtlas;
    private BitmapTextureAtlas mCollisionBitmapTextureAtlas;
    private BitmapTextureAtlas mBallBitmapTextureAtlas;
    private BitmapTextureAtlas mBgBitmapTextureAtlas;
    
    //Texture Regions
    private ITextureRegion mBgTexture;
    private ITextureRegion mBallTextureRegion;
    private TiledTextureRegion mPaddleTextureRegion;
    private TiledTextureRegion mAITextureRegion;
    private TiledTextureRegion mCollisionTextureRegion;
    private TiledTextureRegion mBgTextureRegion;
    
    //Boundaries and parameters
    private HashMap<String, Rectangle> boundaryShapes;
	public boolean outOfBounds = false;
	
	//Triangle Boundaries and bumper boundaries helper vector
	static Vector2 linePos = new Vector2(0,0);
	
	public static Vector2 start_position;
	static Body ballBody;
	static Sprite ball;
	
	//User Paddle and Parameters
    static Slapper playerSlapperShape;
	private Body paddleBody; 
	private float diffX;
	private boolean fingerDown;
	
	//AI Paddle and Parameters
	private double orient = 0;
	private Body[] aiBody = new Body[4];
	private Slapper[] aiSlapper = new Slapper[4];
	FixtureDef[] aiDef = new FixtureDef[4];
	
	//Misc.
	public static Random randomNumGen = new Random();

	
	/* *****************************************************
	 * Creating the android scene / environment for gameplay
	 *******************************************************/

	public EngineOptions onCreateEngineOptions() {
		Toast.makeText(this, "Let the battle begin...", Toast.LENGTH_SHORT).show();

		CAMERA_WIDTH = 800 * 1.5f;
		CAMERA_HEIGHT = 480 * 1.5f;
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), mCamera);
	}

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		NUM_SLAPPERS= bundle.getInt("cpunumber")+1;
		NUM_LIVES = bundle.getInt("numberLives");
		numPlayerLives = NUM_LIVES;
		POWERUPS = bundle.getBoolean("powerupen");
		difficulty = bundle.getString("difficulty");

		if(difficulty.equalsIgnoreCase("Easy")) {
			MainActivity.ballSpeedDifficultyIncrease = 1.01f;
		}
		else if(difficulty.equalsIgnoreCase("Medium")) {
			MainActivity.ballSpeedDifficultyIncrease = 1.05f;
		}
		else if(difficulty.equalsIgnoreCase("Hard")){
			MainActivity.ballSpeedDifficultyIncrease = 1.1f;
		}
		else {
			MainActivity.ballSpeedDifficultyIncrease = 0.0f;
			Log.i("Difficulty Not Set", "The difficulty did not match easy/medium/hard");
		}
	}
	
	@Override   
	public void onCreateResources() {
		/* Load Font/Textures. */
        final BitmapTextureAtlas mLivesTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, 
					TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final BitmapTextureAtlas mGameResetTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, 
					TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        final BitmapTextureAtlas mGameOverTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, 
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        this.mLivesFont = new Font(this.getFontManager(), (ITexture) mLivesTexture, 
				   Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 35.0f, true, Color.RED);
        this.mGameResetFont = new Font(this.getFontManager(), (ITexture) mGameResetTexture, 
				   Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 50.0f, true, Color.WHITE);
        this.mGameOverFont = new Font(this.getFontManager(), (ITexture) mGameOverTexture,
        		   Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 65.0f, true, Color.WHITE);
        
        this.mEngine.getTextureManager().loadTexture(mLivesTexture);
        this.mEngine.getTextureManager().loadTexture(mGameResetTexture);
        this.mEngine.getTextureManager().loadTexture(mGameOverTexture);
        this.getFontManager().loadFont(this.mLivesFont);
        this.getFontManager().loadFont(this.mGameResetFont);
        this.getFontManager().loadFont(this.mGameOverFont);
        
        // Pause Menu textures
        this.mPauseMenuResumeBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mPauseMenuRestartBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mPauseMenuQuitBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mPauseMenuHelpBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mPauseMenuSoundBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mHelpMenuHowToPlayBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mHelpMenuGoBackBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mSoundMenuSettingsBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mPauseMenuResumeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPauseMenuResumeBitmapTextureAtlas, this, "Resume.png", 0, 99); 
        this.mPauseMenuRestartTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPauseMenuRestartBitmapTextureAtlas, this, "Restart.png", 0, 99); 
        this.mPauseMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPauseMenuQuitBitmapTextureAtlas, this, "Quit.png", 0, 99); 
        this.mPauseMenuHelpTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPauseMenuHelpBitmapTextureAtlas, this, "Help.png", 0, 99); 
        this.mPauseMenuSoundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mPauseMenuSoundBitmapTextureAtlas, this, "Sound.png", 0, 99); 
        this.mHelpMenuHowToPlayTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mHelpMenuHowToPlayBitmapTextureAtlas, this, "HelpMenu.png", 0, 356);
        this.mHelpMenuGoBackTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mHelpMenuGoBackBitmapTextureAtlas, this, "goBack.png", 0, 99); 
        this.mSoundMenuSettingsTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mSoundMenuSettingsBitmapTextureAtlas, this, "SoundMenu.png", 0, 356); 
        
        this.mEngine.getTextureManager().loadTexture(this.mPauseMenuResumeBitmapTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(this.mPauseMenuRestartBitmapTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(this.mPauseMenuQuitBitmapTextureAtlas); 
        this.mEngine.getTextureManager().loadTexture(this.mPauseMenuHelpBitmapTextureAtlas); 
        this.mEngine.getTextureManager().loadTexture(this.mPauseMenuSoundBitmapTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(this.mHelpMenuHowToPlayBitmapTextureAtlas); 
        this.mEngine.getTextureManager().loadTexture(this.mHelpMenuGoBackBitmapTextureAtlas); 
        this.mEngine.getTextureManager().loadTexture(this.mSoundMenuSettingsBitmapTextureAtlas); 

        // Game Over Menu textures
        this.mGameOverMenuReplayBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA); 
        this.mGameOverMenuGameOverBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mGameOverMenuQuitBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        
        this.mGameOverMenuReplayTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mGameOverMenuReplayBitmapTextureAtlas, this, "replay.png", 0, 99);
        this.mGameOverMenuGameOverTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mGameOverMenuReplayBitmapTextureAtlas, this, "gameOver.png", 0, 356);
        this.mGameOverMenuQuitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mGameOverMenuQuitBitmapTextureAtlas, this, "gameOverQuit.png", 0, 99);
        
        this.mEngine.getTextureManager().loadTexture(this.mGameOverMenuReplayBitmapTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(this.mGameOverMenuGameOverBitmapTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(this.mGameOverMenuQuitBitmapTextureAtlas);

        //Ball Textures
        texChoice = "ball.png";
        this.mBallBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 44, 44, 		// 68 x 68 is the size of the image
        													  TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        
        //this.mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBallBitmapTextureAtlas, 
        	//																		this, texChoice, 0, 32, 2, 1); // 64x32
        
        this.mBallTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBallBitmapTextureAtlas, this, texChoice, 0, 0);
        MainActivity.BALL_RADIUS = mBallTextureRegion.getWidth() / 2;
        
        this.mEngine.getTextureManager().loadTexture(this.mBallBitmapTextureAtlas);
        
        //Paddle Textures (not used anymore)
        /*texChoice = "normal.png";
        this.mPaddleBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 512, 512, 
				  TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        this.mPaddleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPaddleBitmapTextureAtlas, 
										this, texChoice, 0, 0, 1, 1); // Base
        this.mEngine.getTextureManager().loadTexture(this.mPaddleBitmapTextureAtlas);*/
        
        //Paddle Collision Textures
       
        /*texChoice="ai.png";
        this.mCollisionBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 512, 512, 
				  TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        this.mCollisionTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mCollisionBitmapTextureAtlas, 
										this, texChoice, 0, 0, 2, 1); // Base
        this.mEngine.getTextureManager().loadTexture(this.mCollisionBitmapTextureAtlas);*/
        
        
        //BG Textures
        this.mBgBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(),1024, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        this.mBgTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBgBitmapTextureAtlas, this, "bg1.png", 0, 0);
        this.mEngine.getTextureManager().loadTexture(this.mBgBitmapTextureAtlas);
        
        // Text for resetting the game
        // 40 is the max size for text. Currently a magic #
		this.gameResetMessage = new Text(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, this.mGameResetFont, loserMessage + "Resetting.",
				40, this.getVertexBufferObjectManager());
		this.countDownTimer = new Text(CAMERA_WIDTH/2, (int)(CAMERA_HEIGHT*.1), this.mGameResetFont, "Starting in: " + BALL_RESET_DELAY,
				"Starting in: X".length(), this.getVertexBufferObjectManager());
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		
		this.mScene = new Scene();
		
		this.mScene.setBackground(new Background(0, 0, 0));
		
		this.mScene.setOnSceneTouchListener(this);
		
		this.mPauseMenuScene = this.createPauseMenuScene();
		this.mHelpMenuScene = this.createHelpMenuScene(); 
		this.mSoundMenuScene = this.createSoundMenuScene();
		
		this.mGameOverMenuScene = this.createGameOverMenuScene();
			
		// Initialize Physics World - No Gravity
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);
		
		this.boundaryShapes = this.createBoundaryShapes();
		this.createBoundaryBodies();
		
			/* Setting Up the Game */
		
		this.numPlayerLives = NUM_LIVES;
		
		showInfo(this.mLivesFont, NUM_LIVES);
		
		// Localized Player and paints		
		if (NUM_SLAPPERS == 4) {
			playerSlapperShape = new Slapper(CAMERA_WIDTH/2 - PADDLE_WIDTH/2, (sideLength + bumperSideLength * 2 - SLAPPER_WALL_BUFFER - PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, this.getVertexBufferObjectManager(),(float) orient);
		}
		else if (NUM_SLAPPERS == 3) {
			playerSlapperShape = new Slapper(CAMERA_WIDTH/2 - PADDLE_WIDTH/2, (float) (sideLength*Math.sin(Math.PI/3) + bumperLength*Math.sin(Math.PI/3) - SLAPPER_WALL_BUFFER - PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, this.getVertexBufferObjectManager(),(float) orient);
		}
		else { // NUM_SLAPPERS == 2
			playerSlapperShape = new Slapper(CAMERA_WIDTH/2 - PADDLE_WIDTH/2, (CAMERA_HEIGHT - fingerBuffer - SLAPPER_WALL_BUFFER - PADDLE_HEIGHT/2), PADDLE_WIDTH, PADDLE_HEIGHT, this.getVertexBufferObjectManager(),(float) orient);
		}
		final FixtureDef playerDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		paddleBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, playerSlapperShape, BodyType.KinematicBody, playerDef);
		paddleBody.setUserData("paddleBody");
		this.mScene.attachChild(playerSlapperShape);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(playerSlapperShape, paddleBody));

		// initialize the paddle for the AI, and paints them
		Vector2 temp = new Vector2(0,0);
		for (int i = 0; i<NUM_SLAPPERS-1; i++) {
			if(NUM_SLAPPERS==4){
				if (i>0)	{
					if (i==2)	{ 
						orient = Math.PI/2;
					}
					else{
						orient = Math.PI/2;
					}
				}
			}
				
			if(NUM_SLAPPERS==3) {
				if (i>=0)	{
					if (i==1)	{ 
						orient = Math.PI/3;
					}
					else{
						orient = 2*Math.PI/3;
					}
				}
			}
			
			aiSlapper[i] = new Slapper(CAMERA_WIDTH/2, SLAPPER_WALL_BUFFER, PADDLE_WIDTH, PADDLE_HEIGHT, this.getVertexBufferObjectManager(), (float) orient);	
			aiSlapper[i].setColor(AI_SLAPPER_COLOR);
			aiDef[i] = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
			aiBody[i] = PhysicsFactory.createBoxBody(this.mPhysicsWorld, aiSlapper[i], BodyType.KinematicBody, aiDef[i]);
			aiBody[i].setUserData(aiBody[i]);
			this.mScene.attachChild(aiSlapper[i]);
			if (i==1 && NUM_SLAPPERS==4){ // left ai slapper
				temp.set((float) ((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + SLAPPER_WALL_BUFFER)/PIXEL_TO_METER_RATIO_DEFAULT, (float)((CAMERA_HEIGHT/2 - PADDLE_WIDTH/2)/PIXEL_TO_METER_RATIO_DEFAULT));
				aiBody[i].setTransform(temp, (float) (Math.PI/2));
				temp.mul(PIXEL_TO_METER_RATIO_DEFAULT);
				aiSlapper[i].setSlapper(temp);
			}
			else if (i==2 && NUM_SLAPPERS==4) { // right ai slapper
				temp.set((float) ((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + sideLength + 2*bumperSideLength - SLAPPER_WALL_BUFFER)/PIXEL_TO_METER_RATIO_DEFAULT, (float)((CAMERA_HEIGHT/2 - PADDLE_WIDTH/2)/PIXEL_TO_METER_RATIO_DEFAULT));
				aiBody[i].setTransform(temp, (float) (Math.PI/2));
				temp.mul(PIXEL_TO_METER_RATIO_DEFAULT);
				aiSlapper[i].setSlapper(temp);
			}
			else if (i==0 && NUM_SLAPPERS==3){ // left ai slapper
				temp.set((float) ((CAMERA_WIDTH - 2*bumperLength * Math.cos(Math.PI / 3) - sideLength)/2 + 0.5*sideLength*Math.cos(Math.PI/3) + aiSlapper[i].getSlapperWidth()*Math.cos(Math.PI/3)/2 + WALL_WIDTH*4 - SLAPPER_WALL_BUFFER*Math.cos(Math.PI/3))/PIXEL_TO_METER_RATIO_DEFAULT, (float) (0.5*sideLength*Math.sin(Math.PI/3) - aiSlapper[i].getSlapperWidth()*Math.sin(Math.PI/3)/2 + SLAPPER_WALL_BUFFER*Math.sin(Math.PI/3))/PIXEL_TO_METER_RATIO_DEFAULT);
				aiBody[i].setTransform(temp, (float) ((Math.PI*2)/3));
				temp.mul(PIXEL_TO_METER_RATIO_DEFAULT);
				aiSlapper[i].setSlapper(temp);
			}
			else if (i==1 && NUM_SLAPPERS==3) { // right ai slapper
				temp.set((float) ((CAMERA_WIDTH - 2*bumperLength * Math.cos(Math.PI / 3) - sideLength)/2 + 1.5*sideLength*Math.cos(Math.PI/3) + bumperLength - aiSlapper[i].getSlapperWidth()*Math.cos(Math.PI/3)/2 - WALL_WIDTH*5 + SLAPPER_WALL_BUFFER*Math.cos(Math.PI/3))/PIXEL_TO_METER_RATIO_DEFAULT, (float) (0.5*sideLength*Math.sin(Math.PI/3) - aiSlapper[i].getSlapperWidth()*Math.sin(Math.PI/3)/2 + SLAPPER_WALL_BUFFER)/PIXEL_TO_METER_RATIO_DEFAULT);
				aiBody[i].setTransform(temp, (float) (Math.PI/3));
				temp.mul(PIXEL_TO_METER_RATIO_DEFAULT);
				aiSlapper[i].setSlapper(temp);
			}
			mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(aiSlapper[i], aiBody[i]));
			this.mScene.registerUpdateHandler(new AIUpdater(aiBody[i],aiSlapper[i]));
		}
		
		// initialize the ball
		switch (NUM_SLAPPERS) {
			case 3:
				start_position = new Vector2((CAMERA_WIDTH/2)/PIXEL_TO_METER_RATIO_DEFAULT, 
	 							(float) (0.6*(sideLength + bumperLength)*Math.sin(Math.PI/3))/PIXEL_TO_METER_RATIO_DEFAULT);
				break;
			default:	
				start_position = new Vector2((CAMERA_WIDTH/2)/PIXEL_TO_METER_RATIO_DEFAULT, 
				 					(CAMERA_HEIGHT/2 - fingerBuffer/2)/PIXEL_TO_METER_RATIO_DEFAULT);
				break;
		}
		final FixtureDef ballDef = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
        ball = new Sprite(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, this.mBallTextureRegion, this.getVertexBufferObjectManager());
		ballBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ball, BodyType.DynamicBody, ballDef);
		ballBody.setUserData("ballBody");
		this.mScene.attachChild(ball);
		mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ball, ballBody));
		ballReset();
		
		// set a listener to determine if the ball is out of bounds
		mPhysicsWorld.setContactListener(new BallCollisionUpdate());

		this.mScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mScene.registerUpdateHandler(this);
				
		this.gameStarting = true;
		startTimer();
		
		return this.mScene;
	}

	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(fingerDown) {
				float nextX = pSceneTouchEvent.getX() - diffX;
				nextX = playerSlapperShape.bound(nextX);
				Vector2 v;
				if (NUM_SLAPPERS == 2) {
					v = new Vector2(nextX/PIXEL_TO_METER_RATIO_DEFAULT, (CAMERA_HEIGHT - fingerBuffer - SLAPPER_WALL_BUFFER)/PIXEL_TO_METER_RATIO_DEFAULT);
				}
				else if (NUM_SLAPPERS == 3) {
					v = new Vector2(nextX/PIXEL_TO_METER_RATIO_DEFAULT, (float) ((sideLength*Math.sin(Math.PI/3) + bumperLength*Math.sin(Math.PI/3) - SLAPPER_WALL_BUFFER)/PIXEL_TO_METER_RATIO_DEFAULT));
				}
				else { // NUM_SLAPPERS == 4
					v = new Vector2(nextX/PIXEL_TO_METER_RATIO_DEFAULT, (sideLength + bumperSideLength * 2 - SLAPPER_WALL_BUFFER)/PIXEL_TO_METER_RATIO_DEFAULT);
				}
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
	            this.resuming = true;
	            startTimer();
	            
	            return true;
			case PAUSE_MENU_RESTART:
				// restart the game, for now just reset the ball
				this.resetGame();
	            // remove the menu
	            this.mScene.clearChildScene();
	            
	            // set up the count down timer
	            this.gameStarting = true;
	            startTimer();
	            
	            this.mPauseMenuScene.reset();
	            return true;
            case PAUSE_MENU_QUIT:
                // end the current activity (MainActivity)
                this.finish();
                return true;
            case PAUSE_MENU_HELP:
            	//bring up the help menu
            	this.mScene.setChildScene(this.mHelpMenuScene, false, true, true);
            	return true;
            case PAUSE_MENU_SOUND:
            	//bring up sound menu (music level/on/off, FX level/on/off, master volume)
            	//this.mScene.setChildScene(this.mSoundMenuScene, false, true, true);
            	if(HomeScreenActivity.SOUND_ENABLED)
            		HomeScreenActivity.mediaPlayer.stop();
            	else
            	{
            		try
        			{
        				HomeScreenActivity.mediaPlayer.prepare();
        			} catch (IllegalStateException e1)
        			{
        				e1.printStackTrace();
        			} catch (IOException e1)
        			{
        				e1.printStackTrace();
        			}
            		HomeScreenActivity.mediaPlayer.start();
            	}
            	
            	HomeScreenActivity.SOUND_ENABLED = !HomeScreenActivity.SOUND_ENABLED;
            	SharedPreferences.Editor e = HomeScreenActivity.settings.edit();
            	e.putBoolean("sound_enabled", HomeScreenActivity.SOUND_ENABLED);
            	e.commit();
            	
            	return true;
            case HELP_MENU_HOWTOPLAY:
            	//this test just resumes
            	this.mScene.clearChildScene();
	            this.mPauseMenuScene.reset();
            	return true;
            case HELP_MENU_GOBACK:
            	this.mScene.setChildScene(this.mPauseMenuScene, false, true, true);
            	return true;
            case SOUND_MENU_GOBACK:
            	this.mScene.setChildScene(this.mPauseMenuScene, false, true, true);
            	return true;
            case GAME_OVER_MENU_REPLAY:
            	this.mScene.clearChildScene();
            	this.mGameOverMenuScene.detachChild(this.gameOverScore);
            	this.mGameOverMenuScene.reset();
            	resetGame();            	
            	startTimer();
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

	protected HashMap<String, Rectangle> createBoundaryShapes() {
		HashMap<String, Rectangle> boundaries = new HashMap<String, Rectangle>();
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		
		switch (NUM_SLAPPERS) {
			case 4: {
				bumperSideLength = (float) (bumperLength / Math.sqrt(2));
				sideLength = CAMERA_HEIGHT - fingerBuffer - 2 * bumperSideLength - 2 * WALL_WIDTH;

				final Rectangle ground = new Rectangle((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + bumperSideLength, sideLength + bumperSideLength * 2 - WALL_WIDTH, sideLength, WALL_WIDTH, vertexBufferObjectManager);
				final Rectangle roof = new Rectangle((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + bumperSideLength, 0, sideLength, WALL_WIDTH, vertexBufferObjectManager);
				final Rectangle left = new Rectangle((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2, bumperSideLength, WALL_WIDTH, sideLength, vertexBufferObjectManager);
				final Rectangle right = new Rectangle((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + sideLength + 2*bumperSideLength, bumperSideLength, WALL_WIDTH, sideLength, vertexBufferObjectManager);
				final Rectangle bottomLeftBumper = new Rectangle(0, 0, bumperLength, WALL_WIDTH, vertexBufferObjectManager);
				final Rectangle bottomRightBumper = new Rectangle(0, 0, bumperLength, WALL_WIDTH, vertexBufferObjectManager);
				final Rectangle topLeftBumper = new Rectangle(0, 0, bumperLength, WALL_WIDTH, vertexBufferObjectManager);
				final Rectangle topRightBumper = new Rectangle(0, 0, bumperLength, WALL_WIDTH, vertexBufferObjectManager);
					
				ground.setColor(GOAL_COLOR);
				roof.setColor(GOAL_COLOR);
				left.setColor(GOAL_COLOR);
				right.setColor(GOAL_COLOR);
				
				boundaries.put("ground", ground); 
				boundaries.put("roof", roof); 
				boundaries.put("left", left); 
				boundaries.put("right", right);	
				boundaries.put("bottomLeftBumper", bottomLeftBumper);
				boundaries.put("bottomRightBumper", bottomRightBumper);
				boundaries.put("topLeftBumper", topLeftBumper);
				boundaries.put("topRightBumper", topRightBumper);

				break;
			}
			case 3: {
				sideLength = (float) ((CAMERA_HEIGHT - fingerBuffer - bumperLength*Math.sin(Math.PI / 3) - 2 * WALL_WIDTH) / Math.sin(Math.PI / 3));
				
				final Rectangle btri = new Rectangle((float) (CAMERA_WIDTH - 2*bumperLength * Math.sin(Math.PI / 3) - sideLength)/2 + (float) (bumperLength * Math.sin(Math.PI / 3)) - WALL_WIDTH, (float) (sideLength * Math.sin(Math.PI / 3) + bumperLength * Math.sin(Math.PI /3) + WALL_WIDTH), sideLength, WALL_WIDTH, this.getVertexBufferObjectManager());
				final Rectangle ltri = new Rectangle(0, 0, sideLength, WALL_WIDTH, this.getVertexBufferObjectManager());
				final Rectangle rtri = new Rectangle(0, 0, sideLength, WALL_WIDTH, this.getVertexBufferObjectManager());
				final Rectangle lbump = new Rectangle(0, 0, bumperLength, WALL_WIDTH, this.getVertexBufferObjectManager());
				final Rectangle rbump = new Rectangle(0, 0, bumperLength, WALL_WIDTH, this.getVertexBufferObjectManager());
				final Rectangle tbump = new Rectangle((CAMERA_WIDTH - bumperLength)/2 - WALL_WIDTH, 0, bumperLength, WALL_WIDTH, this.getVertexBufferObjectManager());
				boundaries.put("lbump", lbump);
				boundaries.put("rbump", rbump);
				boundaries.put("tbump", tbump);
				boundaries.put("btri", btri);
				boundaries.put("ltri", ltri);
				boundaries.put("rtri", rtri);

				btri.setColor(GOAL_COLOR);
				ltri.setColor(GOAL_COLOR);
				rtri.setColor(GOAL_COLOR);
				
				break;
			}
			default: { // 2 players 
				final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - WALL_WIDTH - fingerBuffer, CAMERA_WIDTH, WALL_WIDTH, vertexBufferObjectManager);
				final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, WALL_WIDTH, vertexBufferObjectManager);
				final Rectangle left = new Rectangle(0, 0, WALL_WIDTH, CAMERA_HEIGHT - fingerBuffer, vertexBufferObjectManager);
				final Rectangle right = new Rectangle(CAMERA_WIDTH - WALL_WIDTH, 0, WALL_WIDTH, CAMERA_HEIGHT - fingerBuffer, vertexBufferObjectManager);
				boundaries.put("ground", ground); 
				boundaries.put("roof", roof); 
				boundaries.put("left", left); 
				boundaries.put("right", right);
				
				ground.setColor(GOAL_COLOR);
				roof.setColor(GOAL_COLOR);
				
				break;
			}
		}
		
		return boundaries;
	}
	
	protected void createBoundaryBodies() {
		final FixtureDef wallFD = PhysicsFactory.createFixtureDef(0, 1.0f, 0.0f);
		final FixtureDef outOfBoundsFD = PhysicsFactory.createFixtureDef(0, 0, 0);
		outOfBoundsFD.isSensor = true;
		switch (NUM_SLAPPERS) {
			case 4: {				
				// create bodies for goals
				Body leftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("left"), BodyType.StaticBody, outOfBoundsFD);
				leftBody.setUserData("leftBody");
				Body rightBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("right"), BodyType.StaticBody, outOfBoundsFD);
				rightBody.setUserData("rightBody");
				
				Body groundBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("ground"), BodyType.StaticBody, outOfBoundsFD);
				groundBody.setUserData("groundBody");
				Body roofBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("roof"), BodyType.StaticBody, outOfBoundsFD);
				roofBody.setUserData("roofBody");
				
				Body bottomLeftBumperBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("bottomLeftBumper"), BodyType.StaticBody, wallFD);
				bottomLeftBumperBody.setUserData("bottomLeftBumperBody");
				Body bottomRightBumperBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("bottomRightBumper"), BodyType.StaticBody, wallFD);
				bottomRightBumperBody.setUserData("bottomRightBumperBody");
				Body topLeftBumperBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("topLeftBumper"), BodyType.StaticBody, wallFD);
				topLeftBumperBody.setUserData("topLeftBumperBody");
				Body topRightBumperBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("topRightBumper"), BodyType.StaticBody, wallFD);
				topRightBumperBody.setUserData("topRightBumperBody");
								
				linePos.set(((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + bumperSideLength/2)/PIXEL_TO_METER_RATIO_DEFAULT, (bumperSideLength + sideLength + bumperSideLength/2)/PIXEL_TO_METER_RATIO_DEFAULT);
				bottomLeftBumperBody.setTransform(linePos, (float) (Math.PI/4));
				
				linePos.set(((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + bumperSideLength*1.5f + sideLength)/PIXEL_TO_METER_RATIO_DEFAULT, (bumperSideLength + sideLength + bumperSideLength/2)/PIXEL_TO_METER_RATIO_DEFAULT);
				bottomRightBumperBody.setTransform(linePos, (float) ((Math.PI*3)/4));
				
				linePos.set(((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + bumperSideLength/2)/PIXEL_TO_METER_RATIO_DEFAULT, (bumperSideLength/2)/PIXEL_TO_METER_RATIO_DEFAULT);
				topLeftBumperBody.setTransform(linePos, (float) ((Math.PI*3)/4));
				
				linePos.set(((CAMERA_WIDTH - bumperSideLength*2 - sideLength)/2 + bumperSideLength*1.5f + sideLength)/PIXEL_TO_METER_RATIO_DEFAULT, (bumperSideLength/2)/PIXEL_TO_METER_RATIO_DEFAULT);
				topRightBumperBody.setTransform(linePos, (float) (Math.PI/4));
				
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("bottomLeftBumper"), bottomLeftBumperBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("bottomRightBumper"), bottomRightBumperBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("topLeftBumper"), topLeftBumperBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("topRightBumper"), topRightBumperBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("left"), leftBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("right"), rightBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("roof"), roofBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("ground"), groundBody));
				this.mScene.attachChild(boundaryShapes.get("bottomLeftBumper")); 
				this.mScene.attachChild(boundaryShapes.get("bottomRightBumper"));
				this.mScene.attachChild(boundaryShapes.get("topLeftBumper"));
				this.mScene.attachChild(boundaryShapes.get("topRightBumper"));
				
				this.mScene.attachChild(boundaryShapes.get("roof")); 
				this.mScene.attachChild(boundaryShapes.get("ground"));
				this.mScene.attachChild(boundaryShapes.get("left"));
				this.mScene.attachChild(boundaryShapes.get("right"));
				
				break;
			}
			case 3: {
				Body lefttri = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("ltri"), BodyType.StaticBody, outOfBoundsFD);
				lefttri.setUserData("ltri");
				
				Body righttri = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("rtri"), BodyType.StaticBody, outOfBoundsFD);
				righttri.setUserData("rtri");
				
				Body lbump = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("lbump"), BodyType.StaticBody, wallFD);
				lbump.setUserData("lbump");
				
				Body rbump = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("rbump"), BodyType.StaticBody, wallFD);
				rbump.setUserData("rbump");
				
				Body tbump = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("tbump"), BodyType.StaticBody, wallFD);
				tbump.setUserData("tbump");
				
				Body bottri = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("btri"), BodyType.StaticBody, outOfBoundsFD);
				bottri.setUserData("btri");	
					
				linePos.set((float) ((CAMERA_WIDTH - 2*bumperLength * Math.cos(Math.PI / 3) - sideLength)/2 + 0.5*bumperLength*Math.cos(Math.PI/3) - WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT, (float) (sideLength*Math.sin(Math.PI / 3) + 0.5 * bumperLength*Math.sin(Math.PI/3) + WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT);
				lbump.setTransform(linePos, (float) (Math.PI/3));
				
				linePos.set((float) ((CAMERA_WIDTH - 2*bumperLength * Math.cos(Math.PI / 3) - sideLength)/2 + 0.5*sideLength*Math.cos(Math.PI/3) - WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT, (float) (0.5*sideLength*Math.sin(2*Math.PI / 3) + WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT);
				lefttri.setTransform(linePos, (float) ((Math.PI*2)/3));
				
				linePos.set((float) ((CAMERA_WIDTH - 2*bumperLength * Math.cos(Math.PI / 3) - sideLength)/2 + sideLength*Math.cos(Math.PI/3) + bumperLength + 0.5*sideLength*Math.cos(Math.PI/3) - WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT, (float) (0.5*sideLength*Math.sin(2*Math.PI / 3) + WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT);
				righttri.setTransform(linePos, (float) (Math.PI/3));
				
				linePos.set((float) ((float) (CAMERA_WIDTH - 2*bumperLength * Math.cos(Math.PI / 3) - sideLength)/2 + bumperLength*Math.cos(Math.PI/3) + sideLength + 0.5*bumperLength*Math.cos(Math.PI/3) - WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT, (float) (sideLength*Math.sin(Math.PI / 3) + 0.5 * bumperLength*Math.sin(Math.PI/3) + WALL_WIDTH)/PIXEL_TO_METER_RATIO_DEFAULT);
				rbump.setTransform(linePos, (float) ((Math.PI*2)/3));
				
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("rtri"), righttri));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("ltri"), lefttri));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("lbump"), lbump));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("btri"), bottri));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("rbump"), rbump));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("tbump"), tbump));
				this.mScene.attachChild(boundaryShapes.get("lbump"));
				this.mScene.attachChild(boundaryShapes.get("rbump"));
				this.mScene.attachChild(boundaryShapes.get("tbump"));
				this.mScene.attachChild(boundaryShapes.get("btri"));
				this.mScene.attachChild(boundaryShapes.get("rtri"));
				this.mScene.attachChild(boundaryShapes.get("ltri"));
								
				break;
			}
			default: { // 2 players		
				// create wall bodies (left and right) 
				Body leftBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("left"), BodyType.StaticBody, wallFD);
				leftBody.setUserData("leftBody");
				Body rightBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("right"), BodyType.StaticBody, wallFD);
				rightBody.setUserData("rightBody");
				
				// create bodies for goals (ground and roof)
				Body groundBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("ground"), BodyType.StaticBody, outOfBoundsFD);
				groundBody.setUserData("groundBody");
				Body roofBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, boundaryShapes.get("roof"), BodyType.StaticBody, outOfBoundsFD);
				roofBody.setUserData("roofBody");
				
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("left"), leftBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("right"), rightBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("roof"), roofBody));
				mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(boundaryShapes.get("ground"), groundBody));
				this.mScene.attachChild(boundaryShapes.get("left")); 
				this.mScene.attachChild(boundaryShapes.get("right"));
				
				this.mScene.attachChild(boundaryShapes.get("ground"));
				this.mScene.attachChild(boundaryShapes.get("roof"));
								
				break;
			}
		}
	}

	protected MenuScene createPauseMenuScene() {
		final MenuScene tempMenuScene = new MenuScene(this.mCamera);
	      
        final SpriteMenuItem resumeMenuItem = new SpriteMenuItem(PAUSE_MENU_RESUME, this.mPauseMenuResumeTextureRegion, this.getVertexBufferObjectManager());
        resumeMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(resumeMenuItem);
             
        final SpriteMenuItem restartMenuItem = new SpriteMenuItem(PAUSE_MENU_RESTART, this.mPauseMenuRestartTextureRegion, this.getVertexBufferObjectManager());
        restartMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(restartMenuItem);
       
        final SpriteMenuItem quitMenuItem = new SpriteMenuItem(PAUSE_MENU_QUIT, this.mPauseMenuQuitTextureRegion, this.getVertexBufferObjectManager());
        quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(quitMenuItem);
               
        final SpriteMenuItem helpMenuItem = new SpriteMenuItem(PAUSE_MENU_HELP, this.mPauseMenuHelpTextureRegion, this.getVertexBufferObjectManager());
        helpMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(helpMenuItem);
           
        final SpriteMenuItem soundMenuItem = new SpriteMenuItem(PAUSE_MENU_SOUND, this.mPauseMenuSoundTextureRegion, this.getVertexBufferObjectManager());
        soundMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        tempMenuScene.addMenuItem(soundMenuItem);
        
        tempMenuScene.buildAnimations();
        tempMenuScene.setBackgroundEnabled(false);
        tempMenuScene.setOnMenuItemClickListener(this);
        
        return tempMenuScene;
}
	
protected MenuScene createHelpMenuScene() {
    final MenuScene tempMenuScene = new MenuScene(this.mCamera);
    
    final SpriteMenuItem howToPlayMenuItem = new SpriteMenuItem(HELP_MENU_HOWTOPLAY, this.mHelpMenuHowToPlayTextureRegion, this.getVertexBufferObjectManager());
    final SpriteMenuItem goBackMenuItem = new SpriteMenuItem(HELP_MENU_GOBACK, this.mHelpMenuGoBackTextureRegion, this.getVertexBufferObjectManager());

    howToPlayMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    goBackMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    tempMenuScene.addMenuItem(howToPlayMenuItem);
    tempMenuScene.addMenuItem(goBackMenuItem);
    tempMenuScene.setMenuAnimator(new SlideMenuAnimator());
              
    tempMenuScene.buildAnimations();
    tempMenuScene.setBackgroundEnabled(false);
    tempMenuScene.setOnMenuItemClickListener(this);
    
    return tempMenuScene;
}	

protected MenuScene createGameOverMenuScene() {
    final MenuScene tempMenuScene = new MenuScene(this.mCamera);
    
    final SpriteMenuItem gameOverMenuItem = new SpriteMenuItem(GAME_OVER_MENU_MAIN, this.mGameOverMenuGameOverTextureRegion, this.getVertexBufferObjectManager());

    gameOverMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    tempMenuScene.addMenuItem(gameOverMenuItem);
    //tempMenuScene.setMenuAnimator(new SlideMenuAnimator());
    
    final SpriteMenuItem replayMenuItem = new SpriteMenuItem(GAME_OVER_MENU_REPLAY, this.mGameOverMenuReplayTextureRegion, this.getVertexBufferObjectManager());
    replayMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    tempMenuScene.addMenuItem(replayMenuItem);
   
    final SpriteMenuItem quitMenuItem = new SpriteMenuItem(PAUSE_MENU_QUIT, this.mGameOverMenuQuitTextureRegion, this.getVertexBufferObjectManager());
    quitMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    tempMenuScene.addMenuItem(quitMenuItem);
                  
    tempMenuScene.buildAnimations();
    tempMenuScene.setBackgroundEnabled(false);
    tempMenuScene.setOnMenuItemClickListener(this);
    
    return tempMenuScene;
}	

protected MenuScene createSoundMenuScene() {
    final MenuScene tempMenuScene = new MenuScene(this.mCamera);
    
    final SpriteMenuItem settingsMenuItem = new SpriteMenuItem(SOUND_MENU_SETTINGS, this.mSoundMenuSettingsTextureRegion, this.getVertexBufferObjectManager());
    final SpriteMenuItem goBackMenuItem = new SpriteMenuItem(HELP_MENU_GOBACK, this.mHelpMenuGoBackTextureRegion, this.getVertexBufferObjectManager());

    settingsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    goBackMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    tempMenuScene.addMenuItem(settingsMenuItem);
    tempMenuScene.addMenuItem(goBackMenuItem);
    tempMenuScene.setMenuAnimator(new SlideMenuAnimator());
          
    tempMenuScene.buildAnimations();
    tempMenuScene.setBackgroundEnabled(false);
    tempMenuScene.setOnMenuItemClickListener(this);
    
    return tempMenuScene;
}
	
	/* Functions for fun */
	
 	private void ballReset() {
    	ballBody.setTransform(start_position, 0f);
		Vector2 unit = getUnitVector();
		ballBody.setLinearVelocity(START_SPEED * unit.x, START_SPEED * unit.y);
	}

	public void onUpdate(final float pSecondsElapsed) {
		//ballBody.setTransform(ballBody.getPosition(),ballAngle);
		if (gameOver) {
        	this.mScene.setChildScene(this.mGameOverMenuScene, false, true, true);
        	this.gameOverScore = new Text(575, 345, mGameOverFont, Integer.toString(current_score), this.getVertexBufferObjectManager());
        	this.mGameOverMenuScene.attachChild(gameOverScore);
        	clearBooleans();
			current_score = 0;
			return;
		}
		
		if (ballAngle == 360){ ballAngle = 0;}
		ballAngle += ballAngleDiff;
		
		for (int j = 0; j<NUM_SLAPPERS-1; j++) {
		
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
			startTimer();
		}
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public boolean ballStuck() {
		return ballBody.getLinearVelocity().y == 0;
	}
	
	private Vector2 getUnitVector() {
		Vector2 unitVector = new Vector2(randomNumGen.nextFloat(), randomNumGen.nextFloat());
		
		if (NUM_SLAPPERS == 2 || NUM_SLAPPERS == 4) {
			// avoid the ball going "too" horizontal
			while (unitVector.y < 0.2f)
				unitVector.y += 0.1f;
		}
		
		int quadrant = randomNumGen.nextInt(4);
		if (quadrant == 3) { // traditional fourth quadrant
			unitVector.y = -unitVector.y;
		}
		else if (quadrant == 2) { // traditional third quadrant
			unitVector.x = -unitVector.x;
			unitVector.y = -unitVector.y;
		}
		else if (quadrant == 1) { // traditional second quadrant
			unitVector.x = -unitVector.x;
		}
		
		return unitVector.nor();
	}
	
	private void showPlayerLives(Font font, int numLives, int pX, int pY) {
		this.playerLives = new Text(pX, pY, font,
				("Lives left: " + numLives), "Players Lives: X".length(), this.getVertexBufferObjectManager());
		this.mScene.attachChild(playerLives);
	}
	
	private void showCurrentScore(Font font, int pX, int pY) {
		currentScoreText = new Text(pX, pY, font, ("Score: " + current_score), 
							("Score:                       ").length(), this.getVertexBufferObjectManager());
		this.mScene.attachChild(currentScoreText);
	}
	
	private void showInfo(Font font, int numPlayerLives) {
		showPlayerLives(font, numPlayerLives, (int)(CAMERA_WIDTH*.01), (int)(CAMERA_HEIGHT*.06));
		showCurrentScore(font, (int)(CAMERA_WIDTH*.01), (int)(CAMERA_HEIGHT*.11));
	}
	
	/*
	 * Naive way to reset the game.
	 */
	protected void resetGame() {
		this.ballReset();
		this.clearBooleans();
		this.numPlayerLives = NUM_LIVES;
		playerLives.setText("Lives left: " + numPlayerLives);
		current_score = 0;
		currentScoreText.setText("Score: " + current_score);
	}

	private String getLosingMessage() {
		return this.loserMessage + "Game about to start.";
	}
	
	private void startTimer() {
		if(timerCountOn) {
			return;
		}
		else {
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
    			if(resuming) {
    				if(gameResetMessage.hasParent())
    					mScene.detachChild(gameResetMessage);
    				if(countDownTimer.hasParent())
    					mScene.detachChild(countDownTimer);
    			}
    			//else if(gameOver || gameStarting) {
    			else if (gameStarting) {
    				resetGame();
    				if(gameResetMessage.hasParent())
    					mScene.detachChild(gameResetMessage);
    				if(countDownTimer.hasParent())
    					mScene.detachChild(countDownTimer);
    			}
    			else { // Coming back from someone losing a life but game still going
    				ballReset();
    				if(gameResetMessage.hasParent())
    					mScene.detachChild(gameResetMessage);
    				if(countDownTimer.hasParent())
    					mScene.detachChild(countDownTimer);
    			}
				
				ballBody.setActive(true);
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
		this.gameOver = false;
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
		Vector2 temp = new Vector2(0,0);
		public void beginContact(Contact contact) {
			Body bodyA = contact.getFixtureA().getBody();
			Body bodyB = contact.getFixtureB().getBody();
			Object userAData = bodyA.getUserData();
			Object userBData = bodyB.getUserData();
					
			//Rotation / Speed / Hit
			if(userAData.equals("ballBody") && userBData.equals("paddleBody")
					|| userAData.equals("paddleBody") && userBData.equals("ballBody")) {
				Log.i("Contact Made", "Ball contacted the paddle");
				temp = paddleCollision(ballBody,paddleBody,temp);
				ballBody.setLinearVelocity(temp.x * MainActivity.ballSpeedDifficultyIncrease,
										  (ballBody.getLinearVelocity().y + temp.y) * MainActivity.ballSpeedDifficultyIncrease);
			}
			
			for (int j = 0; j<NUM_SLAPPERS-1; j++) {
				if(userAData.equals("ballBody") && userBData.equals(aiBody[j])
						|| userAData.equals(aiBody[j]) && userBData.equals("ballBody")) {
					Log.i("Contact Made", "Ball contacted the paddle");

					aiSlapper[j].setHit(true);	// what's the point of this?
					temp = paddleCollision(ballBody,aiBody[j],temp);
					
					//Log.i("ballVelocity", "before: " + ballBody.getLinearVelocity().x + ", " + ballBody.getLinearVelocity().y);
					ballBody.setLinearVelocity(temp.x * MainActivity.ballSpeedDifficultyIncrease,
											   (temp.y+ballBody.getLinearVelocity().y) * MainActivity.ballSpeedDifficultyIncrease);
					//Log.i("ballVelocity", "after: " + ballBody.getLinearVelocity().x + ", " + ballBody.getLinearVelocity().y);
				}
			}
			
			boundaryCollision(userAData, userBData);
		}
		
		public Vector2 paddleCollision(Body ballB, Body slapperB, Vector2 t) {
			// if the slapper is at an angle then this function doesn't take into consideration the y distance at all?
			Vector2 e = new Vector2();
			e = t;
			float c = 0, d = 0;
			
			c = (slapperB.getPosition().x - ballB.getPosition().x)*-10;
			//Log.i("paddleCollision(): temp.x",""+ c);
				
			d =  1; // What is the point of this and e.y = d?
			
			e.x = c;
			e.y = d;
			
			return e;
		}
		
		public void boundaryCollision(Object userAData, Object userBData) {
			switch (NUM_SLAPPERS) {
				case 4: 
					if(userAData.equals("ballBody") && userBData.equals("groundBody")
							|| userAData.equals("groundBody") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the ground");
						outOfBounds = true;
						playerLives.setText("Lives left: " + --numPlayerLives);
						
						if(numPlayerLives == 0) {
							gameOver = true;
						}
					}
					else if(userAData.equals("ballBody") && userBData.equals("roofBody")
							|| userAData.equals("roofBody") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the roof");
						outOfBounds = true;
						current_score += AI_KILL_SCORE;
						currentScoreText.setText("Score: " + current_score);
					}
					else if(userAData.equals("ballBody") && userBData.equals("leftBody")
							|| userAData.equals("leftBody") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the left");
						outOfBounds = true;
						current_score += AI_KILL_SCORE;
						currentScoreText.setText("Score: " + current_score);
					}
					else if(userAData.equals("ballBody") && userBData.equals("rightBody")
							|| userAData.equals("rightBody") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the right");
						outOfBounds = true;
						current_score += AI_KILL_SCORE;
						currentScoreText.setText("Score: " + current_score);
					}
					break;
				case 3:
					if(userAData.equals("ballBody") && userBData.equals("btri")
							|| userAData.equals("btri") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the ground");
						outOfBounds = true;
						playerLives.setText("Lives left: " + --numPlayerLives);
						
						if(numPlayerLives == 0) {
							gameOver = true;
						}
					}
					else if(userAData.equals("ballBody") && userBData.equals("rtri")
							|| userAData.equals("rtri") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the roof");
						outOfBounds = true;
						current_score += AI_KILL_SCORE;
						currentScoreText.setText("Score: " + current_score);
					}
					else if(userAData.equals("ballBody") && userBData.equals("ltri")
							|| userAData.equals("ltri") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the left");
						outOfBounds = true;
						current_score += AI_KILL_SCORE;
						currentScoreText.setText("Score: " + current_score);
					}
					break;
				default:
					if(userAData.equals("ballBody") && userBData.equals("groundBody")
							|| userAData.equals("groundBody") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the ground");
						outOfBounds = true;
						playerLives.setText("Lives left: " + --numPlayerLives);
						
						if(numPlayerLives == 0) {
							gameOver = true;
						}
					}
					else if(userAData.equals("ballBody") && userBData.equals("roofBody")
							|| userAData.equals("roofBody") && userBData.equals("ballBody")) {
						Log.i("Contact Made", "Ball contacted the roof");
						outOfBounds = true;
						current_score += AI_KILL_SCORE;
						currentScoreText.setText("Score: " + current_score);
					}
					
					break;
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