package com.example.bottombar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PlanActivity extends AppCompatActivity {
    private EditText ed_input;
    private Button set_btn;
    private ImageView back;

    private int plan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        ed_input=findViewById(R.id.ed_planStep);
        set_btn=findViewById(R.id.set_btn);
        back=findViewById(R.id.back_btn);

        final Intent intent=getIntent();
        plan=intent.getIntExtra("planWalk",6000);
        ed_input.setText(plan+"");

        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s=ed_input.getText().toString();
                if (s.isEmpty() || Integer.parseInt(s)==0)
                {
                    s="6000";
                }
                Intent intent1=new Intent();
                intent1.putExtra("plan",s);
                setResult(1,intent1);
                finish();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
