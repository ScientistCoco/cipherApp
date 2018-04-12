package com.example.court.cipherapp;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Scanner;

public class CaesarDecode extends AppCompatActivity{
    private static EditText cipherText;
    private static DatabaseHelper db;
    private static Button decode_btn;
    private boolean loadingData = false;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_text);

        decode_btn = findViewById(R.id.decodeButton);
        TextView paste_btn = findViewById(R.id.paste_btn);
        TextView clear_btn = findViewById(R.id.clear_btn);
        cipherText = findViewById(R.id.decodeTextInput);
        progress_bar = findViewById(R.id.progress_bar);
        db = new DatabaseHelper(this);

        decode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cipherText.getText().toString().isEmpty()){
                    Decode_caesar_text task = new Decode_caesar_text();
                    if (loadingData == false) {
                        task.execute(cipherText.getText().toString());
                        decode_btn.setEnabled(false);
                        progress_bar.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(CaesarDecode.this, "Input needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        paste_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cbm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData cd = cbm.getPrimaryClip();
                ClipData.Item item = cd.getItemAt(0);
                cipherText.setText(item.getText().toString());
            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cipherText.setText("");
            }
        });
    }

    //TODO add a loading bar for onProgressUpdate?
    private class Decode_caesar_text extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... cipherText){
            loadingData = true;
            int key = determine_best_key(cipherText[0]);
            return caesar_decode_usingkey(cipherText[0], key);
        }

        @Override
        protected void onPostExecute(String result){
            PopUpHelper pop_up = new PopUpHelper(CaesarDecode.this);
            pop_up.set_and_show_box(result);
            loadingData = false;
            progress_bar.setVisibility(View.INVISIBLE);
            decode_btn.setEnabled(true);
        }
    }
    // Create a method that receives a key and decodes the ciphertext using that key
    private static String caesar_decode_usingkey(String cipherText, int key){
        StringBuilder newText = new StringBuilder();
        int letter = 0;

        for (int i = 0; i < cipherText.length(); i ++){
            // Text will be in lowercase so we use 122 (z) as the modulus
            letter = cipherText.charAt(i);

            if (letter >= 'a' && letter <= 'z') {
                letter = (char)(letter - key);

                if (letter < 'a') {
                    letter = (char)(letter + 'z' - 'a' + 1);
                }

                newText.append((char)letter);
            }

            else if (letter >= 'A' && letter <= 'Z') {
                letter = (char)(letter - key);

                if (letter < 'A') {
                    letter = (char)(letter + 'Z' - 'A' + 1);
                }

                newText.append((char)letter);
            }
            else {
                newText.append((char)letter);
            }
        }


        return newText.toString();
    }

    private int determine_best_key(String cipherText){
        int bestKey = 1;
        double result = 0;

        // Find how many words there are in the text so we can determine which bigram analysis to use
        int ngramKey = cipherText.split(" ").length < 50 ? 5 : 4;

        String converted_text = convert_to_lowercase(cipherText);
        // We take the first shift as the starting point
        String shiftedText = caesar_decode_usingkey(converted_text, bestKey);

        double bestFitness = calculate_log_probability(get_quadgrams(shiftedText, ngramKey), ngramKey);
        for (int i = 2; i <= 26; i ++){
            shiftedText = caesar_decode_usingkey(converted_text, i);
            result = calculate_log_probability(get_quadgrams(shiftedText, ngramKey), ngramKey);
            if (result > bestFitness){
                bestFitness = result;
                bestKey = i;
            }
        }

        return bestKey;
    }

    private static String convert_to_lowercase(String plainText){
        StringBuilder newText = new StringBuilder();
        int decChar = 0;

        for (int i = 0 ; i < plainText.length(); i++){
            decChar = (int)plainText.charAt(i);

            if (decChar >= 65 && decChar <= 90){
                decChar = (decChar - 65) + 97;
            }

            if ((decChar >= 97 && decChar <= 122)){
                newText.append(Character.toString((char)(decChar)));
            }
        }

        return newText.toString();
    }

    private static Integer count_occurences(String plainText, String substring){
        int count = 0;
        int fromIndex = 0;

        while ((fromIndex = plainText.indexOf(substring, fromIndex)) != -1){
            count++;
            fromIndex++;
        }

        return count;
    }

    private static HashMap<String, Integer> get_quadgrams(String plainText, int n){
        //String[] ngram_result = new String[plainText.length() * n];
        String x;
        HashMap<String, Integer> y = new HashMap<String, Integer>();
        //int count_grams = 0;

        for (int i = 0; i + n <= plainText.length(); i = i + n){
            x = plainText.substring(i, i + n);
            y.put(x, count_occurences(plainText, x));
            //count_grams++;
        }

        return y;
    }

    private double get_quadgram_count(String word, int ngram){
        // Function receives a quadgram and finds the word in the file
        // then returns its count number
        double count = 0;
        // Can actually do a check to make sure the word is 4 characters long to
        // save having to do the next step
        if (word.length() != ngram){
            return count;
        }
        count = db.getFrequency(word);
        return count;
    }

    // Now we use the hashmap and the quadgram count to get the overall fitness
    private double calculate_log_probability(HashMap<String, Integer> a, int ngram){
        double overall_result = 0;
        for (String key : a.keySet()){
            // Formula is P(word) = count(word)/N
            // where count(word) comes from the hashmap value
            // N comes from get_quadgram_count();

            double n = get_quadgram_count(key, ngram);
            // Suppose we get n = 0 then this means that it is extermely unlikely
            // that this text is found in the english text
            // plus if we use our formula count(word)/N where n = 0
            // we would get an error so its best to jump out of this for loop
            // and return result = -infinity
            if (n == 0) {
                overall_result = Double.NEGATIVE_INFINITY;
                break;
            }
            overall_result = overall_result + Math.log10((double)a.get(key)/n);
        }
        return overall_result;
    }
}
