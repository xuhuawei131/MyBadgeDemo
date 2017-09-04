package com.lingdian.mybadgedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MyBadgeView myBadgeView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myBadgeView=(MyBadgeView)findViewById(R.id.myBadgeView);
        myBadgeView.setBadgeNum(9);
        myBadgeView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        myBadgeView.setBadgeNum(199);
    }
}
