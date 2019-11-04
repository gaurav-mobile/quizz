package com.gaurav.quizz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.gaurav.quizz.databinding.LayoutQuestionsBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends Activity {
    static final long START_TIME_IN_MILIS = 60000;
    static final private int QUIZ_COUNT = 5;
    ProgressBar mProgressBar;
    int PROGRESS_BAR_INCREMENT = 100 / QUIZ_COUNT;
    ArrayList<HashMap<String, String>> formList;
    private TextView countLabel;
    private TextView questionLabel;
    private TextView quizTimer;
    private Button answerButton1;
    private Button answerButton2;
    private Button answerButton3;
    private Button answerButton4;
    private CountDownTimer mCountDownTimer;
    private String rightAnswer;
    private int rightAnswerCount = 0;
    private int quizCount = 1;
    private long mTimeLeftinMillis = START_TIME_IN_MILIS;
    AlertDialog alert;
    List<Quiz> quizModelList;
    LayoutQuestionsBinding binding;
    Quiz quiz;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.layout_questions);

        startTimer();


        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray m_jArry = obj.getJSONArray("questions");
            formList = new ArrayList<HashMap<String, String>>();
            ArrayList<String> tmpArray = new ArrayList<>();
            HashMap<String, String> m_li;
            quizModelList = new ArrayList<>();

            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);

                String question = jo_inside.getString("question");
                String answer = jo_inside.getString("answer");
                String choice_1 = jo_inside.getString("choice1");
                String choice_2 = jo_inside.getString("choice2");
                String choice_3 = jo_inside.getString("choice3");
                String choice_4 = jo_inside.getString("choice4");

                quizModelList.add(new Quiz(question,answer,choice_1,choice_2,choice_3,choice_4));

            }

           // binding.setQuiz(quiz);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        showNextQuiz();
    }



    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftinMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftinMillis = millisUntilFinished;
                binding.tvTimer.setText(getString(R.string.time) + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText(getString(R.string.done));
            }
        }.start();

    }


    public void showNextQuiz() {

        //Update quizCountLabel
        binding.tvLabel.setText("Q" + quizCount);
        //set question and right answer.
        binding.tvQuestion.setText(quizModelList.get(0).getQuestion());
        rightAnswer = quizModelList.get(0).getAnswer();


        //set choices
        binding.btnAnswer1.setText(quizModelList.get(0).getChoice1());
        binding.btnAnswer2.setText(quizModelList.get(0).getChoice2());
        binding.btnAnswer3.setText(quizModelList.get(0).getChoice3());
        binding.btnAnswer4.setText(quizModelList.get(0).getChoice4());
        quizModelList.remove(0);
    }

    public void checkAnswer(View view) {

        //Get pushed button
        Button answerBtn = (Button) findViewById(view.getId());
        String btnText = answerBtn.getText().toString();

        String alertTitle;

        if (btnText.equals(rightAnswer)) {

            //correct!
            alertTitle = "Your answer is correct";
            rightAnswerCount++;
        } else {
            //wrong
            alertTitle = "Your answer is wrong...";
        }


        //alert handler
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);


        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_alert, null);
        builder.setView(dialogView);

        TextView textViewMessage = dialogView.findViewById(R.id.tv_alert);
        Button buttonOk = dialogView.findViewById(R.id.btn_ok);

        textViewMessage.setText(alertTitle);

        final AlertDialog show = builder.show();

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (quizCount == QUIZ_COUNT) {
                    //show Result
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    binding.progressBar.setProgress(0);
                    intent.putExtra("RIGHT_ANSWER_COUNT", rightAnswerCount);
                    startActivity(intent);
                } else {
                    quizCount++;
                    binding.progressBar.incrementProgressBy(PROGRESS_BAR_INCREMENT);
                    showNextQuiz();
                }

                show.dismiss();

            }
        });

        builder.setCancelable(false);

    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("questions.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}


