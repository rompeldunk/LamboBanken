package com.yelobase.firebase;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import info.androidhive.firebase.MainActivity;
import info.androidhive.firebase.R;

public class Splash extends AppCompatActivity {

    private TextView tv;
    private ImageView iv;

    // Random Startup-quote
    String[] list = {
            "Because your worth it",
            "Når du føler deg lavest",
            "Ska æ' fett dæ rjett nee?"
    };
    Random r = new Random();
    String funnyQuote = list[r.nextInt(list.length)];


    int[] imageIds = {
            R.drawable.lambobanken1,
            R.drawable.lambobanken2,
            R.drawable.lambobanken3
    };
    Random generator = new Random();
    int randomImageId = imageIds[generator.nextInt(imageIds.length)];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        TextView ssid = (TextView) findViewById (R.id.tv);
        ssid.setText(funnyQuote);


        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.iv);

        iv.setImageResource(randomImageId);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);


        tv.startAnimation(myanim);
        iv.startAnimation(myanim);




        final Intent i  = new Intent(this, LoginActivity.class);
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(5000);

                }
                catch (InterruptedException e){
                    e.printStackTrace();

                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
