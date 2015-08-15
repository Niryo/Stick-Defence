package huji.ac.il.stick_defence;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Nir on 12/08/2015.
 */
public class MathBomb {
    //TODO: DISABLE ARRAOWS
    private Random rand = new Random(System.currentTimeMillis());
    private LinearLayout layout;
    private LinearLayout answerButtonsLayout;
    private Bitmap frame;
    private int width;
    private int height;
    private static Bitmap bitmap = null;
    private LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
    private ArrayList<Integer> correctAnswer;
    private int currentButtonToPress = 0;
    private ArrayList<Button> buttons = new ArrayList<>();
    private int NUM_OF_FAKE_BUTTONS = 2;
    private int NUMBER_OF_FRAMES = 13;
    private int DIGITS_IN_FIRST_NUMBER = 3;
    private int DIGITS_IN_SECOND_NUMBER = 3;
    private int EQUAL_SIGN_POSITION = 11;
    private int PLUS_POSITION = 10;
    private int QUESTION_MARK_POSITION = 12;
    private String BLUE_COLOR = "#66CCFF";
    private String GREEN_COLOR = "#85FF85";
    private Context context;
    private LinearLayout bomb;


    public MathBomb(Context context) {
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.numbers);

        }
        this.context = context;
        this.width = bitmap.getWidth() / NUMBER_OF_FRAMES;
        this.height = bitmap.getHeight();
        //lp.setMargins(2, 2, 2, 2);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        bomb = (LinearLayout) inflater.inflate(R.layout.math_bomb_layout, null);
        this.layout = (LinearLayout) bomb.findViewById(R.id.math_layout);
        this.answerButtonsLayout = (LinearLayout) bomb.findViewById(R.id.answer_buttons);


        //create first num:
        int firstNum = addRandomNumToLayout(DIGITS_IN_FIRST_NUMBER);
        //create plus sign:
        addSpecificFrame(PLUS_POSITION);
        //create second num:
        int secondNum = addRandomNumToLayout(DIGITS_IN_SECOND_NUMBER);
        //create equal
        addSpecificFrame(EQUAL_SIGN_POSITION);
        //create questionMark:
        addSpecificFrame(QUESTION_MARK_POSITION);

        //add the buttons for the answer:
        addAnswerButtons(firstNum + secondNum);
        Log.w("custom", "" + firstNum + "      " + secondNum);
    }

    private int addRandomNumToLayout(int numOfDigits) {
        int number = 0;
        for (int i = 0; i < numOfDigits; i++) {
            int randomNum = rand.nextInt(10);
            //don't allow numbers to start with zero:
            if (randomNum == 0 && i == 0) {
                i--;
                continue;
            }
            //build the number (1*first+ 10*second+ 100*last)
            number += Math.pow(10, numOfDigits - 1 - i) * randomNum;
            frame = Bitmap.createBitmap(bitmap, randomNum * this.width, 0, this.width, this.height);
            ImageView pic = new ImageView(context);
            pic.setImageBitmap(frame);
            pic.setLayoutParams(lp);
            layout.addView(pic);
        }

        return number;
    }

    private void addSpecificFrame(int frameNumber) {
        frame = Bitmap.createBitmap(bitmap, frameNumber * this.width, 0, this.width, this.height);
        ImageView sign = new ImageView(context);
        sign.setImageBitmap(frame);
        sign.setLayoutParams(lp);
        layout.addView(sign);
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

}
