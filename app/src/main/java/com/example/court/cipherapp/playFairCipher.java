package com.example.court.cipherapp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashSet;
import java.util.Set;

public class playFairCipher extends AppCompatActivity {
    Dialog pop_up_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_fair_cipher);

        pop_up_result = new Dialog(this);
        final EditText key = findViewById(R.id.keyInput);
        final EditText message = findViewById(R.id.encodeTextInput);
        Button btn = findViewById(R.id.encodeButton);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide the keyboard as soon as the user presses the button
                if (!message.getText().toString().isEmpty()){
                    if (!key.getText().toString().isEmpty()){
                        String result = playfairCipher(message.getText().toString(), (create_key_table(key.getText().toString())));

                        pop_up_result.setContentView(R.layout.pop_up_cipher);
                        TextView playFairResult = pop_up_result.findViewById(R.id.cipherResult);
                        playFairResult.setText(result);

                        pop_up_result.show();

                        TextView close_btn = pop_up_result.findViewById(R.id.x_close_btn);
                        close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pop_up_result.dismiss();
                            }
                        });
                    }
                    else {
                        Toast.makeText(getApplication(), "Please put a key in", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplication(),"Please put a message to encode" , Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private static String playfairCipher(String msg, char[][] table){
        msg = string_convert(msg);
        char[][] pairLetters = diagraph(msg);
        int[] firstLetter = new int[2];
        int[] secondLetter = new int[2];
        char[] result = new char[2];
        StringBuilder encrypted_text = new StringBuilder();

        for (int i = 0; i < Math.ceil(msg.length()/2); i++){
            firstLetter = findPosition(pairLetters[i][0], table);
            secondLetter = findPosition(pairLetters[i][1], table);

            // Check if they lie in the same row
            if (firstLetter[0] == secondLetter[0]){
                // Call function for horizontal find
                result = row_pick(table, firstLetter, secondLetter);
            }
            // Check if they lie in the same column
            else if (firstLetter[1] == secondLetter[1]){
                // Call function for vertical find
                result = column_pick(table, firstLetter, secondLetter);
            }
            // Neither in the same column or row
            else {
                // Call function for rectangle find
                result = rectangle_pick(table, firstLetter, secondLetter);
            }

            encrypted_text.append(Character.toString(result[0]));
            encrypted_text.append(Character.toString(result[1]));
        }
        return encrypted_text.toString();
    }

    private static char[] column_pick(char[][] table, int[] firstLetter, int[] secondLetter){
        // If both letters are in the same column, take the letter below each one (going back to the top if at the bottom)
        char[] letter_result = new char[2];

        // Start with the first letter:
        int col_letter = firstLetter[0];
        // Increment the row by 1 and do mod of 5 incase it goes beyond, hence going
        // back to the top:
        col_letter = (col_letter + 1)%5;

        // Add the resulting character to our letter_result array
        letter_result[0] = table[col_letter][firstLetter[1]];

        // Do the same with the second letter:
        col_letter = secondLetter[0];
        col_letter = (col_letter + 1)%5;
        letter_result[1] = table[col_letter][secondLetter[1]];

        return letter_result;
    }

    private static char[] row_pick(char[][] table, int[] firstLetter, int[]secondLetter){
        // If both letters are in the same row, take the letter to the right of each one (going back to the left if at the farthest right)
        char[] letter_result = new char[2];

        // Start with the first letter:
        int row_letter = firstLetter[1];
        // Increment the col by 1 and do mod of 5 incase it goes beyond, hence going
        // back to the top:
        row_letter = (row_letter + 1)%5;

        // Add the resulting character to our letter_result array
        letter_result[0] = table[firstLetter[0]][row_letter];

        // Do the same with the second letter:
        row_letter = secondLetter[1];
        row_letter = (row_letter + 1)%5;
        letter_result[1] = table[secondLetter[0]][row_letter];

        return letter_result;
    }

    private static char[] rectangle_pick(char[][]table, int[] firstLetter, int[] secondLetter){
        char[] letter_result = new char[2];

        // So to pick the corners we just swap the row and col around, i.e. if we have the
        // index [0, 0] and [3, 2] then the first letter would be [0, 2] and second letter
        // would be [0, 3]

        letter_result[0] = table[firstLetter[0]][secondLetter[1]];
        letter_result[1] = table[secondLetter[0]][firstLetter[1]];
        return letter_result;
    }

    // Builds a diagraph for the message to be encoded
    private static char[][] diagraph(String msg){
        msg = string_convert(msg);

        // Check if string is an odd length, if it is we append y at the end
        if (msg.length() % 2 == 1){
            StringBuilder newMsg = new StringBuilder(msg);
            newMsg.append('y');
            msg = newMsg.toString();
        }

        int height = msg.length()/2;
        char[][] pairArray = new char[height][2];

        int position = 0;

        for (int i = 0; i < height; i++){
            for (int j = 0; j < 2; j++){
                pairArray[i][j] = msg.charAt(position);
                position++;
            }
        }
        return pairArray;
    }

    // Finds the index for which letter input matches that of the key input
    private static int[] findPosition(char letter, char[][] table){
        int row = 0;
        int col = 0;
        int[] positions = new int[2];

        for (int i = 0; i < 5 ; i++){
            for (int j = 0; j < 5 ; j++){
                if (letter == table[i][j]){
                    row = i;
                    col = j;
                }
            }
        }

        positions[0] = row;
        positions[1] = col;

        return positions;
    }

    // Creates the key table with no duplicates
    private static char[][] create_key_table(String key){
        // First use the key to create the 5x5 keytable
        char[][] keyTable = new char[5][5];

        // We need a string that is of length 25, so check the length of the
        // key, if it is less than 25 then we need to fill it up with remaining
        // letters that are not duplicates
        key = string_convert(key);
        key = removeDuplicates(key);
        key = build_key_table_string(key);

        int row = 0;
        int col = 0;
        for (int i = 0; i < 25; i++){
            if (col == 5){
                col = 0;
                row++;
            }

            keyTable[row][col] = key.charAt(i);
            col++;
        }
        return keyTable;
    }

    // Takes the key input and removes any duplicates
    private static String removeDuplicates(String key){
        Set <Character> key_set = new HashSet<>();
        StringBuilder text = new StringBuilder();

        // We use a set to remove any duplicates
        for (int i = 0; i < key.length(); i++){
            if (key_set.add(key.charAt(i)) == true){
                text.append(key.charAt(i));
            }
        }
        return text.toString();
    }

    // Function that converts to lowercase and removes any special characters
    // which includes numbers
    private static String string_convert(String key){
        StringBuilder text = new StringBuilder();
        int chDec = 0;

        for (int i = 0; i < key.length(); i ++){
            chDec = key.charAt(i);

            if (chDec >= 'A' && chDec <= 'Z'){
                chDec = chDec - 'A' + 'a';
            }

            if (chDec >= 'a' && chDec <= 'z'){
                text.append(Character.toString((char)chDec));
            }
        }

        return text.toString();
    }

    // Function that creates a string of length 25 with no duplicates
    private static String build_key_table_string(String key){
        Set<Character> key_set = new HashSet<>();
        StringBuilder text = new StringBuilder();
        int length_needed = 25 - key.length();
        int starting_letter = 'a';

        // Case when the key is already 25 letters long
        if (length_needed == 0){
            return key;
        }

        for (int i = 0; i < key.length(); i++){
            text.append(key.charAt(i));
            key_set.add(key.charAt(i));
        }

        for (int i = 0; i < length_needed; i++, starting_letter++){
            if (key_set.add((char)starting_letter) == true){
                text.append((char)starting_letter);
            } else {
                i--;
            }

        }

        return text.toString();
    }
}
