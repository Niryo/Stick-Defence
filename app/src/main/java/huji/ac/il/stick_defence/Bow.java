package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.Log;

/**
 * Created by yahav on 01/05/15.
 */
public class Bow{
    //Bow height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //bow to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.15;
    private static final int NUMBER_OF_FRAMES = 9;

    private GameState gameState= GameState.getInstance();
    private static        Bitmap leftBowPic = null;
    private static        Bitmap rightBowPic = null;
    private Sprite        sprite;
    private int           towerHeight;
    private Sprite.Player player;

    private Path path = new Path();
    private PathMeasure pathMeasure;
    private float pathLength;
    private float[] pos= new float[2];
    private float[] tan= new float[2];
    private Matrix matrix= new Matrix();
    private int distance=0;
    private float bm_offsetX;
    private float bm_offsetY;
    private Bitmap[] scaledLeftBow = new Bitmap[NUMBER_OF_FRAMES];
    private int currentFrame=0;

    private float angel;

    /**
     * Constructor
     * @param context the context
     * @param player the player - right or left
     */
    public Bow(Context context, Sprite.Player player, int towerHeight) {
        if (leftBowPic == null) {
            leftBowPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bow); // Read resource only once
        }
        if (rightBowPic == null) {
            rightBowPic = Sprite.mirrorBitmap(leftBowPic);
        }

        sprite = new Sprite();

        if (player == Sprite.Player.LEFT) {
            sprite.initSprite(context, leftBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        } else {
            sprite.initSprite(context, rightBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        }

        this.player = player;

        int frameHeight = leftBowPic.getHeight();
        int frameWidth = leftBowPic.getWidth() / NUMBER_OF_FRAMES;


        this.towerHeight = towerHeight;


        for (int i = 0; i < NUMBER_OF_FRAMES; i++) {
            Bitmap frameToScale = Bitmap.createBitmap(this.leftBowPic, i * frameWidth, 0, frameWidth, frameHeight);
            this.scaledLeftBow[i] = Bitmap.createScaledBitmap(frameToScale, (int) this.sprite.getScaledFrameWidth(), (int) this.sprite.getScaledFrameHeight(), false);
        }


        //==============temp=============
        RectF oval = new RectF();
        oval.set(100, towerHeight - 30, 140, towerHeight + 30);
        path.addArc(oval, 280, 80);

        this.pathMeasure = new PathMeasure(path, false);
        this.pathLength = this.pathMeasure.getLength();
        this.bm_offsetX = this.scaledLeftBow[0].getWidth() / 2;
        this.bm_offsetY = this.scaledLeftBow[0].getHeight() / 2;
        this.resetMatrix();
        Arrow.init(context, sprite.getScaleDownFactor());

    }

    /**
     * Updates bow's place and angel
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
//        sprite.update(gameTime);


    }

    /**
     * Draws the tower
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        renderBow(canvas);

        Paint paint =new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(this.path, paint);
    }


    public void rotateLeft(){
       if(distance>0){
           this.distance-=1;
           this.resetMatrix();
       }
    }

    public void rotateRight(){
        if(distance<this.pathLength){
            this.distance+=1;
            this.resetMatrix();
        }
    }
    private void resetMatrix(){
        pathMeasure.getPosTan(distance, pos, tan);
        matrix.reset();
        float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
        matrix.postRotate(degrees, bm_offsetX, bm_offsetY);
        matrix.postTranslate(pos[0]- bm_offsetX, pos[1]- bm_offsetY);
    }

    public void setBowDirection(Sprite.Point point){
      /*  float degrees = (float)(Math.atan2(point.getY(), point.getX())*180.0/Math.PI);
        angel = degrees;

        pathMeasure.getPosTan(distance, pos, tan);
        matrix.reset();

        matrix.postRotate(degrees, bm_offsetX, bm_offsetY);
        matrix.postTranslate(pos[0]- bm_offsetX, pos[1]- bm_offsetY);*/


    }

    public void stretch(){
        if(this.currentFrame==NUMBER_OF_FRAMES-1){
            this.currentFrame=0;
        }
        if(this.currentFrame<NUMBER_OF_FRAMES-4){
            this.currentFrame++;
        }
    }
    public void unStretch(){

        if(this.currentFrame>0){
            this.currentFrame--;
        }
    }
    public void release(){
        if(this.currentFrame != NUMBER_OF_FRAMES-1) {
            this.currentFrame = NUMBER_OF_FRAMES - 1;
            this.gameState.addArrow(this.pos[0], this.pos[1], this.tan);
        }
    }

    private void renderBow(Canvas canvas){
        canvas.drawBitmap(this.scaledLeftBow[this.currentFrame], matrix, null);

    }
    public double getScaleDownFactor(){
        return this.sprite.getScaleDownFactor();
    }
}
