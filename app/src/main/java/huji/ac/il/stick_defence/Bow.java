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
import android.graphics.Rect;
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

    private static        Bitmap leftBowPic = null;
    private static        Bitmap rightBowPic = null;
    private Sprite        sprite;
    private double        frameScaledWidth;   //the frame width after scale
    private double        frameScaledHeight;  //the frame height after scale
    private double        bowAngle;
    private int           screenWidth;
    private int           screenHeight;
    private int           towerHeight;

    private Sprite.Player player;

    private Path path = new Path();
    private PathMeasure pathMeasure;
    private float pathLength;
    private float[] pos= new float[2];
    private float[] tan= new float[2];;
    private Matrix matrix= new Matrix();
    private int distance=0;
    private float boffsetX;
    private float boffsetY;
    private Bitmap[] scaledLeftBow = new Bitmap[NUMBER_OF_FRAMES];
    private int currentFrame=0;

    /**
     * Constructor
     * @param context the context
     * @param player the player - right or left
     */
    public Bow(Context context, Sprite.Player player, int towerHeight) {
        if (null == leftBowPic){
            leftBowPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bow); // Read resource only once
        }
        if (null == rightBowPic){
            rightBowPic = Sprite.mirrorBitmap(leftBowPic);
        }

        sprite = new Sprite();

        if (player == Sprite.Player.LEFT){
            sprite.initSprite(context, leftBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        } else {
            sprite.initSprite(context, rightBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        }

        this.player = player;

        int frameHeight = leftBowPic.getHeight();
        int frameWidth = leftBowPic.getWidth() / NUMBER_OF_FRAMES;
        this.screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        this.screenHeight = context.getResources().getDisplayMetrics().heightPixels;

        double scaleDownFactor = sprite.getScaleDownFactor();

        this.frameScaledHeight = frameHeight/scaleDownFactor;
        this.frameScaledWidth = frameWidth/scaleDownFactor;

        this.towerHeight = towerHeight;


        for(int i=0; i<NUMBER_OF_FRAMES; i++){
            Bitmap temp= Bitmap.createBitmap(this.leftBowPic, i*frameWidth, 0, frameWidth, frameHeight);
            this.scaledLeftBow[i] = Bitmap.createScaledBitmap(temp,   (int) this.frameScaledWidth, (int) this.frameScaledHeight ,false);
        }

//        this.scaledLeftBow=temp;

        //==============temp=============
        RectF oval = new RectF();
        oval.set(100,towerHeight-30, 140, towerHeight+30);
        path.addArc(oval, 280,80);

        this.pathMeasure= new PathMeasure(path,false);
        this.pathLength = pathMeasure.getLength();
        this.boffsetX= this.scaledLeftBow[0].getWidth()/2;
        this.boffsetY= this.scaledLeftBow[0].getHeight()/2;
        this.resetMatrix();

    }





    /**
     * Updates bow's place and angel
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
        sprite.update(gameTime);

    }

    /**
     * Draws the tower
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        // where to draw the sprite
        if (player == Sprite.Player.RIGHT){
            sprite.render(canvas, (int)(screenWidth - frameScaledWidth),
                    (int)(towerHeight - frameScaledHeight));
        } else {
//            super.render(canvas, 0, (int)(towerHeight - frameScaledHeight));
            renderBow(canvas);
        }

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
        matrix.postRotate(degrees, boffsetX, boffsetY);
        matrix.postTranslate(pos[0]-boffsetX, pos[1]-boffsetY);
    }

    public void stretch(){

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
        //TODO - shoot an arrow
    }

    private void renderBow(Canvas canvas){
            canvas.drawBitmap(this.scaledLeftBow[this.currentFrame], matrix, null);

    }
}
