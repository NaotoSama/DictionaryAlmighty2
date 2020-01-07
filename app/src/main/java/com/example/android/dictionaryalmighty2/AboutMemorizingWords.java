package com.example.android.dictionaryalmighty2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutMemorizingWords extends AppCompatActivity {

    TextView forgettingCurveText1;
    TextView forgettingCurveText2;

    ImageView forgettingCurve1;
    ImageView forgettingCurve2;


//==============================================================================================
// onCreate
//==============================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_memorizing_words);

        forgettingCurveText1 = findViewById(R.id.forgetting_curve_textView_1);
        forgettingCurveText2 = findViewById(R.id.forgetting_curve_textView_2);
        forgettingCurve1 = findViewById(R.id.forgetting_curve_1);
        forgettingCurve2 = findViewById(R.id.forgetting_curve_2);
    }


}
