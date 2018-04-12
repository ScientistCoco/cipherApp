package com.example.court.cipherapp;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProvider;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

// Just a general class for showing a pop up box to display the desired text
public class PopUpHelper {
    private final Context mContext;
    private Dialog myDialog;
    private static ContentProvider mContent;
    private TextView cipherResult;

    public PopUpHelper(Context context){
        this.mContext = context;
        this.myDialog = new Dialog(mContext);
        myDialog.setContentView(R.layout.pop_up_cipher);
        this.cipherResult = myDialog.findViewById(R.id.cipherResult);

        copy_function();
        close_popup();
    }

    public void copy_function(){
        TextView copy_btn = myDialog.findViewById(R.id.copy_text);
        final TextView cipher_result = myDialog.findViewById(R.id.cipherResult);

        copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)mContext.getSystemService(mContext.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", cipher_result.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void set_and_show_box(String result){
        this.cipherResult.setText(result);
        this.cipherResult.setMovementMethod(new ScrollingMovementMethod());
        myDialog.show();
    }

    public void close_popup(){
        TextView x_close = myDialog.findViewById(R.id.x_close_btn);
        x_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });
    }
}
