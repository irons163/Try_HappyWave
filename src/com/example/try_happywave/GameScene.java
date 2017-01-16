package com.example.try_happywave;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.try_gameengine.action.MAction;
import com.example.try_gameengine.action.MAction2;
import com.example.try_gameengine.action.MathUtil;
import com.example.try_gameengine.action.MovementAction;
import com.example.try_gameengine.action.MovementActionInfo;
import com.example.try_gameengine.action.MovementActionItemBaseReugularFPS;
import com.example.try_gameengine.action.MovementActionItemRotation;
import com.example.try_gameengine.action.MovementActionSetWithThreadPool;
import com.example.try_gameengine.action.MovementAtionController;
import com.example.try_gameengine.action.RotationOnceController;
import com.example.try_gameengine.framework.ALayer;
import com.example.try_gameengine.framework.ALayer.LayerParam;
import com.example.try_gameengine.framework.ButtonLayer;
import com.example.try_gameengine.framework.ButtonLayer.OnClickListener;
import com.example.try_gameengine.framework.GameView;
import com.example.try_gameengine.framework.IGameController;
import com.example.try_gameengine.framework.IGameModel;
import com.example.try_gameengine.framework.ILayer;
import com.example.try_gameengine.framework.LabelLayer;
import com.example.try_gameengine.framework.Layer;
import com.example.try_gameengine.framework.LayerManager;
import com.example.try_gameengine.framework.Sprite;
import com.example.try_gameengine.scene.EasyScene;
import com.example.try_gameengine.utils.GameTimeUtil;
import com.example.try_happywave.R;
import com.example.try_happywave.utils.AudioUtil;
import com.example.try_happywave.utils.BitmapUtil;
import com.example.try_happywave.utils.CommonUtil;

public class GameScene extends EasyScene implements ButtonLayer.OnClickListener{
    int direction;
//    Bitmap bitmapUtil;
    int offsetX;
    int offsetY;
    Sprite backgroundNode, backgroundNode2, backgroundNode3, backgroundNode4;
    int backgroundMovePointsPerSec;
    int gameScoreForDistance;
    int createToolCount;
    LabelLayer gameScoreForDistanceLabel;
    int distanceCount;
    int readyStep;
//    NSTimer * theGameTimer, * theReadyTimer, *theToolTimer;
    LabelLayer readyLabel;
    boolean readyFlag;
    Sprite rankBtn;
//    MyADView * myAdView;
    
    List<Bitmap> musicBtnTextures;
    
    Sprite musicBtn;
    float speedX;
    float speedY;
    
    int toolTimeCount;
    boolean flyFlag;
    
    boolean toolCounterStart;
    boolean checkEatToolable;
    
    boolean gameFlag;
    List<List<Wall>> walls;
    List<Tool> tools;
    Player player;
    
	private GameTimeUtil gameTimeUtil;
	
	public GameScene(Context context, String id, int level) {
		super(context, id, level);
		// TODO Auto-generated constructor stub
	    walls = new ArrayList<List<Wall>>();
	    tools = new ArrayList<Tool>();
	    direction = DIRECTION_RIGHT;
	    initGame();
	    getBackground();
	    
	    addAutoDraw(backgroundNode);
	    backgroundMovePointsPerSec = (int)speedY;
	    createInitWall();
	    createPlayer();
	    initGameScoreForDistanceLabel();
        
        isEnableRemoteController(false);

    	gameTimeUtil = new GameTimeUtil(200);
    	
    	MovementAction movementAction = MAction.rotationToAction(2000, 90);
    	player.runMovementAction(movementAction);
    	
	    final MathUtil mathUtil = new MathUtil(speedX, speedY);
	    mathUtil.setINITSPEEDX(mathUtil.genTotalSpeed());
	    mathUtil.setAngle(90);
	    
//	    MovementAction movementAction = new MovementActionSetWithThreadPool();
//		movementAction.setMovementActionController(new MovementAtionController());
////		MovementActionInfo info = new MovementActionInfo(1000, 1, 10, 0, "", new RotationOnceController(30), false, this, BulletAnimaton.Move.getName());
//		final MovementActionInfo info = new MovementActionInfo(1000, 1, 10, 0, "", new RotationOnceController(30), false, player, null);
//		MovementAction action = new MovementActionItemBaseReugularFPS(info);
//		movementAction.addMovementAction(action);
//			
//			movementAction.setTimerOnTickListener(new MovementAction.TimerOnTickListener() {
//				
//				@Override
//				public void onTick(float dx, float dy) {
//					// TODO Auto-generated method stub
//					
//					mathUtil.setAngle(info.getRotationController().getRotation());
//					mathUtil.genSpeed();
//					dx = mathUtil.getSpeedX();
//					dy = mathUtil.getSpeedY();
//					moveBg(dx, dy);
//					
//					Matrix matrix = new Matrix();
//					
//					player.setRotation(info.getRotationController().getRotation());
//					
////					matrix.mapPoints(new float[10]);
////					int r = ground.getR();
////					System.out.println("r"+r);
////					boolean isRightMove = ground.isRightMove();
////					canvas.save();
////					int rotationX = x + BitmapUtil.player.getWidth()/2;
////					int rotationY = y + BitmapUtil.player.getHeight()/2;
////					canvas.translate(rotationX , rotationY);
//////					canvas.rotate(r);
////					if(isRightMove){
////						canvas.rotate(r);
////					}else{
////						canvas.rotate(r);
////					}
//////					canvas.restore();
////					canvas.drawBitmap(BitmapUtil.player, x - rotationX, y -rotationY, null);
////					canvas.restore();
//					
//				}
//			});
//	    player.runMovementAction(movementAction);
	}
	
	public void initGame(){
	    gameFlag = true;
	    readyFlag = true;
	    flyFlag = false;
	    toolCounterStart = false;
	    checkEatToolable = true;
	    readyStep = 0;
	    speedX = BASE_SPEEDX;
	    speedY = BASE_SPEEDY;

	    offsetX = (int) BitmapUtil.wall_size.x;
	    offsetY = (int) BitmapUtil.wall_size.y;
//	    offsetY = 0;
//	    readyLabel = [SKLabelNode labelNodeWithFontNamed:@"Chalkduster"];
	    readyLabel = new LabelLayer(0, 0, false);
	    readyLabel.setText("");
	    readyLabel.setTextSize(80);
//	    readyLabel.color = [SKColor colorWithRed:0.15 green:0.15 blue:0.3 alpha:1.0];
	    readyLabel.setTextColor(Color.RED);
	    readyLabel.setPosition(CommonUtil.screenWidth/2 - readyLabel.getWidth(), CommonUtil.screenHeight/2);
	    
	    addAutoDraw(readyLabel);
	    
	    rankBtn = new Sprite(0, 0, false);
	    rankBtn.setBitmapAndAutoChangeWH(BitmapUtil.btn_GameCenter_hd);
	    rankBtn.setWidth(42);
	    rankBtn.setHeight(42);
	    rankBtn.setAnchorPoint(0, 0);
	    rankBtn.setPosition(CommonUtil.screenWidth/2 - rankBtn.getWidth(), CommonUtil.screenHeight/2);
	    rankBtn.setzPosition(1);
	    addAutoDraw(rankBtn);
	    
	    musicBtnTextures = new ArrayList<>();
	    musicBtnTextures.add(BitmapUtil.btn_Music_hd);
	    musicBtnTextures.add(BitmapUtil.btn_Music_Select_hd);
//	    [musicBtnTextures addObject:[SKTexture textureWithImageNamed:@"btn_Music-hd"]];
//	    [musicBtnTextures addObject:[SKTexture textureWithImageNamed:@"btn_Music_Select-hd"]];
	    
	    musicBtn = new Sprite(0,0,false);
	    musicBtn.setBitmapAndAutoChangeWH(BitmapUtil.btn_Music_hd);
	    musicBtn.setWidth(42);
	    musicBtn.setHeight(42);
	    musicBtn.setAnchorPoint(0, 0);
	    musicBtn.setPosition(CommonUtil.screenHeight/2 - musicBtn.getWidth(), CommonUtil.screenHeight - 42);
	    musicBtn.setzPosition(1);
	    addAutoDraw(musicBtn);
	    
	    int[] musics = new int[]{R.raw.am_white, R.raw.biai, R.raw.cafe, R.raw.deformation};
	    
	    Random random = new Random();
	    int index = random.nextInt(4);
	    AudioUtil.playMusic(musics[index]);
	    
	    SharedPreferences sharedPreferences = context.getSharedPreferences("data", 0);
	
	    boolean isPlayMusic = sharedPreferences.getBoolean("isPlayMusic", true);

	    if(isPlayMusic){
	        AudioUtil.startMusic();
	        musicBtn.bitmap = musicBtnTextures.get(0);
	    }else{
	        AudioUtil.pauseMusic();
	        musicBtn.bitmap = musicBtnTextures.get(1);
	    }
	    
//	    myAdView = [MyADView spriteNodeWithTexture:nil];
//	    myAdView.size = CGSizeMake(self.frame.size.width, self.frame.size.width/5.0f);
	    //        myAdView.position = CGPointMake(self.frame.size.width/2, self.frame.size.height - 35);
//	    myAdView.position = CGPointMake(self.frame.size.width/2, 0);
//	    [myAdView startAd];
//	    myAdView.zPosition = 1;
//	    myAdView.anchorPoint = CGPointMake(0.5, 0);
//	    [self addChild:myAdView];
	    

		 
	}

	
	GameView gameView;
	
	
void checkCollistion(){

}

//public void downAndUp(final Sprite sprite,float down, float downTime, float up, float upTime, boolean isRepeat){
//    MovementAction downAct = MAction.moveByY(down, (long)(downTime*1000));
////    downAct.setTimerOnTickListener(new MovementAction.TimerOnTickListener() {
////		
////		@Override
////		public void onTick(float dx, float dy) {
////			// TODO Auto-generated method stub
////			sprite.move(dx, dy);
////		}
////	});
//    //moveByX(CGFloat(0), y: down, duration: downTime)
//    MovementAction upAct = MAction.moveByY(up, (long)(upTime*1000));
////    upAct.setTimerOnTickListener(new MovementAction.TimerOnTickListener() {
////		
////		@Override
////		public void onTick(float dx, float dy) {
////			// TODO Auto-generated method stub
////			sprite.move(dx, dy);
////		}
////	});
//    
//    //MAction use threadPool it would delay during action by action.
//    MovementAction downUpAct = MAction2.sequence(new MovementAction[]{downAct,upAct});
//    downUpAct.setMovementActionController(new MovementAtionController());
//    if (isRepeat) {
//    	sprite.runMovementActionAndAppend(MAction.repeatForever(downUpAct));
//    }else {
//    	sprite.runMovementActionAndAppend(downUpAct);
//    }
//    
//    
//}
	    
	@Override
	public void initGameView(Activity activity, IGameController gameController,
			IGameModel gameModel) {
		// TODO Auto-generated method stub
		gameView = new GameView(activity, gameController, gameModel);
	}

	public void action(){
//		gameDog.alone();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction()==MotionEvent.ACTION_DOWN){
//		        PointF location = this.touch
			float x = event.getX();
		    float y = event.getY();
		    PointF location = new PointF(x, y);
		    
		    if(rankBtn.getFrame().contains(location.x, location.y)){
		            //rankBtn.texture = storeBtnClickTextureArray[PRESSED_TEXTURE_INDEX];
		            
//		            showRankView();
	        }else if(musicBtn.getFrame().contains(location.x, location.y)){
	            if(AudioUtil.isBackgroundMusicPlaying()){
	                AudioUtil.pauseMusic();
	                musicBtn.bitmap = musicBtnTextures.get(1);
//		                [[NSUserDefaults standardUserDefaults] setBool:false forKey:@"isPlayMusic"];
	            }else{
	                AudioUtil.playBackgroundMusic();
	                musicBtn.bitmap = musicBtnTextures.get(0);
//		                [[NSUserDefaults standardUserDefaults] setBool:true forKey:@"isPlayMusic"];
	            }
	        }
		    
		    direction = -direction;
		    speedX = -speedX;
		    speedY = -speedY;
		    
		    MovementAction movementAction = MAction.rotationToAction(5000, 60*direction);
	    	player.runMovementAction(movementAction);
//	    	movementAction = MAction.scaleToAction(2000, 2.0f*direction, 2.0f*direction);
//	    	movementAction = MAction2.sequence(new MovementAction[]{MAction.scaleToAction(1000, 0, 0),MAction.scaleToAction(1000, 2.0f, 1.5f)});
//	    	movementAction = MAction.scaleToAction(2000, 2.0f*direction, 2.0f*direction);
//	    	player.runMovementActionAndAppend(movementAction);
//		    player.setXscale(0);
//		    player.setYscale(0);
		    
	    	for(List<Wall> wallLine : walls){
	            for(Wall wall : wallLine){
	            	movementAction = MAction.rotationToAction(5000, 60*direction);
	            	wall.runMovementAction(movementAction);
	            }
	        }
		}
		LayerManager.getInstance().onTouchLayers(event);
		return true;
	}
	
	@Override
	public void process() {
		// TODO Auto-generated method stub
		
	    if(readyFlag && theReadyTimer==null){
	        initReadyTimer();
	        initToolTimer();
	    }
	    
	    if(theReadyTimer.isArriveExecuteTime())
	    	countReadyTimer();
	    if(countToolTimer.isArriveExecuteTime())
	    	countToolTimer();
	    
	    if (!gameFlag || readyFlag)
	        return;
	    
	    updateWithTimeSinceLastUpdate();
	    
	    
		
	}
	
	@Override
	public void doDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		LayerManager.getInstance().drawLayers(canvas, null);
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		canvas.drawRect(new RectF(player.getFrameInScene().left+10, player.getFrameInScene().top+10, player.getFrameInScene().right-10, player.getFrameInScene().bottom-10), paint);
//		canvas.drawRect(player.getFrameInScene(), paint);
//		for (int wallLinePosition = 0; wallLinePosition < walls.size(); wallLinePosition++) {
//	        for (Wall wall : walls.get(wallLinePosition)) {
////	        	canvas.drawRect(new RectF(wall.getFrame().left+20, wall.getFrame().top+20, wall.getFrame().right-20, wall.getFrame().bottom-20), paint);
//	        	canvas.drawRect(wall.getFrameInScene(), paint);
//	        }
//		}
		
//		fight.drawSelf(canvas, null);
	}

	@Override
	public void beforeGameStart() {
		// TODO Auto-generated method stub
	}

	@Override
	public void arrangeView(Activity activity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivityContentView(Activity activity) {
		// TODO Auto-generated method stub
		activity.setContentView(gameView);
	}

	@Override
	public void afterGameStart() {
		// TODO Auto-generated method stub
		Log.e("game scene", "game start");
		AudioUtil.playBackgroundMusic();
	}
	
	@Override
	protected void beforeGameStop() {
		// TODO Auto-generated method stub
		Log.e("game scene", "game stop");
		AudioUtil.stopBackgroundMusic();
	}
	
	@Override
	protected void afterGameStop() {
		// TODO Auto-generated method stub
//		AudioUtil.stopBackgr          oundMusic();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(ButtonLayer buttonLayer) {
		// TODO Auto-generated method stub

	}

	


	public void initReadyTimer(){
	    readyStep = 0;
	    theReadyTimer = new GameTimeUtil(1000);
//	    theReadyTimer = [NSTimer scheduledTimerWithTimeInterval:1.0
//	                                                     target:self
//	                                                   selector:@selector(countReadyTimer)
//	                                                   userInfo:nil
//	                                                    repeats:YES];
//	    [timers addObject:theReadyTimer];
	}

	public void countReadyTimer(){
	    //    for (int i = 0; i < 4; i++) {
	    //        readyStep = i;
	    if (readyStep == 0) {
	        
	        //            canvas.drawText("READY", 150, height / 2, paint);
	        readyLabel.setText("READY");
	        readyLabel.setPosition(CommonUtil.screenWidth/2, CommonUtil.screenHeight/2);
	    } else if(readyStep==5){
	        readyLabel.setHidden(true);
	        theReadyTimer.enable(false);
	        readyFlag = false;
	        return;
	    }
	    else {
	        //            canvas.drawText(4 - readyStep + "", width / 2, height / 2,
	        //                            paint);
	        readyLabel.setText(4-readyStep+"");
//	        readyLabel.position = CGPointMake(self.frame.size.width/2 - readyLabel.frame.size.width/2, self.frame.size.height/2 );
	        readyLabel.setPosition(CommonUtil.screenWidth/2, CommonUtil.screenHeight/2);
	    }
	    
	    readyStep++;
	    //        sleep(1);
	    //    }
	}

	GameTimeUtil countToolTimer, theReadyTimer;
	public void initToolTimer(){
	    readyStep = 0;
	    countToolTimer = new GameTimeUtil(1000);
	}

	public void countToolTimer(){
	    if(toolCounterStart && toolTimeCount<=0){
	        toolCounterStart = false;
	        resetSpeed();
	        return;
	    }else if(!toolCounterStart){
	        return;
	    }
	    toolTimeCount--;
	}

	public void updateWithTimeSinceLastUpdate(){
//		if(gameTimeUtil.isArriveExecuteTime()){     
			LayerManager.getInstance().processLayers();
	        move();
//	        draw();
	        if(checkEatToolable)
	        	checkEatTool();
	        checkRemoveTools();
//	        player.frameTrig();
//	    }	    
	}

//	final int BASE_SPEEDX = 6;
	final int BASE_SPEEDX = 0;
	final float BASE_SPEEDY = 4.5f;
	final int TOOL_TIME = 10;
	final int WALL_LEFT_AND_RIGHT_DISTANCE = 230;
	int playerStartX = 160;
	int platerStartY = 200;
	int DIRECTION_LEFT = -1;
	int DIRECTION_RIGHT = 1;


	public void createInitWall() {
	    int wallLeftX = 40, wallRightX = wallLeftX + WALL_LEFT_AND_RIGHT_DISTANCE;
//	    int wallY = ((CommonUtil*)[CommonUtil sharedInstance]).screenHeight;
//	    int wallY = 0;
	    int wallY = CommonUtil.screenHeight;
	    for (int i = 0; i < 60; i++) {
//	        [self createWallLineWithLeftX:wallLeftX WithRightX:wallRightX WithY:wallY enableOffsetX:false];
	        createWallLineWithLeftX(wallLeftX, wallRightX, wallY, false);
//	    	wallY += offsetY;
	        wallY -= offsetY;
	    }
	}

	public void createWallLineWithLeftX(int wallLeftX, int wallRightX, int wallY, boolean enableOffsetX){
//	    if (wallLeftX < 20) {
//	        offsetX = -offsetX;
//	    } else if (wallRightX > ((CommonUtil*)[CommonUtil sharedInstance]).screenWidth - 20
//	               - bitmapUtil.wall_size.width) {
//	        offsetX = -offsetX;
//	    }

	    if(enableOffsetX){
	    	Random random = new Random();
	        int dir = random.nextInt(2);
	        if(dir == 0){
	            offsetX = -offsetX;
	        }
	        
	        wallLeftX += offsetX;
	        wallRightX += offsetX;
	    }
	    
//	    if (wallY >= (CommonUtil.screenHeight+offsetY))
//	        return;
	    if (wallY <= -offsetY)
	        return;
	    
//	    wallY += offsetY;
	    wallY -= offsetY;
	    
//	    System.out.println("wallY" + wallY);
	    
	    Wall wallLeft = new Wall(0, 0, false);
	    wallLeft.setBitmapAndAutoChangeWH(BitmapUtil.wall_bitmap);
	    wallLeft.setWidth((int) BitmapUtil.wall_size.x*2);
	    wallLeft.setHeight((int) BitmapUtil.wall_size.y*2);
	    wallLeft.setPosition(wallLeftX, wallY);
	    wallLeft.setXscale(-1.0f);
//	    wallLeft.setYscale(2.0f);
	    wallLeft.setAnchorPoint(0f, 0f);
//	    wallLeft.setAnchorPoint(0.5f, 0.5f);
//	    wallLeft.setAnchorPoint(1f, 1f);
//	    wallLeft.setAnchorPoint(1.2f, -1.2f);
	    Wall wallRight = new Wall(0, 0, false);
	    wallRight.setBitmapAndAutoChangeWH(BitmapUtil.wall_bitmap);
	    wallRight.setWidth((int) BitmapUtil.wall_size.x*2);
	    wallRight.setHeight((int) BitmapUtil.wall_size.y*2);
	    wallRight.setPosition(wallRightX, wallY);
	    wallRight.setAnchorPoint(0f, 0f);
//	    wallRight.setAnchorPoint(0.5f, 0.5f);
//	    wallRight.setAnchorPoint(1f, 1f);
	    addAutoDraw(wallLeft);
	    addAutoDraw(wallRight);
	    
	    List<Wall> wallLine = new ArrayList<Wall>();
	    wallLine.add(wallLeft);
	    wallLine.add(wallRight);
	    walls.add(wallLine);
	}

	public void initGameScoreForDistanceLabel(){
//	    gameScoreForDistanceLabel = [SKLabelNode labelNodeWithText:@"0"];
	    gameScoreForDistanceLabel = new LabelLayer(0, 0, false);
	    gameScoreForDistanceLabel.setText("0");
	    gameScoreForDistanceLabel.setTextSize(30);
	    gameScoreForDistanceLabel.setTextColor(Color.rgb((int)(255*0.15), (int)(255*0.15), (int)(255*0.7)));
	    gameScoreForDistanceLabel.setPosition(gameScoreForDistanceLabel.getFrame().width()/2, gameScoreForDistanceLabel.getFrame().height() -100 - gameScoreForDistanceLabel.getFrame().height());
	    gameScoreForDistanceLabel.setzPosition(5);
//	    gameScoreForDistanceLabel.text = "0";
//	    gameScoreForDistanceLabel.fontSize = 30;
//	    gameScoreForDistanceLabel.fontColor = SKColor colorWithRed:0.15 green:0.15 blue:0.7 alpha:1.0];
//	    gameScoreForDistanceLabel.position = CGPointMake(gameScoreForDistanceLabel.frame.size.width/2, self.frame.size.height - 100 - gameScoreForDistanceLabel.frame.size.height);
//	    gameScoreForDistanceLabel.zPosition = 5;
	    addAutoDraw(gameScoreForDistanceLabel);
	}

	public void createPlayer(){
	    
//	    player = new Player(playerStartX, platerStartY);
	    player = new Player(playerStartX,platerStartY,false);
//	    player.setBitmapAndFrameWH(BitmapUtil.hamster, 96, 100);
	    player.setBitmapAndFrameWHAndColAndRowNum(BitmapUtil.hamster, 72, 75, 7, 2);
	    player.setAnchorPoint(1,1);
//	    player.setAnchorPoint(0.5f,0.5f);
//	    player.setAnchorPoint(0,0);
//	    player.setAnchorPoint(1.2f,-1.2f);
	    player.setXscale(-2f);
//	    player.setYscale(-1.5f);
	    player.setYscale(1.5f);
//	    NSArray* array =  [TextureHelper getTexturesWithSpriteSheetNamed:@"hamster" withinNode:nil sourceRect:CGRectMake(0, 0, 192, 200) andRowNumberOfSprites:2 andColNumberOfSprites:7
//	                                                           sequence:@[@7,@8]];
	 
	    player.addActionFPSFrame("hamster", new int[]{7,8}, new int[]{8,8});
	    player.setAction("hamster");
//	    player.setHeight(50);
//	    player.setWidth(50);
	    player.setzPosition(2);
	    player.setBackgroundColor(Color.BLUE);
	    addAutoDraw(player);
//	    [player runAction:[SKAction repeatActionForever:[SKAction animateWithTextures:array timePerFrame:0.2]]];
//	    player.size = CGSizeMake(35, 35);
//	    player.position = CGPointMake(playerStartX, platerStartY);
//	    player.zPosition = 2;
//	    [self addChild:player];
	    
//	    for(NSArray* wallLine in walls){
//	        for(Wall* wall in wallLine){
//	            wall.position = CGPointMake(wall.position.x-, wall.position.y);
//	        }
//	    }
	}
	static int increaseScoreDistance = 10;
	public void move(){
	    
//	    player.move(speedX,0);
//	    player.position = CGPointMake(player.position.x+speedX, player.position.y);
	    
//	    for(SKNode* node in self.children){
//	        node.position = CGPointMake(node.position.x-speedX, node.position.y);
//	    }

	    
	    
	    int offsetXByWallCorrecX = 0;
	    
	    if(flyFlag){
	        for(List<Wall> wallLine : walls){
	            for(Wall wall : wallLine){
	                if(wall.getY() > player.getY()-player.getHeight()/2 && wall.getY() < player.getY()+player.getHeight()/2)
	                {
	                    int wallCorrectLeftX = (int) (player.getX() - WALL_LEFT_AND_RIGHT_DISTANCE/2);
	                    offsetXByWallCorrecX = (int) (wall.getX() - wallCorrectLeftX) ;
	                    break;
	    //                wall.position.x;
	                }
	            }
	        }
	    }
	    
    	final MathUtil mathUtil = new MathUtil(speedX, speedY);
	    mathUtil.setINITSPEEDX(mathUtil.genTotalSpeed());
	    mathUtil.setAngle(player.getRotation()+90);
	    mathUtil.genSpeed();
	    
	    float newSpeedX = mathUtil.getSpeedX();
	    float newSpeedY = mathUtil.getSpeedY();
	    
	    moveBg(-newSpeedX, newSpeedY);
	    
	    for(List<Wall> wallLine : walls){
	        for(Wall wall : wallLine){
	            if(flyFlag){
	                wall.setPosition(wall.getX()-offsetXByWallCorrecX, wall.getY());
	            }else{
//	                NSLog(@"wall %f",speedX);

	                wall.setPosition(wall.getX()+newSpeedX, wall.getY()-newSpeedY);
	            }
	            
	        }
	    }
	    
	    if(flyFlag){
	        moveTools(offsetXByWallCorrecX);
	    }else{
	        moveTools(speedX);
	    }
	    
	    doWallMoveAndCollisionDetectedAndCreateAndRemoveWall();
	    
	    distanceCount += speedY;
	    
	    if(distanceCount<increaseScoreDistance){
	        return;
	    }else{
	        distanceCount -= increaseScoreDistance;
	    }
	    gameScoreForDistance += increaseScoreDistance;
	    gameScoreForDistanceLabel.setText(gameScoreForDistance+"");
	    gameScoreForDistanceLabel.setPosition(gameScoreForDistanceLabel.getWidth()/2, CommonUtil.screenHeight - 100 - gameScoreForDistanceLabel.getHeight());
	    
	    if(gameScoreForDistance/200 >= createToolCount){
	    	createToolCount++;
	    	System.out.println(createToolCount+"");
	    	List<Wall> wallLine = walls.get(walls.size()-1);
	        Wall wall = wallLine.get(0);
//	        createToolWithToolX((int)(wall.getX() + WALL_LEFT_AND_RIGHT_DISTANCE/2));
	        createToolWithToolX((int)(player.getX() - player.getWidth()/2));
//	        [self createToolWithToolX:wall.position.x + WALL_LEFT_AND_RIGHT_DISTANCE/2];
	    }
	}

	public boolean isCollision(Player player, Sprite wall) {
	    RectF rectPlayer = player.getFrame();
	    RectF rectPlayerCollisionArea = new RectF(rectPlayer.left + rectPlayer.width()*0.25f, rectPlayer.top + rectPlayer.height()*0.25f, rectPlayer.left + rectPlayer.width()*0.75f, rectPlayer.top + rectPlayer.height()*0.75f);
	    RectF rectWall = wall.getFrame();
	    return RectF.intersects(rectPlayerCollisionArea, rectWall);
	}

	public void doWallMoveAndCollisionDetectedAndCreateAndRemoveWall() {
	    boolean isCollision = false;
	    boolean isNeedCreateNewInstance = false;
	    boolean isNeedRemoveInstance = false;
	    int firstCarPosition = 0;
	    int LastCatPosition = walls.size() - 1;
	    Wall lastLeftWall = null;
	    for (int wallLinePosition = 0; wallLinePosition < walls.size(); wallLinePosition++) {
	        boolean isChecked = false;
	        for (Wall wall : walls.get(wallLinePosition)) {
//	            wall.move(speedY);
//	            [wall move];
	            if(!isChecked){
	                isChecked = true;
	                if (wallLinePosition == LastCatPosition) {
	                    isNeedCreateNewInstance = wall.isNeedCreateNewInstance();
	                    lastLeftWall = wall;
	                }
	                if (wallLinePosition == firstCarPosition) {
	                    isNeedRemoveInstance = wall.isNeedRemoveInstance();
	                }
	            }
	            if (!isCollision)
	                isCollision = isCollision(player, wall);
	        }
	    }
	    
	    if (isNeedCreateNewInstance) {
	        int wallLeftX = (int) lastLeftWall.getX();
	        int wallRightX = wallLeftX + WALL_LEFT_AND_RIGHT_DISTANCE;
	        int wallY = (int) lastLeftWall.getY();
	        createWallLineWithLeftX(wallLeftX, wallRightX, wallY, true);
	    }
	    if (isNeedRemoveInstance) {
	        List<Wall> wallWithLine = walls.get(firstCarPosition);
	        walls.remove(wallWithLine);
//	        walls.remove(walls.get(firstCarPosition));
	        for(Wall wall : wallWithLine){
	            wall.removeFromParent();
	        }
	        wallWithLine = null;
	    }
//	    gameFlag = !isCollision;
//	    
//	    if(isCollision){
//	        player.removeAllMovementActions();
////	        GameCenterUtil * gameCenterUtil = [GameCenterUtil sharedInstance];
////	        [gameCenterUtil reportScore:gameScoreForDistance forCategory:@"com.irons.HappySpeedUp"];
////	        [self.gameDelegate showGameOver];
////	        [myAdView close];
//	    }
	}

	public void getBackground(){
	    backgroundNode = new Sprite(0, 0, false);
//	    backgroundNode.anchorPoint = CGPointZero;
	    
	    Sprite bg1 = new Sprite(0, 0, false);
	    bg1.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg1.anchorPoint = CGPointZero;
	    bg1.setWidth(CommonUtil.screenWidth);
	    bg1.setHeight(CommonUtil.screenHeight);
	    bg1.setPosition(0, 0);
	    
	    backgroundNode.addChild(bg1);
	    
	    Sprite bg2 = new Sprite(0, 0, false);
	    bg2.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg2.anchorPoint = CGPointZero;
	    bg2.setWidth(CommonUtil.screenWidth);
	    bg2.setHeight(CommonUtil.screenHeight);
	    bg2.setPosition(0, bg1.getHeight());
	    
	    backgroundNode.addChild(bg2);
	    
	    backgroundNode.setWidth(bg1.getWidth());
	    backgroundNode.setHeight(bg1.getHeight() + bg2.getHeight());
//	    backgroundNode.size = CGSizeMake(bg1.size.width, bg1.size.height + bg2.size.height);
//	    backgroundNode.name = @"background";
	    
	    backgroundNode2 = new Sprite(0, 0, false);
//	    backgroundNode.anchorPoint = CGPointZero;
	    
	    bg1 = new Sprite(0, 0, false);
	    bg1.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg1.anchorPoint = CGPointZero;
	    bg1.setWidth(CommonUtil.screenWidth);
	    bg1.setHeight(CommonUtil.screenHeight);
	    bg1.setPosition(0, 0);
	    
	    backgroundNode2.addChild(bg1);
	    
	    bg2 = new Sprite(0, 0, false);
	    bg2.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg2.anchorPoint = CGPointZero;
	    bg2.setWidth(CommonUtil.screenWidth);
	    bg2.setHeight(CommonUtil.screenHeight);
	    bg2.setPosition(0, bg1.getHeight());
	    
	    backgroundNode2.addChild(bg2);
	    
	    backgroundNode2.setWidth(bg1.getWidth());
	    backgroundNode2.setHeight(bg1.getHeight() + bg2.getHeight());
//	    backgroundNode.size = CGSizeMake(bg1.size.width, bg1.size.height + bg2.size.height);
//	    backgroundNode.name = @"background";
	    backgroundNode2.setPosition(0, backgroundNode.getHeight());
	    addAutoDraw(backgroundNode2);
	    
	   
	    backgroundNode3 = new Sprite(backgroundNode.getWidth(), 0, false);
//	    backgroundNode.anchorPoint = CGPointZero;
	    
	    bg1 = new Sprite(0, 0, false);
	    bg1.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg1.anchorPoint = CGPointZero;
	    bg1.setWidth(CommonUtil.screenWidth);
	    bg1.setHeight(CommonUtil.screenHeight);
	    bg1.setPosition(0, 0);
	    
	    backgroundNode3.addChild(bg1);
	    
	    bg2 = new Sprite(0, 0, false);
	    bg2.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg2.anchorPoint = CGPointZero;
	    bg2.setWidth(CommonUtil.screenWidth);
	    bg2.setHeight(CommonUtil.screenHeight);
	    bg2.setPosition(0, bg1.getHeight());
	    
	    backgroundNode3.addChild(bg2);
	    
	    backgroundNode3.setWidth(bg1.getWidth());
	    backgroundNode3.setHeight(bg1.getHeight() + bg2.getHeight());
//	    backgroundNode.size = CGSizeMake(bg1.size.width, bg1.size.height + bg2.size.height);
//	    backgroundNode.name = @"background";
	    addAutoDraw(backgroundNode3);
	    
	    backgroundNode4 = new Sprite(0, 0, false);
//	    backgroundNode.anchorPoint = CGPointZero;
	    
	    bg1 = new Sprite(0, 0, false);
	    bg1.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg1.anchorPoint = CGPointZero;
	    bg1.setWidth(CommonUtil.screenWidth);
	    bg1.setHeight(CommonUtil.screenHeight);
	    bg1.setPosition(0, 0);
	    
	    backgroundNode4.addChild(bg1);
	    
	    bg2 = new Sprite(0, 0, false);
	    bg2.setBitmapAndAutoChangeWH(BitmapUtil.bg01_green);
//	    bg2.anchorPoint = CGPointZero;
	    bg2.setWidth(CommonUtil.screenWidth);
	    bg2.setHeight(CommonUtil.screenHeight);
	    bg2.setPosition(0, bg1.getHeight());
	    
	    backgroundNode4.addChild(bg2);
	    
	    backgroundNode4.setWidth(bg1.getWidth());
	    backgroundNode4.setHeight(bg1.getHeight() + bg2.getHeight());
//	    backgroundNode.size = CGSizeMake(bg1.size.width, bg1.size.height + bg2.size.height);
//	    backgroundNode.name = @"background";
	    backgroundNode4.setPosition(backgroundNode.getWidth(), backgroundNode.getHeight());
	    addAutoDraw(backgroundNode4);
	}

	public void moveBg(float dx, float dy){
        Sprite bgNode = null;
        for(int i = 0; i < 4;i++){
        	if(i==0)
        		bgNode = backgroundNode;
        	else if(i==1)
        		bgNode = backgroundNode2;
        	else if(i==2)
        		bgNode = backgroundNode3;
        	else if(i==3)
        		bgNode = backgroundNode4;
        	
//	        CGPoint backgroundVelocity = CGPointMake(0, -backgroundMovePointsPerSec);
        PointF backgroundVelocity = new PointF(-dx, -dy);
        //        CGPoint amountToMove = backgroundVelocity;
        bgNode.setPosition(bgNode.getX() + backgroundVelocity.x, bgNode.getY() + backgroundVelocity.y);
        	
            if (bgNode.getY() >= bgNode.getHeight()) {
            	bgNode.setPosition(bgNode.getX(), bgNode.getY() - bgNode.getHeight()*2);
            }
          else if(bgNode.getX() <= -bgNode.getWidth()){
            	bgNode.setPosition(bgNode.getX() + bgNode.getWidth()*2, bgNode.getY());
            }
          else if(bgNode.getX() >= bgNode.getWidth()){
            	bgNode.setPosition(bgNode.getX() - bgNode.getWidth()*2, bgNode.getY());
            }
            else if (bgNode.getY() <= -bgNode.getHeight()) {
            	bgNode.setPosition(bgNode.getX(), bgNode.getY() + bgNode.getHeight()*2);
            }
            

        }

	}

	public void draw(){
//	    Canvas canvas = surfaceHolder.lockCanvas();
//	    canvas.drawColor(Color.WHITE);
	//    
//	    player.draw(canvas);
	//    
//	    for (ArrayList<Wall> wallLine : walls) {
//	        for (Wall wall : wallLine) {
//	            wall.draw(canvas);
//	        }
//	    }
//	    surfaceHolder.unlockCanvasAndPost(canvas);
	}

	public void checkEatTool(){
	    boolean isCollision = false;
	    Tool tool = null;
	    for (int wallLinePosition = 0; wallLinePosition < tools.size(); wallLinePosition++) {
	        
	        tool = tools.get(wallLinePosition);
	        isCollision = isCollision(player, tool);
	        
	        if (isCollision) {
	            break;
	        }
	    }
	    
	    if(isCollision){
	        tools.remove(tool);
	        tool.removeFromParent();
	        doToolEffect(tool);
	        toolTimeCount = TOOL_TIME;
	    }
	    
	}

	public void checkRemoveTools(){
	    List<Tool> removeArray = new ArrayList<>();
	    for(Tool tool : tools){
	        if(tool.isNeedRemoveInstance()){
	            tool.removeFromParent();
	            removeArray.add(tool);
	        }
	    }
	    
	    tools.removeAll(removeArray);
	}

	int TOOL_SPEEDUP = 0;
	int TOOL_SPEEDDOWN = 1;
	int TOOL_FLY = 2;
	int TOOL_TPYE_NUM = 3;

	public void createToolWithToolX(int toolX){
	    Tool tool;
	    Random random = new Random();
	    int type = random.nextInt(5);
	    if(type <= 1){
	        tool = new Tool(0, 0, false);
	        tool.setBitmapAndAutoChangeWH(BitmapUtil.speedup_bitmap);
	        tool.setWidth((int) BitmapUtil.speedup_size.x);
	        tool.setHeight((int) BitmapUtil.speedup_size.y);
	        tool.type = TOOL_SPEEDUP;
	    }else if(type <= 3){
	        tool = new Tool(0, 0, false);
	        tool.setBitmapAndAutoChangeWH(BitmapUtil.speeddown_bitmap);
	        tool.setWidth((int) BitmapUtil.speeddown_size.x);
	        tool.setHeight((int) BitmapUtil.speeddown_size.y);
	        tool.type = TOOL_SPEEDDOWN;
	    }else{
	        tool = new Tool(0, 0, false);
	        tool.setBitmapAndAutoChangeWH(BitmapUtil.fly_bitmap);
	        tool.setWidth((int) BitmapUtil.fly_size.x);
	        tool.setHeight((int) BitmapUtil.fly_size.y);
	        tool.type = TOOL_FLY;
	    }
	    tool.setPosition(toolX, CommonUtil.screenHeight);
	    addAutoDraw(tool);
	    tools.add(tool);
	}

	public void moveTools(float moveDistance){
//	    NSLog("%f",moveDistance);
	    for(Tool tool : tools){
	    	tool.setPosition(tool.getX()-moveDistance, tool.getY() - speedY);
	    }
	}

	public void doToolEffect(Tool tool){
	    int type = tool.type;
	    if(type == TOOL_SPEEDUP){
	        speedUp();
	    }else if(type == TOOL_SPEEDDOWN){
	        speedDown();
	    }else{
	        fly();
	    }
	}

	public void speedUp(){
	    if(speedY > 10){
	        return;
	    }
	    
	    if (speedX > 0) {
	        speedX += 3.9;
	    }else{
	        speedX -= 3.9;
	    }
	    speedY += 3;
	    
	    toolCounterStart = true;
	}

	public void speedDown(){
	    if(speedX < 4 && speedY < 42){
	        return;
	    }
	    
	    if (speedX > 0) {
	        speedX -= 3.9;
	    }else{
	        speedX += 3.9;
	    }

	    speedY -= 3;
	    
	    toolCounterStart = true;
	}

	public void fly(){
	    flyFlag = true;
	    speedX = 0;
	    toolCounterStart = true;
//	    player.xScale = 2;
//	    player.yScale = 2;
//	    MovementAction fly = [SKAction scaleTo:2 duration:2.0f];
	    MovementAction fly = MAction.scaleToAction(2000, 2, 2);
	    player.runMovementAction(fly);
	    checkEatToolable = false;
	    
	    Sprite wing = new Sprite(0, 0, false);
	    wing.setBitmapAndAutoChangeWH(BitmapUtil.wing);
	    wing.setWidth(75);
	    wing.setHeight(35);
	    wing.setPosition(0, 10);
	    wing.setzPosition(3);
	    wing.setRotation(player.getRotation());
	    player.addChild(wing);
	}

	public void resetSpeed(){
	    if(speedX>0){
	        speedX = BASE_SPEEDX;
	    }else{
	        speedX = -BASE_SPEEDX;
	    }
	    speedY = BASE_SPEEDY;
	    
	    if (flyFlag) {
//	        SKAction* leftDown = [SKAction scaleTo:1 duration:2.0f];
//	        [player runAction:[SKAction sequence:@[leftDown, [SKAction runBlock:^{
//	            flyFlag = false;
//	            checkEatToolable = true;
//	            [player removeAllChildren];
//	        }]]]];
	        MovementAction leftDown = MAction.scaleToAction(2000, 1, 1);
	        player.runMovementAction(MAction2.sequence(new MovementAction[]{leftDown, MAction.runBlockNoDelay(new MAction.MActionBlock() {
				
				@Override
				public void runBlock() {
					// TODO Auto-generated method stub
					flyFlag = false;
		            checkEatToolable = true;
		            player.removeAllChildren();
				}
			})}));
	    }
	    
	}

	public int gameScoreForDistance(){
	    return gameScoreForDistance;
	}
}
