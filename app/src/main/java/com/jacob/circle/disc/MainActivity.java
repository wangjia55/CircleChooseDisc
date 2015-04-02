package com.jacob.circle.disc;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;


public class MainActivity extends FragmentActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView);

        CircleIndexView circleIndexView = (CircleIndexView) findViewById(R.id.circle_index_view);
        circleIndexView.setOnIndexChangeListener(new CircleIndexView.OnIndexChangeListener() {
            @Override
            public void onIndexChange(String letter) {
                mTextView.setText(letter);
            }
        });
        circleIndexView.setIndexLetter("G");
        mTextView.setText("G");


    }

}
