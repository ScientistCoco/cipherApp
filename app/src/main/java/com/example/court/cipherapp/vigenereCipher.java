package com.example.court.cipherapp;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class vigenereCipher extends AppCompatActivity {
    Dialog pop_up_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vigenere_cipher);
        final EditText keyInput = findViewById(R.id.keyInput);
        final EditText encodeTextInput = findViewById(R.id.encodeTextInput);
        Button encodeBtn = findViewById(R.id.encodeButton);
        pop_up_result = new Dialog(this);

        encodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
*/
                if (!keyInput.getText().toString().isEmpty()){
                    if (!encodeTextInput.getText().toString().isEmpty()){
                        String result = vigenereCipherEncode(encodeTextInput.getText().toString(), keyInput.getText().toString());

                        pop_up_result.setContentView(R.layout.pop_up_cipher);
                        final TextView vigenereResult = pop_up_result.findViewById(R.id.cipherResult);
                        vigenereResult.setText(result);
                        pop_up_result.show();

                        TextView x_close_btn = pop_up_result.findViewById(R.id.x_close_btn);
                        x_close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pop_up_result.dismiss();
                            }
                        });

                        TextView copy_btn = pop_up_result.findViewById(R.id.copy_text);
                        copy_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("text", vigenereResult.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                Toast.makeText(vigenereCipher.this, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplication(), "Please enter a key to use", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplication(), "Please enter text to encode", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private static String vigenereCipherEncode(String plainText, String key){
        // We will remove all special characters including spaces, then at the end we will
        // add make each word 5 characters long
        plainText = convert_to_lowercase(plainText);
        key = convert_to_lowercase(key);
        StringBuilder encryptedText = new StringBuilder();
        char encryptedLetter = '\0';
        int keywordIndex = 0;
        int decPlainText = 0;
        int decKeyword = 0;

        for (int i = 0; i < plainText.length(); i++){
            keywordIndex = i % key.length();
            decPlainText = (int)plainText.charAt(i) - 97;
            decKeyword = (int)key.charAt(keywordIndex) - 97;

            encryptedLetter = (char)((decPlainText + decKeyword)%26 + 97);

            encryptedText.append(Character.toString(encryptedLetter));
        }

        return convert_to_block(encryptedText.toString());
    }

    private static String convert_to_lowercase(String plainText){
        StringBuilder newText = new StringBuilder();
        int decChar = 0;

        for (int i = 0; i < plainText.length(); i ++){
            decChar = (int)plainText.charAt(i);

            // Converting uppercase to lowercase letters
            if (decChar >= 65 && decChar <= 90){
                decChar = (decChar - 65) + 97;
            }

            // Now checking that it is lowercase or a space character before
            // we append
            if ((decChar >= 97 && decChar <= 122)){
                newText.append(Character.toString((char)(decChar)));
            }
        }
        return newText.toString();
    }

    private static String convert_to_block(String text){
        // Five letter block
        StringBuilder newText = new StringBuilder();

        for (int i = 0; i < text.length(); i ++){
            if (i != 0 && i % 5 == 0){
                newText.append(' ');
            }
            newText.append(Character.toString(text.charAt(i)));
        }

        return newText.toString();
    }
}
