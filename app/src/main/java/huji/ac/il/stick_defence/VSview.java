package huji.ac.il.stick_defence;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Nir on 09/08/2015.
 */
public class VSview extends View {
    private double PADDING_TOP = 5;
    private double PADDING_BOTTOM = 1.1;
    private int FONT_SCALE_FACTOR = 6;
    private int DESIRED_WIDTH = 400;
    private int DESIRED_HEIGHT = 400;
    private static Bitmap scaledPic = null;
    private String name1;
    private String name2;
    private Paint paintRight = null;
    private Paint paintLeft = null;


    public VSview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VSview(Context context) {
        super(context);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        if (scaledPic == null) {
            if (width == 0) {
                width = 1;
            }
            if (height == 0) {
                height = 1;
            }
            Bitmap pic = BitmapFactory.decodeResource(getResources(),
                    R.drawable.vs);
            float scale = Math.max((float) pic.getHeight() / (float) height, (float) pic.getWidth() / width);
            int newWidth = Math.round(pic.getWidth() / scale);
            int newHeight = Math.round(pic.getHeight() / scale);
            scaledPic = Bitmap.createScaledBitmap(pic, newWidth, newHeight, true);
        }

        if (paintRight == null) {
            paintRight = new Paint();
            paintRight.setTextSize(scaledPic.getWidth() / FONT_SCALE_FACTOR);
            paintRight.setTextAlign(Paint.Align.LEFT);

            paintLeft = new Paint();
            paintLeft.setTextSize(scaledPic.getWidth() / FONT_SCALE_FACTOR);
            paintRight.setTextAlign(Paint.Align.RIGHT);

        }

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Get the width measurement
        int widthSize = getMeasurement(widthMeasureSpec,
                DESIRED_WIDTH);

        //Get the height measurement
        int heightSize = getMeasurement(heightMeasureSpec,
                DESIRED_HEIGHT);

        //MUST call this to store the measurements
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(scaledPic, 0, 0, null);

        String half1Name1 = name1.substring(0, name1.length() / 2);
        String half2Name1 = name1.substring(name1.length() / 2);
        String half1Name2 = name2.substring(0, name2.length() / 2);
        String half2Name2 = name2.substring(name2.length() / 2);


        canvas.drawText(half1Name1, scaledPic.getWidth() / 2, (int) (scaledPic.getHeight() / PADDING_TOP), paintRight);
        canvas.drawText(half2Name1, scaledPic.getWidth() / 2, (int) (scaledPic.getHeight() / PADDING_TOP), paintLeft);
        canvas.drawText(half1Name2, scaledPic.getWidth() / 2, (int) (scaledPic.getHeight() / PADDING_BOTTOM), paintRight);
        canvas.drawText(half2Name2, scaledPic.getWidth() / 2, (int) (scaledPic.getHeight() / PADDING_BOTTOM), paintLeft);
    }

    private int getMeasurement(int measureSpec, int contentSize) {
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);
        int resultSize = 0;
        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                //Big as we want to be
                resultSize = contentSize;
                break;
            case View.MeasureSpec.AT_MOST:
                //Big as we want to be, up to the spec
                resultSize = Math.min(contentSize, specSize);
                break;
            case View.MeasureSpec.EXACTLY:
                //Must be the spec size
                resultSize = specSize;
                break;
        }

        return resultSize;
    }

    public void setNames(String name1, String name2) {
        this.name1 = name1;
        this.name2 = name2;
    }

}
