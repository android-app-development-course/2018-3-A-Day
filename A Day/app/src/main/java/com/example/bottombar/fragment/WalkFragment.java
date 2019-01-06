package com.example.bottombar.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.example.bottombar.CalorieActivity;
import com.example.bottombar.HistoryActivity;
import com.example.bottombar.LinechartActivity;
import com.example.bottombar.MainActivity;
import com.example.bottombar.PlanActivity;
import com.example.bottombar.R;
import com.example.bottombar.step.utils.SharedPreferencesUtils;
import com.example.bottombar.view.LineView;
import com.example.bottombar.view.WaveProgressView;


public class WalkFragment extends Fragment{
    private WaveProgressView waveProgressView;
    private TextView tv_show;

    private int stepCount;
    private int planWalk;

    private SharedPreferencesUtils sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_walk, container, false);

        waveProgressView = view.findViewById(R.id.aa);
        tv_show = view.findViewById(R.id.tv_StepCount);

        sp = new SharedPreferencesUtils(getActivity());

        initData();

        Bundle bundle =this.getArguments();//得到从Activity传来的数据
        if(bundle!=null){
            stepCount=bundle.getInt("stepCount");
            setCurrentPercent();
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ImageView iv_history = (ImageView) getActivity().findViewById(R.id.iv_history);
        iv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从fragment跳转到activity中
                startActivity(new Intent(getActivity(), HistoryActivity.class));
            }
        });

        ImageView iv_chart = (ImageView) getActivity().findViewById(R.id.iv_chart);
        iv_chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LinechartActivity.class));
            }
        });

        ImageView iv_plan = (ImageView) getActivity().findViewById(R.id.iv_plan);
        iv_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), PlanActivity.class);
                intent.putExtra("planWalk",planWalk);
                startActivityForResult(intent,1);
            }
        });

        ImageView iv_cal = (ImageView) getActivity().findViewById(R.id.iv_calorie);
        iv_cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), CalorieActivity.class);
                intent.putExtra("step",stepCount);
                startActivity(intent);
            }
        });

        waveProgressView = getActivity().findViewById(R.id.aa);
        waveProgressView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                alert_edit();
            }
        });
    }


    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==1){
            if(resultCode==1){
                String s=data.getStringExtra("plan");
                save_planWalk(s);
            }
        }
    }


    private void initData() {
        //获取用户设置的计划锻炼步数，没有设置过的话默认7000
        planWalk=Integer.parseInt(get_planWalk());
        //设置当前步数为0
        stepCount=0;
        tv_show.setText(stepCount+"");
        waveProgressView.setProgressNum(0, 1200);
    }

    public void setCurrentPercent()
    {
        int CurrentPercent=stepCount*100/planWalk;
        waveProgressView.setProgressNum(CurrentPercent, 1400);
        tv_show.setText(stepCount+"");
    }

    public void alert_edit(){
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog, null);
        final EditText input = (EditText) view.findViewById(R.id.input);
        input.setText(get_planWalk());
        final Button yes = (Button) view.findViewById(R.id.yes_btn);
        final Button no = (Button) view.findViewById(R.id.no_btn);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        final AlertDialog dlg=builder.show();
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=input.getText().toString();
                if (s.isEmpty() || Integer.parseInt(s)==0)
                {
                    s="6000";
                }
                save_planWalk(s);
                dlg.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
    }

    public void save_planWalk(String s)
    {
        sp.setParam("planWalk_QTY",s);
        planWalk=Integer.parseInt(s);
        setCurrentPercent();
    }

    public String get_planWalk()
    {
        String s = (String) sp.getParam("planWalk_QTY", "7000");
        return s;
    }

}

