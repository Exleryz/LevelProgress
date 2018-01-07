package com.yezhou.progressdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    LevelProgressBar myProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myProgressBar = (LevelProgressBar) findViewById(R.id.progress_bar);
        myProgressBar.setLevels(4);
        String[] texts = {"one", "two", "three", "four"};
        myProgressBar.setStrStrar("zero");
        myProgressBar.setLevelTexts(texts);

        Button button1 = (Button) findViewById(R.id.level1);
        button1.setOnClickListener(this);
        Button button2 = (Button) findViewById(R.id.level2);
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.level3);
        button3.setOnClickListener(this);
        Button button4 = (Button) findViewById(R.id.level4);
        button4.setOnClickListener(this);
        Button btnClear = (Button) findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.level1:
                myProgressBar.setCurrentLevel(1);
                myProgressBar.setAnimInterval(100);
                break;
            case R.id.level2:
                myProgressBar.setCurrentLevel(2);
                myProgressBar.setAnimInterval(100);
                break;
            case R.id.level3:
                myProgressBar.setCurrentLevel(3);
                myProgressBar.setAnimInterval(100);
                break;
            case R.id.level4:
                myProgressBar.setCurrentLevel(4);
                myProgressBar.setAnimInterval(100);
                break;
            case R.id.btn_clear:
                myProgressBar.setCurrentLevel(0);
                myProgressBar.setAnimInterval(100);
                break;
        }
    }
}
