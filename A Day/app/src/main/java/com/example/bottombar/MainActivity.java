package com.example.bottombar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.bottombar.fragment.NoteFragment;
import com.example.bottombar.fragment.WalkFragment;
import com.example.bottombar.step.UpdateUiCallBack;
import com.example.bottombar.step.service.StepService;
import com.example.bottombar.step.utils.SharedPreferencesUtils;


public class MainActivity extends AppCompatActivity {
    private BottomBar bottomBar;
   // private TextView tv_show;
    private SharedPreferencesUtils sp;

    private int stepCount;

    private Boolean isFront;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setContainer(R.id.fl_container)
                .setTitleBeforeAndAfterColor("#999999", "#7ecef4")
                .addItem(WalkFragment.class,
                        "步数",
                        R.drawable.shoe1,
                        R.drawable.shoe2)
                .addItem(NoteFragment.class,
                        "记事",
                        R.drawable.note1,
                        R.drawable.note2)
                .build();

     //   tv_show=findViewById(R.id.tv_show);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        isFront = true;
        bottomBar.setCurrentStep(stepCount);
    }

    @Override
    public void onPause() {
        super.onPause();
        isFront = false;
    }

    private void initData() {
        sp = new SharedPreferencesUtils(this);

        //设置当前步数为0
        stepCount=0;
     //   tv_show.setText(stepCount+"");
        bottomBar.setCurrentStep(stepCount);
        setupService();
    }


    private boolean isBind = false;

    /**
     * 开启计步服务
     */
    private void setupService() {
        Intent intent = new Intent(this, StepService.class);
        isBind = bindService(intent, conn, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    /**
     * 用于查询应用服务（application Service）的状态的一种interface，
     * 更详细的信息可以参考Service 和 context.bindService()中的描述，
     * 和许多来自系统的回调方式一样，ServiceConnection的方法都是进程的主线程中调用的。
     */
    ServiceConnection conn = new ServiceConnection() {
        /**
         * 在建立起于Service的连接时会调用该方法，目前Android是通过IBind机制实现与服务的连接。
         * @param name 实际所连接到的Service组件名称
         * @param service 服务的通信信道的IBind，可以通过Service访问对应服务
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StepService stepService = ((StepService.StepBinder) service).getService();

            stepCount=stepService.getStepCount();
      //      tv_show.setText(stepCount+"");
            if(isFront) bottomBar.setCurrentStep(stepCount);

            //设置步数监听回调
            stepService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount0) {
                    stepCount=stepCount0;
            //        tv_show.setText(stepCount+"");
                    if(isFront) bottomBar.setCurrentStep(stepCount);
                }
            });
        }

        /**
         * 当与Service之间的连接丢失的时候会调用该方法，
         * 这种情况经常发生在Service所在的进程崩溃或者被Kill的时候调用，
         * 此方法不会移除与Service的连接，当服务重新启动的时候仍然会调用 onServiceConnected()。
         * @param name 丢失连接的组件名称
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isBind) {
            this.unbindService(conn);
        }
    }

}
