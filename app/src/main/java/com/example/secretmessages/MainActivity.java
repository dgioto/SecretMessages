package com.example.secretmessages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    EditText txtIn;
    EditText txtKey;
    EditText txtOut;
    SeekBar sb;
    Button btn, btn2;

    public  String encode(String message, int keyVal) {
        String output = "";
        char key = (char) keyVal;
        for (int x = 0; x < message.length(); x++){
            char input = message.charAt(x);
            if (input >= 'А' && input <= 'Я'){
                input += key;
                if (input > 'Я') input -= 32;
                if (input < 'А') input += 32;
            } else if (input>= 'а' && input <= 'я') {
                input += key;
                if (input > 'я') input -= 32;
                if (input < 'а') input += 32;
            } else if (input >= '0' && input <= '9'){
                input += (keyVal % 10);
                if (input > '9') input -= 10;
                if (input < '0') input += 10;
            }
            output += input;
         }
        return output;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtIn = (EditText) findViewById(R.id.txtIn);
        txtKey = (EditText) findViewById(R.id.txtKey);
        txtOut = (EditText) findViewById(R.id.txtOut);
        sb = (SeekBar) findViewById(R.id.seekBar);
        btn = (Button) findViewById(R.id.button);
        btn2 = (Button) findViewById(R.id.button2);

        //получение секретных сообщений из других приложений
        Intent receivedIntent = getIntent();
        String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
        if (receivedText != null){
            txtIn.setText(receivedText);
        }

        //настраиваем слушатель кнопки ENCODE/DECODE
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int key = (Integer.parseInt(txtKey.getText().toString())) * (-1);
                String message = txtIn.getText().toString();
                String output = encode(message, key);
                txtOut.setText(output);
            }
        });

        //настраиваем слушатель кнопки MoveUp
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int key = Integer.parseInt(txtKey.getText().toString());
                String message = txtOut.getText().toString();
                String inPut = encode(message, key);
                txtIn.setText(inPut);
            }
        });

        //сохраняем компонент SeekBar в переменной sb
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int key = sb.getProgress() - 13;
                String message = txtIn.getText().toString();
                String output = encode(message, key);
                txtOut.setText(output);
                txtKey.setText("" + key);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //всплывающая кнопка действи
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Secret Message" +
                        DateFormat.getDateTimeInstance().format(new Date()));
                shareIntent.putExtra(Intent.EXTRA_TEXT, txtOut.getText().toString());
                try {
                    startActivity(Intent.createChooser(shareIntent, "Share message..."));
                    finish();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Error; Couldn't share.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}