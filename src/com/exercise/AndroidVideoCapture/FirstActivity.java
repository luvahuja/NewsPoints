package com.exercise.AndroidVideoCapture;

import java.lang.Thread.UncaughtExceptionHandler;

import com.mobileapp.journalist.sfsu.AndroidVideoCapture;
import com.mobileapp.journalist.sfsu.AudioRecorderActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends Activity {

	Button audio, video;
	ProfileActivity profileActivity = new ProfileActivity();
//	ExceptionHandlerActivity exceptionHandle;
	public static PendingIntent intent;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first);
//		exceptionHandle = new ExceptionHandlerActivity();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		intent = PendingIntent.getActivity(this.getBaseContext(), 0,
				new Intent(getIntent()), getIntent().getFlags());
		 audio = (Button) findViewById(R.id.audio_recording);
		video = (Button) findViewById(R.id.video_recording);
		audio.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(FirstActivity.this,
						AudioRecorderActivity.class);
				startActivity(i);

			}
		});
		video.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent i = new Intent(FirstActivity.this,
						AndroidVideoCapture.class);
				startActivity(i);
			}
		});
	}

	public UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		public UncaughtExceptionHandler uncaughtException = Thread
				.getDefaultUncaughtExceptionHandler();

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// TODO Auto-generated method stub

			System.out.println("An uncaught exception has occured");
			AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
					ProfileActivity.intent);
			System.exit(2);
			uncaughtException.uncaughtException(thread, ex);

		}
	};
}
