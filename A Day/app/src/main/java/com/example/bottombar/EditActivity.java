package com.example.bottombar;


import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bottombar.alert.AlarmReceiver;
import com.example.bottombar.fragment.NoteFragment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import pub.devrel.easypermissions.EasyPermissions;

public class EditActivity extends Activity {

    //private static final int REQUEST_PERMISSION_SD_STORAGE_CODE = 1;
    private static final int TAKE_PHOTO_CODE = 666;//拍照
    private static final int LOCAL_IMAGE_CODE = 888;//调用图库
    private Uri imageUri;
    private TextView tv_time, tv_skin;
    private ImageView iv_skin;
    private LinearLayout ll_img, ll_skin, ll_alert, ll_tittle ,ll_menu,ll_pureskin1, ll_pureskin2, ll_pureskin3,
            ll_pureskin4, ll_pureskin5, ll_pureskin6, ll_pureskin7, ll_pureskin8, ll_pureskin9, ll_pureskin10, ll_pureskin11, ll_pureskin12;
    private RelativeLayout lay_main;
    private HorizontalScrollView skinmenu;
    private ImageButton yes_btn, prv_btn;
    private ptEditor pt_edit;
    private String[] perms = {Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
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
    private Uri mUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(Color.parseColor("#99CCFF"));
        }

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
                /*if (EasyPermissions.hasPermissions(EditActivity.this, perms))
                    selectPicFromLocal(EditActivity.this, 888);
                else
                    Toast.makeText(EditActivity.this, "该功能需授权后才能使用", Toast.LENGTH_SHORT).show();*/
                takePhotoOrSelectPicture();
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
                        if(alerttime != "")
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

    private void takePhotoOrSelectPicture() {
        CharSequence[] items = {"拍照","图库"};// items选项

        // 弹出对话框提示用户拍照或者是通过本地图库选择图片
        new AlertDialog.Builder(EditActivity.this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which){
                            // 选择了拍照
                            case 0:
                                if (EasyPermissions.hasPermissions(EditActivity.this, perms))
                                    takePhoto(EditActivity.this, 666);
                                else
                                    Toast.makeText(EditActivity.this, "该功能需授权后才能使用", Toast.LENGTH_SHORT).show();
                                break;
                            // 调用系统图库
                            case 1:
                                if (EasyPermissions.hasPermissions(EditActivity.this, perms))
                                    selectPicFromLocal(EditActivity.this, 888);
                                else
                                    Toast.makeText(EditActivity.this, "该功能需授权后才能使用", Toast.LENGTH_SHORT).show();
                                break;

                        }

                    }
                }).show();
    }

    public void takePhoto(EditActivity Context, int TAKE_PHOTO_CODE){
        String path = getFilesDir() + File.separator + "images" + File.separator;
        File file = new File(path, System.currentTimeMillis()+".png");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //步骤二：Android 7.0及以上获取文件 Uri
            mUri = FileProvider.getUriForFile(EditActivity.this, "com.example.bottombar.fileprovider", file);
        } else {
            //步骤三：获取文件Uri
            mUri = Uri.fromFile(file);
        }
        //步骤四：调取系统拍照
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(intent, TAKE_PHOTO_CODE);
    }

    private void showSkinMenu() {
        ll_tittle = (LinearLayout) findViewById(R.id.ll_tittle);
        ll_menu = (LinearLayout) findViewById(R.id.ll_menu);
        ll_pureskin1 = (LinearLayout) findViewById(R.id.ll_pureskin1);
        ll_pureskin2 = (LinearLayout) findViewById(R.id.ll_pureskin2);
        ll_pureskin3 = (LinearLayout) findViewById(R.id.ll_pureskin3);
        ll_pureskin4 = (LinearLayout) findViewById(R.id.ll_pureskin4);
        ll_pureskin5 = (LinearLayout) findViewById(R.id.ll_pureskin5);
        ll_pureskin6 = (LinearLayout) findViewById(R.id.ll_pureskin6);
        ll_pureskin7 = (LinearLayout) findViewById(R.id.ll_pureskin7);
        ll_pureskin8 = (LinearLayout) findViewById(R.id.ll_pureskin8);
        ll_pureskin9 = (LinearLayout) findViewById(R.id.ll_pureskin9);
        ll_pureskin10 = (LinearLayout) findViewById(R.id.ll_pureskin10);
        ll_pureskin11 = (LinearLayout) findViewById(R.id.ll_pureskin11);
        ll_pureskin12 = (LinearLayout) findViewById(R.id.ll_pureskin12);
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                Window window = getWindow();
                switch (v.getId()) {
                    case R.id.ll_pureskin1:
                        lay_main.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        ll_menu.setBackgroundColor(Color.parseColor("#CCFFFF"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#99CCFF"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#99CCFF"));
                        }
                        break;
                    case R.id.ll_pureskin2:
                        lay_main.setBackgroundColor(Color.parseColor("#CCFFFF"));
                        ll_menu.setBackgroundColor(Color.parseColor("#FFFFCC"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#FFCCCC"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#FFCCCC"));
                        }
                        break;
                    case R.id.ll_pureskin3:
                        lay_main.setBackgroundColor(Color.parseColor("#FFCCCC"));
                        ll_menu.setBackgroundColor(Color.parseColor("#FF9966"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#FF6666"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#FF6666"));
                        }
                        break;
                    case R.id.ll_pureskin4:
                        lay_main.setBackgroundColor(Color.parseColor("#FFFFCC"));
                        ll_menu.setBackgroundColor(Color.parseColor("#99CC00"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#339933"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#339933"));
                        }
                        break;
                    case R.id.ll_pureskin5:
                        lay_main.setBackgroundColor(Color.parseColor("#CCCC99"));
                        ll_menu.setBackgroundColor(Color.parseColor("#999966"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#339999"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#339999"));
                        }
                        break;
                    case R.id.ll_pureskin6:
                        lay_main.setBackgroundColor(Color.parseColor("#999999"));
                        ll_menu.setBackgroundColor(Color.parseColor("#669999"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#003366"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#003366"));
                        }
                        break;
                    case R.id.ll_pureskin7:
                        lay_main.setBackgroundColor(Color.parseColor("#CCCCFF"));
                        ll_menu.setBackgroundColor(Color.parseColor("#9999CC"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#996699"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#996699"));
                        }
                        break;
                    case R.id.ll_pureskin8:
                        lay_main.setBackgroundColor(Color.parseColor("#CCCC99"));
                        ll_menu.setBackgroundColor(Color.parseColor("#999999"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#663300"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#663300"));
                        }
                        break;
                    case R.id.ll_pureskin9:
                        lay_main.setBackgroundColor(Color.parseColor("#FFFF66"));
                        ll_menu.setBackgroundColor(Color.parseColor("#CCCC00"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#666600"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#666600"));
                        }
                        break;
                    case R.id.ll_pureskin10:
                        lay_main.setBackgroundColor(Color.parseColor("#FFFFCC"));
                        ll_menu.setBackgroundColor(Color.parseColor("#FFCC33"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#999966"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#999966"));
                        }
                        break;
                    case R.id.ll_pureskin11:
                        lay_main.setBackgroundColor(Color.parseColor("#FFCCCC"));
                        ll_menu.setBackgroundColor(Color.parseColor("#CC99CC"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#996699"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#996699"));
                        }
                        break;
                    case R.id.ll_pureskin12:
                        lay_main.setBackgroundColor(Color.parseColor("#CCCCCC"));
                        ll_menu.setBackgroundColor(Color.parseColor("#999999"));
                        ll_tittle.setBackgroundColor(Color.parseColor("#333333"));
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
                            window.setStatusBarColor(Color.parseColor("#333333"));
                        }
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
        ll_pureskin7.setOnClickListener(listener);
        ll_pureskin8.setOnClickListener(listener);
        ll_pureskin9.setOnClickListener(listener);
        ll_pureskin10.setOnClickListener(listener);
        ll_pureskin11.setOnClickListener(listener);
        ll_pureskin12.setOnClickListener(listener);
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

        if((!tempContent.equals(content)) || !alerttime.equals(tempAlerttime)){                   //如果内容非空且已经被更改，则更新显示和数据库
            ArrayList<HashMap<String,String>> list = Utils.getList();
            com.example.bottombar.sqlite.SQLiteUtils sqlite = new com.example.bottombar.sqlite.SQLiteUtils();
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
                alertSet();
            }

        }

    }


    private void alertSet(){
        Intent intent = new Intent(EditActivity.this,AlarmReceiver.class);
        intent.setPackage("com.example.bottombar");
        intent.putExtra("datetime", datetime);
        intent.putExtra("content", content);
        intent.putExtra("alerttime",alerttime);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(EditActivity.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    0, pendingIntent);
        } else {
            // 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    (24 * 60 * 60 * 1000), pendingIntent);
        }
        Toast.makeText(EditActivity.this, "闹钟设置成功",
                Toast.LENGTH_LONG).show();
       // alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);
        //setRepeating()这里第二个参数不能设置成现在时间，否则闹钟会设置完就开启
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),(24 * 60 * 60 * 1000), pendingIntent);
    }

    public String getFilePathFromURI(Context context, Uri contentUri) {
        File rootDataDir = context.getFilesDir();
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(rootDataDir + File.separator + fileName);
            copyFile(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public void copyFile(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int copyStream(InputStream input, OutputStream output) throws Exception, IOException {
        final int BUFFER_SIZE = 1024 * 2;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
            }
            try {
                in.close();
            } catch (IOException e) {
            }
        }
        return count;
    }


    public static String uriToRealPath(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.Images.Media.DATA},
                null,
                null,
                null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
        return path;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PHOTO_CODE:// 拍照
                    String str ="";
                    if (Build.VERSION.SDK_INT >= 24) {
                        str = getFilePathFromURI(EditActivity.this, mUri);
                    } else {
                        str = uriToRealPath(EditActivity.this, mUri);
                    }
                    pt_edit.insertBitmap(str);
                    break;
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
