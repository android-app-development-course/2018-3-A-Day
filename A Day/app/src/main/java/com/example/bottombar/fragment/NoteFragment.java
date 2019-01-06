package com.example.bottombar.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.bottombar.EditActivity;
import com.example.bottombar.R;
import com.example.bottombar.Utils;
import com.example.bottombar.sqlite.*;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import pub.devrel.easypermissions.EasyPermissions;

public class NoteFragment extends ListFragment implements SearchView.OnQueryTextListener,EasyPermissions.PermissionCallbacks{


    private static final int REQUEST_PERMISSION_SD_STORAGE_CODE = 1;
    SimpleAdapter listAdapter;
    int index = 0;// 长按指定数据的索引
    PopupWindow mPopupWindow = null;
    ArrayList<HashMap<String,String>> showlist,list = Utils.getList();
    private String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    DatabaseHelper dbHelper = new DatabaseHelper(getActivity(), "a_day_db");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            //如果有权限,正常流程
        } else {
            //如果没有权限,现在申请
            EasyPermissions.requestPermissions(getActivity(),
                    "该文件需要读写sd卡权限",
                    REQUEST_PERMISSION_SD_STORAGE_CODE,
                    perms);
        }
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_note, container, false);

        SearchView searchview = (SearchView) view.findViewById(R.id.searchView);
        searchview.setOnQueryTextListener(this);
        //初始化载入数据库的数据
        list = Utils.getList();
        if(list.isEmpty())
            loadFromDatabase(list);      //先检查缓存，若没有数据再从数据库加载

        Utils.MillisToDate(list);
        listAdapter = new SimpleAdapter(getActivity(),list,R.layout.list_item,new String[]{"datetime","content"},
                new int[]{R.id.datetime,R.id.content});
        setListAdapter(listAdapter);                      //将备忘录数据显示出来
        Button button = (Button)view.findViewById(R.id.createButton);
        button.setOnClickListener(new ClickListener());

        return view;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {

        getListView().setOnItemClickListener(new ListItemClickListener());
        getListView().setOnItemLongClickListener( new ItemLongClickListener());
    }

    class ClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            Utils.DateToMillis(list);
            Intent intent = new Intent(getActivity(), EditActivity.class);
            Bundle b = new Bundle();
            b.putString("datetime", "");
            b.putString("content", "");
            b.putString("alerttime","");
            intent.putExtra("android.intent.extra.INTENT", b);
            startActivity(intent);                                //启动转到的Activity
        }
    }
    class ItemLongClickListener implements OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            index = position;

            View popupView = getLayoutInflater().inflate(R.layout.popupwindow,null);
            mPopupWindow = new PopupWindow(popupView,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setAnimationStyle(R.style.popupAnimation);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable());//这里必须设置这句，使得touch弹窗以外的地方或者按返回键才会消失而且Drawable不能用null代替
            mPopupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

            Button deleteButton = (Button)popupView.findViewById(R.id.deleteButton);
            Button shareButton = (Button)popupView.findViewById(R.id.shareButton);

            deleteButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    deleteItem(index);
                    mPopupWindow.dismiss();
                }
            });
            shareButton.setOnClickListener(new OnClickListener(){
                @Override
                public void onClick(View v) {
                    shareItem(index);
                    mPopupWindow.dismiss();
                }
            });
            return true;
        }
    }


    private void loadFromDatabase(ArrayList<HashMap<String,String>> list){

        DatabaseHelper dbHelper1 = new DatabaseHelper(getActivity(), "a_day_db");
        SQLiteDatabase db = dbHelper1.getReadableDatabase();
        Cursor cursor = db.query("user", new String[] { "datetime", "content","alerttime" }, null,
                null, null, null,"datetime desc");
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                String datetime = cursor.getString(0);
                String content = cursor.getString(1);
                String alerttime = cursor.getString(2);
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("datetime", datetime);
                map.put("content", content);
                map.put("alerttime", alerttime);
                list.add(map);
            }
        }
    }
    class ListItemClickListener implements OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Intent itemintent = new Intent(getActivity(),EditActivity.class);
            Utils.DateToMillis(list);
            Bundle b = new Bundle();
            b.putString("datetime", Utils.getItem(position).get("datetime"));
            b.putString("content", Utils.getItem(position).get("content"));
            b.putString("alerttime", Utils.getItem(position).get("alerttime"));
            b.putInt("index", position);
            itemintent.putExtra("android.intent.extra.INTENT", b);
            startActivity(itemintent);                                //启动转到的Activity
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        Utils.sort();
        Utils.MillisToDate(list);
        getListView().setOnItemClickListener(new ListItemClickListener());
        listAdapter = new SimpleAdapter(getActivity(),list,R.layout.list_item,new String[]{"datetime","content"},
                new int[]{R.id.datetime,R.id.content});
        setListAdapter(listAdapter);
//        listAdapter.notifyDataSetChanged();                           //更新ListView的数据显示
//        Utils.DateToMillis();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        list = Utils.getList();
        if(newText != null){
            showlist = new ArrayList<HashMap<String,String>>();
            for(int i=0;i<list.size();i++){
                String content = list.get(i).get("content");
                if(content.contains(newText)){
                    HashMap<String,String> map = list.get(i);
                    map.put("id", String.valueOf(i));
                    showlist.add(map);
                }
            }
//			listAdapter.notifyDataSetChanged();
            listAdapter = new SimpleAdapter(getActivity(),showlist,R.layout.list_item,new String[]{"datetime","content"},
                    new int[]{R.id.datetime,R.id.content});
            setListAdapter(listAdapter);
            getListView().setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Intent searchintent = new Intent(getActivity(),EditActivity.class);
                    Utils.DateToMillis(list);
                    Bundle b = new Bundle();
                    b.putString("datetime", showlist.get(position).get("datetime"));
                    b.putString("content", showlist.get(position).get("content"));
                    b.putString("alerttime",showlist.get(position).get("alerttime"));
                    b.putInt("index", Integer.parseInt(showlist.get(position).get("id")));
                    searchintent.putExtra("android.intent.extra.INTENT", b);
                    startActivity(searchintent);                                //启动转到的Activity
                }
            });
        }
        return false;
    }

    private boolean deleteItem(int position){
        Utils.DateToMillis(list);
        ListView listview = getListView();
        String deleteDatetime = ((HashMap<String, String>)(listview.getItemAtPosition(index))).get("datetime").toString();
        Utils.getList().remove(index);

//		String deleteContent = ((HashMap)(list.getItemAtPosition(index))).get("content").toString();

        DatabaseHelper dbHelper2 = new DatabaseHelper(getActivity(), "a_day_db");
        SQLiteDatabase db = dbHelper2.getReadableDatabase();
        SQLiteUtils sqlite = new SQLiteUtils();
        sqlite.delete(dbHelper2, deleteDatetime);

        Utils.sort();
        Utils.MillisToDate(list);
        listAdapter.notifyDataSetChanged();                           //更新ListView的数据显示
        return true;
    }

    private void shareItem(int index) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "This is a day，来自备忘录分享："+Utils.getItem(index).get("content"));
        startActivity(Intent.createChooser(intent, "分享到"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getActivity(), "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(getActivity(), "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

}