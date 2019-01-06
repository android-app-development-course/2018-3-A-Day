package com.example.bottombar;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.bottombar.view.LineView;

public class LinechartActivity extends AppCompatActivity{
    private LineView lineChart;
    private ImageView change_btn;
    private ImageView back_btn;

    private boolean step_flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linechart);

        assignViews();

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        change_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(step_flag) {
                    lineChart.setCalData();
                    step_flag=false;
                    change_btn.setImageResource(R.drawable.cal_icon);
                }
                else {
                    lineChart.setStepData();
                    step_flag=true;
                    change_btn.setImageResource(R.drawable.walk_icon);
                }
            }
        });
        initData();
    }

    private void assignViews() {
        lineChart=(LineView) findViewById(R.id.lineView);
        change_btn = (ImageView) findViewById(R.id.change_btn);
        back_btn = (ImageView) findViewById(R.id.back_btn);
    }

    private void initData(){
        lineChart.initData();
        lineChart.setStepData();
        step_flag=true;
    }
}
