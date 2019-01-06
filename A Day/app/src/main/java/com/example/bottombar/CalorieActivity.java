package com.example.bottombar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CalorieActivity extends AppCompatActivity {
    private TextView tv_show;
    private TextView tv_num;
    private ImageView back;

    private double cola_cal=258;//一瓶可乐的热量为258kcal

    private void assignViews() {
        tv_num = (TextView) findViewById(R.id.tv_num);
        tv_show = (TextView) findViewById(R.id.tv_show);
        back=(ImageView) findViewById(R.id.back);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie);

        assignViews();

        Intent intent=getIntent();
        int step=intent.getIntExtra("step",0);
        double cal=step*0.04;
        double num=cal/cola_cal;
        num=(double) Math.round(num * 10) / 10;
        cal=(double) Math.round(cal * 100) / 100;
        tv_num.setText(num+" 杯快乐水");
        tv_show.setText(cal+"");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
