package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;

import java.io.Serializable;

/**
 * Created by yahav on 01/05/15.
 */
public class Bow implements Serializable {
    //Bow height in relation to the screen height.
    //0-1 double. For instance, 0.5 will cause the
    //bow to span over a half of the screen height.
    private static final double SCREEN_HEIGHT_PORTION = 0.15;
    private static final int NUMBER_OF_FRAMES = 9;
    private static int ARC_PATH_WIDTH_FACTOR = 2;
    private static int ARC_PATH_HEIGHT_FACTOR = 4;
    private static int actualArcPathHeight;
    private static int actualArcPathWidth;
    private static final int ARC_PATH_START_ANGLE = 290;
    private static final int ARC_PATH_LENGTH = 80;

    private int bowStrechSound, bowReleaseSound;
    private GameState gameState = GameState.getInstance();
    private static Bitmap leftBowPic = null;
    //    private static        Bitmap rightBowPic = null;
    private Sprite sprite;
    private int towerHeight;
    private Sprite.Player player;

    private Context context;
    private Path path = new Path();
    private PathMeasure pathMeasure;
    private float pathLength;
    private float[] pos = new float[2];
    private float[] tan = new float[2];
    private transient Matrix matrix = new Matrix();
    private int distance = 0;
    private float bm_offsetX;
    private float bm_offsetY;
    private Bitmap[] scaledBow = new Bitmap[NUMBER_OF_FRAMES];
    private int currentFrame = 0;
    private float degrees;

    /**
     * Constructor
     *
     * @param context the context
     * @param player  the PLAYER - right or left
     */
    public Bow(Context context, Sprite.Player player, Tower tower) {
        if (leftBowPic == null) {
            leftBowPic = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.bow); // Read resource only once
        }
        //set the arc path relative to the tower dimentions:
        actualArcPathHeight = (int) tower.getScaledWidth() / ARC_PATH_HEIGHT_FACTOR;
        actualArcPathWidth = (int) tower.getScaledWidth() / ARC_PATH_WIDTH_FACTOR;

        sprite = new Sprite();
        this.context = context;
        if (player == Sprite.Player.LEFT) {
            sprite.initSprite(context, leftBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        } else {
            sprite.initSprite(context, leftBowPic, NUMBER_OF_FRAMES,
                    player, SCREEN_HEIGHT_PORTION);
        }

        this.player = player;

        int frameHeight = leftBowPic.getHeight();
        int frameWidth = leftBowPic.getWidth() / NUMBER_OF_FRAMES;

        for (int i = 0; i < NUMBER_OF_FRAMES; i++) {
            Bitmap frameToScale;
            if (player == Sprite.Player.LEFT) {
                frameToScale = Bitmap.createBitmap(leftBowPic, i * frameWidth, 0,
                        frameWidth, frameHeight);
            } else {
                frameToScale = Bitmap.createBitmap(leftBowPic, i * frameWidth,
                        0, frameWidth, frameHeight);
            }
            this.scaledBow[i] = Bitmap.
                    createScaledBitmap(frameToScale,
                            (int) this.sprite.getScaledFrameWidth(),
                            (int) this.sprite.getScaledFrameHeight(),
                            false);
        }


        RectF oval = new RectF();
        Sprite.Point towerPos = tower.getPosition();
        float centerTowerX = (float) (towerPos.getX() + tower.getWidth() / 2);
        if (player == Sprite.Player.LEFT) {
            oval.set(centerTowerX, towerPos.getY() - actualArcPathHeight, centerTowerX + actualArcPathWidth, towerPos.getY() + actualArcPathHeight);
            path.addArc(oval, ARC_PATH_START_ANGLE, ARC_PATH_LENGTH);
        } else {
            oval.set(centerTowerX - actualArcPathWidth, towerPos.getY() - actualArcPathHeight, centerTowerX, towerPos.getY() + actualArcPathHeight);
            path.addArc(oval, 540 - ARC_PATH_START_ANGLE, -ARC_PATH_LENGTH);
        }

        this.pathMeasure = new PathMeasure(path, false);
        this.pathLength = this.pathMeasure.getLength();
        this.bm_offsetX = this.scaledBow[0].getWidth() / 2;
        this.bm_offsetY = this.scaledBow[0].getHeight() / 2;
        this.resetMatrix();
        Arrow.init(context, sprite.getScaleDownFactor());

    }

    /**
     * Updates bow's place and angel
     *
     * @param gameTime the current time in milliseconds
     */
    public void update(long gameTime) {
//        sprite.update(gameTime);


    }

    /**
     * Draws the tower
     *
     * @param canvas the canvas to draw on
     */
    public void render(Canvas canvas) {
        canvas.drawBitmap(this.scaledBow[this.currentFrame], matrix, null);

//        Paint paint =new Paint();
//        paint.setColor(Color.BLACK);
//        paint.setStrokeWidth(3);
//        paint.setStyle(Paint.Style.STROKE);
//        canvas.drawPath(this.path, paint);
    }


    public boolean rotateLeft() {
        if (distance > 0) {
            this.distance -= 1;
            this.resetMatrix();
            return true;
        }

        return false;
    }

    public boolean rotateRight() {
        if (distance < this.pathLength) {
            this.distance += 1;
            this.resetMatrix();
            return true;
        }
        return false;
    }

    private void resetMatrix() {
        pathMeasure.getPosTan(distance, pos, tan);
        matrix.reset();
        this.degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
        matrix.postRotate(this.degrees, bm_offsetX, bm_offsetY);
        matrix.postTranslate(pos[0] - bm_offsetX, pos[1] - bm_offsetY);
    }

    public void setBowDirection(Sprite.Point point) {
        float newDegrees = (float) Math.toDegrees(Math.atan2(point.getY() - this.pos[1], point.getX() - this.pos[0]));
        while (Math.abs(this.degrees - newDegrees) > 2) {
            if (Sprite.Player.LEFT == player) {
                if (this.degrees < newDegrees) {
                    if (!rotateRight()) {
                        break;
                    }
                } else {
                    if (!rotateLeft()) {
                        break;
                    }
                }
            } else {
                if (this.degrees < newDegrees) {
                    if (!rotateLeft()) {
                        break;
                    }
                } else {
                    if (!rotateRight()) {
                        break;
                    }
                }
            }

        }

    }

    public void stretch() {
        if (this.currentFrame == 1){
            playStrechSound();
        }
        if (this.currentFrame == NUMBER_OF_FRAMES - 1) {
            this.currentFrame = 0;
        }
        if (this.currentFrame < NUMBER_OF_FRAMES - 4) {
            this.currentFrame++;
        }
    }

    public void unStretch() {

        if (this.currentFrame > 0) {
            this.currentFrame--;
        }
    }

    public void release() {
        if (this.currentFrame == NUMBER_OF_FRAMES - 4) {
            stopStrechSound();
            playReleaseSound();
            this.gameState.addArrow(new Arrow(this.context, this.pos[0], this.pos[1], this.tan, this.player, 0));


        }
        this.currentFrame = NUMBER_OF_FRAMES - 1;

    }

    public void aimAndShoot(double relativeDistance, double delayInSec) { //todo: add animation for strech and unstrech

        this.distance = (int) (pathLength * relativeDistance);
        resetMatrix();
        this.gameState.addArrow(new Arrow(this.context, this.pos[0], this.pos[1], this.tan, this.player, delayInSec));

    }

    public double getRelativeDistance() {
        return this.distance / pathLength;
    }

    public void playStrechSound(){
        this.bowStrechSound = Sounds.getInstance().playSound(Sounds.BOW_STRECH,
                                                             false);
    }

    public void stopStrechSound(){
        Sounds.stopSound(this.bowStrechSound);
    }

    public void playReleaseSound(){
        this.bowStrechSound = Sounds.getInstance().playSound(Sounds.BOW_RELEASE,
                                                             false);
    }

}
