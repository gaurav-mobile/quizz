package com.gaurav.quizz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.gaurav.quizz.databinding.ActivityResultBinding;


public class ResultActivity extends AppCompatActivity {

    ActivityResultBinding resultBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultBinding = DataBindingUtil.setContentView(this, R.layout.activity_result);

        int score = getIntent().getIntExtra("RIGHT_ANSWER_COUNT", 0);
        SharedPreferences settings = getSharedPreferences("quizzler", Context.MODE_PRIVATE);
        int totalScore = settings.getInt("totalScore", 0);
        totalScore +=score;

        resultBinding.resultLabel.setText(score +" / 5");
        resultBinding.totalScoreLabel.setText("Total Score: "+totalScore);

        //Update total score
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("totalScore", totalScore);
        editor.commit();
    }


    public void returnMain(){

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
