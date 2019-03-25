package com.example.bottombar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


import com.example.bottombar.step.service.StepService;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;
import java.util.List;

public class LinechartActivity extends AppCompatActivity{
    private LineChart lineChart;
    private ImageView change_btn;
    private ImageView back_btn;

    private boolean step_flag;

    String[] times = new String[]{"0点","2点","6点","10点","14点","18点","22点"};
    Integer[] CaldataArray ={0,0,0,0,0,0,0};
    Integer[] StepdataArray ={0,0,0,0,0,0,0};
    List<Entry> mValues = new ArrayList<>();

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
                    step_flag=false;
                    setCalData();
                    change_btn.setImageResource(R.drawable.cal_icon);
                }
                else {
                    step_flag=true;
                    setStepData();
                    change_btn.setImageResource(R.drawable.walk_icon);
                }
            }
        });
        initData();
    }

    private void assignViews() {
        lineChart=(LineChart) findViewById(R.id.lineView);
        change_btn = (ImageView) findViewById(R.id.change_btn);
        back_btn = (ImageView) findViewById(R.id.back_btn);
    }

    private void initDraw(){
        lineChart.clear();
        Description description = new Description();
        description.setEnabled(false);
        lineChart.setDescription(description);
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(times);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(false);
        leftAxis.setStartAtZero(true);

        YAxis axisRight = lineChart.getAxisRight();
        axisRight.setEnabled(false);

        lineChart.animateX(500);
    }

    public void drawData(String s){
        initDraw();

        LineDataSet set1 = new LineDataSet(mValues, s);
        set1.setDrawCircleHole(false);
        set1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        if(step_flag == true) set1.setColor(Color.GREEN);
        else set1.setColor(Color.RED);
        set1.setDrawFilled(true);
        set1.setHighlightEnabled(false);
        set1.setLineWidth(2f);
        set1.setValueTextSize(15f);
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "" + (int) value;
            }
        });

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1); // add the datasets
        //创建LineData对象 属于LineChart折线图的数据集合
        LineData data = new LineData(dataSets);

        lineChart.setData(data);
        //绘制图表
        lineChart.invalidate();
    }

    public void initData()
    {
        double d;
        StepdataArray[0]=CaldataArray[0]=0;
        for(int i=1;i<7;i++) {
            StepdataArray[i] = StepService.TIME_STEP[i] - StepService.TIME_STEP[i-1];
            if(StepdataArray[i]<0) StepdataArray[i]=CaldataArray[i]=0;
            else
            {
                d=StepdataArray[i]*0.04;
                int k=(int) Math.round(d);
                CaldataArray[i]=k;
            }
        }
        step_flag=true;
        setStepData();
    }

    public void setStepData()
    {
        mValues.clear();
        for(int i=0;i<7;i++) {
            mValues.add(new Entry(i, StepdataArray[i]));
        }
        drawData("步数（步）");
    }

    public void setCalData()
    {
        mValues.clear();
        for(int i=0;i<7;i++) {
            mValues.add(new Entry(i, CaldataArray[i]));
        }
        drawData("卡路里（cal）");
    }
}
