package com.example.court.cipherapp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

// Notes:
// If a user puts in numbers should these numbers also be shifted?

public class caesarCipher extends AppCompatActivity {
    Dialog myDialog;
    TextView shiftInput;
    EditText encodeTextInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caesar_cipher);
        // Get the main xml objects
        shiftInput = findViewById(R.id.shiftInput);
        encodeTextInput = findViewById(R.id.encodeTextInput);
        Button encodeBtn = findViewById(R.id.encodeButton);

        SeekBar caesarSeek = findViewById(R.id.caesarSeek);
        myDialog = new Dialog(this);

        caesarSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int min = 1;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                shiftInput.setText(Integer.toString(i+min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        encodeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Checking that the user has put in text and has put in a shift value to avoid
                // the program crashing
                // Dont do encodeTextInput.getText() != null as it will result in NullPointerException
                if (!encodeTextInput.getText().toString().isEmpty() & !shiftInput.getText().toString().isEmpty()) {
                    int shiftValue = Integer.parseInt(shiftInput.getText().toString());
                    String textInput = encodeTextInput.getText().toString();
                    String result = caesarEncode(textInput, shiftValue);

                    myDialog.setContentView(R.layout.pop_up_cipher);
                    TextView caesarResult = myDialog.findViewById(R.id.cipherResult);
                    caesarResult.setText(result);
                    myDialog.show();

                    TextView x_close = myDialog.findViewById(R.id.x_close_btn);
                    x_close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                        }
                    });

                } else {
                    Toast.makeText(getApplication(), "You need to put in an input!", Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private static String caesarEncode(String text, int shift){
        StringBuilder encodedString = new StringBuilder();
        int newChDec = 0;
        for (int i = 0; i < text.length(); i++){
            int chDec = (int)text.charAt(i);
            if (chDec >= 65 && chDec <= 90) {
                newChDec = ((chDec - 65) + shift) % 26;
                encodedString.append(Character.toString((char) (newChDec + 65)));
            } else if (chDec >= 97 && chDec <= 122){
                newChDec = ((chDec - 97) + shift) % 26;
                encodedString.append(Character.toString((char) (newChDec + 97)));
            } else {
                encodedString.append(Character.toString((char) (chDec)));
            }
        }
        return encodedString.toString();
    }
}
