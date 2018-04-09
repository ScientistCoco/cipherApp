package com.example.court.cipherapp;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


// Notes: Some test cases considered for this cipher
//        - User enters only numbers, this should still be encoded
//        - User enters only spaces? what to do for this ...
public class railCipher extends AppCompatActivity {

    Dialog pop_up_result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rail_cipher);

        final TextView railInput = findViewById(R.id.railInput);
        final EditText encodeTextInput = findViewById(R.id.encodeTextInput);
        Button encodeBtn = findViewById(R.id.encodeButton);
        SeekBar railSeek = findViewById(R.id.railSeek);
        pop_up_result = new Dialog(this);

        railSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            int min_value = 2;  // we choose a minimum value to prevent the seekbar from going to zero. If it hits zero then it won't really be ciphering anything?
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    progress = i + min_value;
                    railInput.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        encodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check that the user has put in a text to encode
                if(!encodeTextInput.getText().toString().isEmpty()){
                    // Check that the user has put in a rail number to use
                    if (!railInput.getText().toString().isEmpty()){
                        String result = railFenceCipher(encodeTextInput.getText().toString(), Integer.parseInt(railInput.getText().toString()), getApplicationContext());

                        pop_up_result.setContentView(R.layout.pop_up_cipher);
                        TextView railResult = pop_up_result.findViewById(R.id.cipherResult);
                        railResult.setText(result);
                        pop_up_result.show();

                        TextView x_close = pop_up_result.findViewById(R.id.x_close_btn);
                        x_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pop_up_result.dismiss();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getApplication(), "You need to put in an input", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private static String railFenceCipher(String plainText, int railNum, Context context){
        // One thing we need to check is that the railNum is less than the number of characters in the plainText
        // if it isn't then it won't really encode anything

        // Remove any special characters and spaces from the plainText
        plainText = removeSpecialChar(plainText);
        if (railNum > plainText.length()){
            Toast.makeText(context, "Rails specified is greater than length of message, will not encode properly", Toast.LENGTH_LONG).show();
        }

        char [][] railArray = new char[railNum][plainText.length()];
        int vertical = 0;
        String traverse = null;

        fillArray(railArray, railNum, plainText.length());


        for (int horizontal = 0; horizontal < plainText.length(); horizontal++){
            railArray[vertical][horizontal] = plainText.charAt(horizontal);

            // Set our switches depending on the condition
            if (vertical == 0){
                traverse = "down";
            } else if (vertical == (railNum - 1)){
                traverse = "up";
            }

            // Then we check out switches to decide how to move
            if(traverse.equals("down")){
                vertical = vertical + 1;
            } else if (traverse.equals("up")){
                vertical = vertical - 1;
            }
        }

        // So now we have an array that should follow the rail structure
        // we just need to read off row by row to get the encoded text
        return readRail(railArray, railNum, plainText.length());
    }

    // Function that takes a string and removes any special characters and spaces
    private static String removeSpecialChar(String plainText){
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < plainText.length(); i++){
            int chDec = (int)plainText.charAt(i);

            if ((chDec >= 65 &&  chDec <= 90) || (chDec >= 97 && chDec <= 122) || (chDec == 32)
                    || (chDec >= 48 && chDec <= 57)){
                newText.append(Character.toString(plainText.charAt(i)));
            }
        }

        return newText.toString();
    }

    private static void fillArray(char[][] arrayIn, int row, int col){
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                arrayIn[i][j] = '*';
            }
        }
    }

    private static String readRail(char[][] arrayIn, int row, int col){
        StringBuilder encodedText = new StringBuilder();

        for (int i = 0; i < row; i ++){
            for (int j = 0; j < col; j++){
                if (arrayIn[i][j] != '*'){
                    encodedText.append(Character.toString(arrayIn[i][j]));
                }
            }
        }

        return encodedText.toString();
    }
}
