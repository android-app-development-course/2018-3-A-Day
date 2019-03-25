package com.example.bottombar.alert;

import java.io.IOException;


import com.example.bottombar.EditActivity;
import com.example.bottombar.fragment.NoteFragment;
import com.example.bottombar.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

public class AlertDialogActivity extends Activity implements OnClickListener{

	public static AlertDialogActivity context = null;
	private MediaPlayer player = new MediaPlayer();
	WakeLock mWakelock;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
		mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,"AlertDialog");
		if (mWakelock != null) {
			mWakelock.acquire();
		}
		KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
		context = this;
		try{
			Uri localUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM);
			if((player != null) && (localUri != null))
			{
					player.setDataSource(context,localUri);
					player.prepare();
					player.setLooping(false);
					player.start();
			}
			
			AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
			localBuilder.setTitle("事件提醒");
			String mes = getIntent().getStringExtra("content");
			String t = "";
			for(int j = 0;j<mes.length();j++){
				if(mes.charAt(j)!='☆'){
					t = t+mes.charAt(j);
				}
				else {
					j++;
					while (mes.charAt(j)!='☆'){
						j++;
					}
					t = t + "[图片]";
				}
			}
			localBuilder.setMessage(t);
			localBuilder.setPositiveButton("查看",this);
			localBuilder.setNegativeButton("忽略",this);
			localBuilder.show();
			
		}catch (IllegalArgumentException localIllegalArgumentException)
	    {
		      localIllegalArgumentException.printStackTrace();
		    }
		    catch (SecurityException localSecurityException)
		    {
		      localSecurityException.printStackTrace();
		    }
		    catch (IllegalStateException localIllegalStateException)
		    {
		      localIllegalStateException.printStackTrace();
		    } catch (IOException e) 
		    {
				e.printStackTrace();
		    }
		if (mWakelock != null) {
			mWakelock.release();
		}
		}
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch(which){
		case DialogInterface.BUTTON_POSITIVE:
		{
			Intent intent = new Intent(AlertDialogActivity.this, EditActivity.class);
			Bundle b = new Bundle();
			b.putString("datetime",getIntent().getStringExtra("datetime"));
			b.putString("content", getIntent().getStringExtra("content"));
			b.putString("alerttime",getIntent().getStringExtra("alerttime"));
			intent.putExtra("android.intent.extra.INTENT", b);
			startActivity(intent);
			finish();
		}
		case DialogInterface.BUTTON_NEGATIVE:
		{
			//mWakelock.release();
			player.stop();
			finish();
		}
	  }
	}
}