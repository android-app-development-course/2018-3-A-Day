package com.example.bottombar;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;

public class EditActivity extends Activity {

    //private static final int REQUEST_PERMISSION_SD_STORAGE_CODE = 1;
    private static final int LOCAL_IMAGE_CODE = 888;
    private TextView tv_time, tv_skin;
    private ImageView iv_skin;
    private LinearLayout ll_img, ll_skin, ll_alert, ll_pureskin1, ll_pureskin2, ll_pureskin3,
            ll_pureskin4, ll_pureskin5, ll_pureskin6, ll_perskin1, ll_perskin2, ll_perskin3, ll_perskin4, ll_perskin5, ll_perskin6;
    private RelativeLayout lay_main;
    private HorizontalScrollView skinmenu;
    private ImageButton yes_btn, prv_btn;
    private ptEditor pt_edit;
    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private boolean isVisible = true;
    private TimeSetDialog timeSetDialog=null;
    private TextView alertTextView;
    private String content;
    private String tempContent,tempDatetime1,tempDatetime,tempAlerttime;
    private int index=0;
    private String alerttime = "";
    private String datetime;
    private UserInfo user;
    Calendar calendar = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE); /*
        if (EasyPermissions.hasPermissions(this, perms)) {
            //如果有权限,正常流程
        } else {
            //如果没有权限,现在申请
            EasyPermissions.requestPermissions(this,
                    "该文件需要读写sd卡权限",
                    REQUEST_PERMISSION_SD_STORAGE_CODE,
                    perms);
        }
        */
        setContentView(R.layout.edit_activity);

        tv_time = (TextView) findViewById(R.id.tv_time);
        ll_img = (LinearLayout) findViewById(R.id.ll_img);
        ll_skin = (LinearLayout) findViewById(R.id.ll_skin);
        ll_alert = (LinearLayout) findViewById(R.id.ll_alert);
        tv_skin = (TextView) findViewById(R.id.tv_skin);
        iv_skin = (ImageView) findViewById(R.id.iv_skin);
        lay_main = (RelativeLayout) findViewById(R.id.lay_main);
        skinmenu = (HorizontalScrollView) findViewById(R.id.skinmenu);
        alertTextView = (TextView)findViewById(R.id.timeText);

        user = new UserInfo();
        user.setAlerttime(alerttime);

        //编辑记事本
        pt_edit = (ptEditor) findViewById(R.id.pt_edit);

        pt_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("EditActivity", pt_edit.getmContentList().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });
        GetTime();

        ClickBottom();

        Back();

        Save();

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("android.intent.extra.INTENT");
        datetime = bundle.getString("datetime");
        content = bundle.getString("content");
        alerttime = bundle.getString("alerttime");
        index = bundle.getInt("index");
        tempContent = new String(content);
        tempDatetime = new String(datetime);
        tempAlerttime = new String(alerttime);
        Time time = new Time();
        //判断该记录是新建还是修改
        if(datetime.equals(""))
        {
            time.setToNow();
        }
        else{
            time.set(Long.parseLong(datetime));
        }
        int month = time.month+1;
        int day = time.monthDay;
        int hour = time.hour;
        int minute = time.minute;
        String c = "/storage/emulated/0/Pictures/Screenshots/Screenshot_20181225-134542.jpg";

        pt_edit.load(content);
        String tempS = new String(alerttime);
        if(!alerttime.equals(""))
            alertTextView.setText(Utils.timeTransfer(tempS));
        else alertTextView.setText("");
        pt_edit.setSelection(content.length());  //设置光标在文字末尾

    }

    private void GetTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date = new Date(System.currentTimeMillis());
        tv_time.setText(simpleDateFormat.format(date));
    }


    private void ClickBottom() {
        ll_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (EasyPermissions.hasPermissions(EditActivity.this, perms))
                    selectPicFromLocal(EditActivity.this, 888);
                else
                    Toast.makeText(EditActivity.this, "该功能需授权后才能使用", Toast.LENGTH_SHORT).show();
            }
        });
        ll_skin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVisible) {
                    isVisible = false;
                    skinmenu.setVisibility(View.VISIBLE);//这一句显示布局LinearLayout区域
                    showSkinMenu();
                    iv_skin.setImageResource(R.drawable.cancel);
                    tv_skin.setText("取消");
                } else {
                    skinmenu.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域
                    isVisible = true;
                    iv_skin.setImageResource(R.drawable.skin);
                    tv_skin.setText("换肤");
                }

            }
        });
        ll_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSetDialog = new TimeSetDialog(EditActivity.this);
                //添加监听器，当dialog消失即执行cancel()方法时触发的事件
                timeSetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        alerttime = timeSetDialog.alerttime;
                        if(alerttime != null)
                            alertTextView.setText(Utils.timeTransfer(alerttime));
                        else
                            alertTextView.setText("");
                        calendar = timeSetDialog.calendar;
                        user.setAlerttime(alerttime);
                    }
                });
                timeSetDialog.show();
            }
        });
    }

    private void showSkinMenu() {
        LinearLayout ll_pureskin1 = (LinearLayout) findViewById(R.id.ll_pureskin1);
        LinearLayout ll_pureskin2 = (LinearLayout) findViewById(R.id.ll_pureskin2);
        LinearLayout ll_pureskin3 = (LinearLayout) findViewById(R.id.ll_pureskin3);
        LinearLayout ll_pureskin4 = (LinearLayout) findViewById(R.id.ll_pureskin4);
        LinearLayout ll_pureskin5 = (LinearLayout) findViewById(R.id.ll_pureskin5);
        LinearLayout ll_pureskin6 = (LinearLayout) findViewById(R.id.ll_pureskin6);
        LinearLayout ll_perskin1 = (LinearLayout) findViewById(R.id.ll_perskin1);
        LinearLayout ll_perskin2 = (LinearLayout) findViewById(R.id.ll_perskin2);
        LinearLayout ll_perskin3 = (LinearLayout) findViewById(R.id.ll_perskin3);
        LinearLayout ll_perskin4 = (LinearLayout) findViewById(R.id.ll_perskin4);
        LinearLayout ll_perskin5 = (LinearLayout) findViewById(R.id.ll_perskin5);
        LinearLayout ll_perskin6 = (LinearLayout) findViewById(R.id.ll_perskin6);
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_pureskin1:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.pureskin1));
                        break;
                    case R.id.ll_pureskin2:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.pureskin2));
                        break;
                    case R.id.ll_pureskin3:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.pureskin3));
                        break;
                    case R.id.ll_pureskin4:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.pureskin4));
                        break;
                    case R.id.ll_pureskin5:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.pureskin5));
                        break;
                    case R.id.ll_pureskin6:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.pureskin6));
                        break;
                    case R.id.ll_perskin1:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.perskin1));
                        break;
                    case R.id.ll_perskin2:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.perskin2));
                        break;
                    case R.id.ll_perskin3:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.perskin3));
                        break;
                    case R.id.ll_perskin4:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.perskin4));
                        break;
                    case R.id.ll_perskin5:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.perskin5));
                        break;
                    case R.id.ll_perskin6:
                        lay_main.setBackground(getResources().getDrawable(R.drawable.perskin6));
                        break;
                    default:
                        break;
                }
                skinmenu.setVisibility((View.GONE));
                isVisible = true;
                iv_skin.setImageResource(R.drawable.skin);
                tv_skin.setText("换肤");
            }
        };

        ll_pureskin1.setOnClickListener(listener);
        ll_pureskin2.setOnClickListener(listener);
        ll_pureskin3.setOnClickListener(listener);
        ll_pureskin4.setOnClickListener(listener);
        ll_pureskin5.setOnClickListener(listener);
        ll_pureskin6.setOnClickListener(listener);
        ll_perskin1.setOnClickListener(listener);
        ll_perskin2.setOnClickListener(listener);
        ll_perskin3.setOnClickListener(listener);
        ll_perskin4.setOnClickListener(listener);
        ll_perskin5.setOnClickListener(listener);
        ll_perskin6.setOnClickListener(listener);
    }

    private void Back() {
        prv_btn = (ImageButton) findViewById(R.id.prv_btn);
        prv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditActivity.this.finish();
            }
        });
    }

    private void Save(){
        yes_btn = (ImageButton) findViewById(R.id.yes_btn);
        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void selectPicFromLocal(EditActivity Context, int LOCAL_IMAGE_CODE) {
        Intent intent = new Intent();
        String IMAGE_TYPE = "image/*";
        intent.setType(IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, LOCAL_IMAGE_CODE);
    }

    public void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        Log.d("Uri1", imagePath);
        pt_edit.insertBitmap(imagePath);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式的ID
                String id = docId.split(":")[1];
                //获取相册路径
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
            Log.d("Uri1", imagePath);
        }
        pt_edit.insertBitmap(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;

        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Time time = new Time();
        time.setToNow();

        user.setAlerttime(alerttime);
        datetime =""+time.toMillis(true);
        user.setDatetime(datetime);
        time.set(time.toMillis(true));

        content = pt_edit.getText().toString();
        user.setContent(content);

        if((!content.isEmpty() && !tempContent.equals(content)) || !alerttime.equals("") && !alerttime.equals(tempAlerttime)){                   //如果内容非空且已经被更改，则更新显示和数据库
            ArrayList<HashMap<String,String>> list = Utils.getList();
            com.example.bottombar.sqlite.SQLiteUtils sqlite = new com.example.bottombar.sqlite.SQLiteUtils();
            System.out.println("---------------------------");
            com.example.bottombar.sqlite.DatabaseHelper dbHelper = sqlite.createDBHelper(EditActivity.this);
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("datetime",user.getDatetime());
            map.put("content",user.getContent());
            map.put("alerttime",user.getAlerttime());

            if(tempContent.isEmpty())  {					//若为新建记录则添加
                list.add(map);
                sqlite.insert(dbHelper,user);
            }
            else {
                list.set(index, map);          //若为修改替换掉原来的记录
                sqlite.delete(dbHelper, tempDatetime);
                sqlite.insert(dbHelper,user);
            }
            //设置闹钟提醒
            if(!alerttime.equals(tempAlerttime) && !alerttime.equals(""))
            {
                System.out.println("alerttime done!");
                alertSet();
            }

        }
    }


    private void alertSet(){
        Intent intent = new Intent("android.intent.action.ALARMRECEIVER");
        intent.putExtra("datetime", datetime);
        intent.putExtra("content", content);
        intent.putExtra("alerttime",alerttime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);
        //setRepeating()这里第二个参数不能设置成现在时间，否则闹钟会设置完就开启
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),(24 * 60 * 60 * 1000), pendingIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOCAL_IMAGE_CODE:
                    if (data != null) {
                        if (resultCode == RESULT_OK) {
                            if (Build.VERSION.SDK_INT >= 19) {
                                handleImageOnKitKat(data);
                            } else {
                                handleImageBeforeKitKat(data);
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
