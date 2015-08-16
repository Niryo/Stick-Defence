package huji.ac.il.stick_defence;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Nir on 12/08/2015.
 */
public class MathBomb {
    //TODO: DISABLE ARRAOWS
    private Random rand = new Random(System.currentTimeMillis());
    private LinearLayout answerButtonsLayout;
    private LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
    private ArrayList<Integer> correctAnswer;
    private int currentButtonToPress = 0;
    private ArrayList<Button> buttons = new ArrayList<>();
    private int NUM_OF_FAKE_BUTTONS = 2;
    private int NUMBER_OF_FRAMES = 13;
    private String BLUE_COLOR = "#66CCFF";
    private String GREEN_COLOR = "#85FF85";
    private Context context;
    private LinearLayout bomb;


    public MathBomb(Context context) {

        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        bomb = (LinearLayout) inflater.inflate(R.layout.math_bomb_layout, null);
        this.answerButtonsLayout = (LinearLayout) bomb.findViewById(R.id.answer_buttons);

        //generate random numbers in the range of 100 to 1000
        int firstNum = rand.nextInt(900)+100;
        int secondNum = rand.nextInt(900)+100;
//add numbers to textView:
        FrameLayout numbersFrame = (FrameLayout) bomb.findViewById(R.id.numbers_frame);
        MathBombTextView textView = new MathBombTextView(context);
        textView.setText(firstNum + "+" + secondNum + "=?");
        FrameLayout.LayoutParams textlp= new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        textView.setLayoutParams(textlp);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
        numbersFrame.addView(textView);
//add buttons:
        addAnswerButtons(firstNum + secondNum);
    }





    private void addAnswerButtons(int result) {
        Log.w("custom", "the result is:" + result);
        ArrayList<Integer> nums = new ArrayList<>();

        //break the results to single digits:
        while (result >= 1) {
            int nextNum = result % 10;
            nums.add(nextNum);
            result = result / 10;
        }
        //the correct order of the digits in the result should be reversed"
        this.correctAnswer = new ArrayList<>(nums);
        Collections.reverse(correctAnswer);

        //create a button for each digit in the result:
        for (int num : nums) {
            Button btn = new Button(context);
            this.buttons.add(btn);
            btn.setText(String.valueOf(num));
            addStyleToButton(btn);
            attachClickListeners(btn);
        }
        //add 2 more fake buttons:
        for (int i = 0; i < NUM_OF_FAKE_BUTTONS; i++) {
            int randomNum = rand.nextInt(10);
            Button fakeButton = new Button(context);
            buttons.add(fakeButton);
            fakeButton.setText(String.valueOf(randomNum));
            addStyleToButton(fakeButton);
            attachClickListeners(fakeButton);

        }

        //add buttons to layout:
        Collections.shuffle(buttons);
        for (Button btn : buttons) {
            answerButtonsLayout.addView(btn);
        }
    }

    private void addStyleToButton(Button btn) {
      btn.getBackground().setColorFilter(Color.parseColor(BLUE_COLOR), PorterDuff.Mode.SRC);
        LinearLayout.LayoutParams margins =new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1);
        margins.setMargins(2,2,2,2);
        btn.setLayoutParams(margins);



    }

    private void attachClickListeners(Button btn) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button) v;
                //check if the current button is really the button that should have been pressed:
                if (correctAnswer.get(currentButtonToPress) == Integer.parseInt((String) btn.getText())) {
                    currentButtonToPress++;
                    btn.getBackground().setColorFilter(Color.parseColor(GREEN_COLOR), PorterDuff.Mode.SRC);
                    if (currentButtonToPress == correctAnswer.size()) {
                        ((ViewGroup) bomb.getParent()).removeView(bomb);
                        GameState.getInstance().enableButtons();
                        Client.getClientInstance().send(Protocol.stringify(Protocol.Action.MATH_BOMB_SOLVED));
                        Log.w("custom", "guessed correct!");
                    }

                } else { //we pressed on the wrong button:
                    currentButtonToPress = 0;
                    for (Button button : buttons) {
                        button.getBackground().setColorFilter(Color.parseColor(BLUE_COLOR), PorterDuff.Mode.SRC);
                    }
                }

            }
        });
    }

    public LinearLayout getBomb() {
        return this.bomb;
    }

    /**
     * Custom Text View that set the text size to fill the whole bomb space
     */
    private class MathBombTextView extends TextView{

        private int SCALE_FACTOR=7;
        public MathBombTextView(Context context) {
            super(context);
        }

        public MathBombTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            super.setTextSize(w/SCALE_FACTOR);
        }
    }
}


