package com.example.court.cipherapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

public class CaesarDecode extends AppCompatActivity{
    private static EditText cipherText;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decode_text);

        Button decode_btn = findViewById(R.id.decodeButton);
        cipherText = findViewById(R.id.decodeTextInput);

        //TODO make a progress bar, asynctask that runs in the background so the app doesn't crash while doing calculations
        decode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!cipherText.getText().toString().isEmpty()){
                    Log.i("Received", cipherText.getText().toString());
                    try {
                        int key = determine_best_key(cipherText.getText().toString());
                    }
                    catch (IOException e){
                        Log.i("Error", "problem reading the file");
                    }
                } else {
                    Toast.makeText(CaesarDecode.this, "Input needed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    private int determine_best_key(String cipherText) throws FileNotFoundException {
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
            Log.i("result_calculated", Double.toString(result));
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

    private double get_quadgram_count(String word, int ngram) throws FileNotFoundException{
        // Function receives a quadgram and finds the word in the file
        // then returns its count number
        double count = 0;
        Scanner input = null;
        // Can actually do a check to make sure the word is 4 characters long to
        // save having to do the next step
        if (word.length() != ngram){
            return count;
        }

        if (ngram == 4) {
            try {
                DataInputStream textFileStream = new DataInputStream(getAssets().open(String.format("english_quadgrams.txt")));
                input = new Scanner(textFileStream);
            }
            catch (IOException e){

            }
        } else {
            try {
                DataInputStream textFileStream = new DataInputStream(getAssets().open(String.format("english_quintgrams.txt")));
                input = new Scanner(textFileStream);
            }
            catch (IOException e){

            }
        }

        String str = input.next();
        while(input.hasNext()){
            if (str.equalsIgnoreCase(word) == true){
                count = Double.parseDouble(input.next());
                break;
            }
            str = input.next();
        }
        input.close();

        return count;
    }

    // Now we use the hashmap and the quadgram count to get the overall fitness
    private double calculate_log_probability(HashMap<String, Integer> a, int ngram) throws FileNotFoundException {
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
