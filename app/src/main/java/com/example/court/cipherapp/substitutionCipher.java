package com.example.court.cipherapp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class substitutionCipher extends AppCompatActivity {
    Dialog pop_up_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_substitution_cipher);
        // Get the main xml objects
        final EditText keyInput = findViewById(R.id.keyInput);
        final EditText encodeTextInput = findViewById(R.id.encodeTextInput);
        Button encodeBtn = findViewById(R.id.encodeButton);
        pop_up_box = new Dialog(this);

        encodeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Check first that the user has put in something to encode
                if (!encodeTextInput.getText().toString().isEmpty()){
                    // Now we check if the user has put in a key for us to use
                    // if not then we have to generate one for them
                    if (keyInput.getText().toString().isEmpty()){
                        keyInput.setText(generateKey());
                        Toast.makeText(getApplicationContext(), "Key randomly generated for you", Toast.LENGTH_SHORT).show();
                    }
                    // If the user enters a key we need to check that the key they entered is unique and
                    // contains 26 letters
                    if (check_unique_key(keyInput.getText().toString()) == false){
                        Toast.makeText(getApplicationContext(), "The key you entered contains duplicate letters or is not 26 characters long", Toast.LENGTH_SHORT).show();
                    } else {
                        String encodedText = encrypt(encodeTextInput.getText().toString(), keyInput.getText().toString());

                        pop_up_box.setContentView(R.layout.pop_up_cipher);
                        TextView subResult = pop_up_box.findViewById(R.id.cipherResult);
                        subResult.setText(encodedText);
                        pop_up_box.show();

                        TextView x_close = pop_up_box.findViewById(R.id.x_close_btn);
                        x_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pop_up_box.dismiss();
                            }
                        });
                    }
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

    private static Boolean check_unique_key (String key){
        HashSet<Character> _set = new HashSet<>();

        // Check the length of key is at 26 characters:
        if (key.length() != 26){
            return false;
        }

        for (int i = 0; i < key.length(); i++){
            if (_set.add(key.charAt(i)) == false){
                return false;
            }
        }
        return true;
    }

    private static String generateKey(){
        int size = 26; // the amount of letters we have in the alphabet
        ArrayList<Integer> list = new ArrayList<>(size);
        ArrayList<Integer> randList = new ArrayList<>();
        StringBuilder key = new StringBuilder();

        // We generate a list with numbers from 0-26
        for (int i = 0; i < size; i++){
            list.add(i);
        }

        // Then we randomly pick an index from 'list' and take the number in that index and append
        // it to randList, so we get a list with numbers randomly ordered
        Random rand = new Random();
        while (list.size() > 0){
            int index = rand.nextInt(list.size());
            randList.add(list.get(index));
            list.remove(index);
        }

        // Now using that randomly ordered array we correspond the numbers to an ASCII code
        // hence creating a random key
        // e.x. if the array is like [4, 3, 17, 2...] then it would have the key [d, c, q, b...]

        int chDec = 0;
        for (int i = 0; i < size; i++){
            chDec = randList.get(i) + (int)'a';
            key.append(Character.toString((char)chDec));
        }

        return key.toString();
    }

    private static String encrypt(String plainText, String key){
        // Function inputs:
        // plainText = the text that the user wants to encode
        // key = the key that the user wants to use to encode the text
        StringBuilder encodedString = new StringBuilder();

        // We get the plainText and if it contains caps then we should convert it to lowercase
        // from there we match it with the correct index
        int chDec = 0;
        char newCh = '\0';

        for (int i = 0; i < plainText.length(); i++){
            chDec = (int)plainText.charAt(i);

            if (chDec >= 65 && chDec <= 90){
                chDec = chDec - 65;
            } else if (chDec >= 97 && chDec <= 122){
                chDec = chDec - 97;
            }

            // Now we get a number in chDec that should correspond to one of the index's in the
            // key array, so it should be a number between 0 and 26, if not then we use the
            // normal ascii key to encode
            if (chDec >= 0 && chDec < 26){
                newCh = key.charAt(chDec);
            } else {
                newCh = plainText.charAt(i);
            }

            encodedString.append(newCh);
        }

        return encodedString.toString();
    }
}
