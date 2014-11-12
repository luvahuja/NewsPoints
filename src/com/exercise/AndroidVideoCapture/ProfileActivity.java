package com.exercise.AndroidVideoCapture;

import java.lang.Thread.UncaughtExceptionHandler;

import com.mobileapp.journalist.sfsu.AudioRecorderActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ProfileActivity extends Activity {

	SharedPreferences prefs;
	EditText authorName, organizationName;
	public static final String AUTHOR = "author", ORGANIZATION = "org";
	Button submit;
	Editor editor;
//	ExceptionHandlerActivity exceptionHandle;
	public static Context context;
	public static PendingIntent intent;
	public static Intent intentRestart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		context = this;
		intentRestart = new Intent(this, AudioRecorderActivity.class);
//		exceptionHandle = new ExceptionHandlerActivity();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		intent = PendingIntent.getActivity(this.getBaseContext(), 0,
				new Intent(getIntent()), getIntent().getFlags());
		authorName = (EditText) findViewById(R.id.author_name);
		organizationName = (EditText) findViewById(R.id.organization);
		submit = (Button) findViewById(R.id.submit);

		if (prefs == null) {
			prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

			editor = prefs.edit();
		}
		submit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String author = authorName.getText().toString();
				String organization = organizationName.getText().toString();
				editor.putString(AUTHOR, author);
				editor.putString(ORGANIZATION, organization);
				editor.commit();
				Intent i = new Intent(ProfileActivity.this, FirstActivity.class);
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

			// System.out.println("An uncaught exception has occured");
			// Intent intent = new Intent(getApplicationContext(),
			// ProfileActivity.class);
			// startActivity(intent);
			// finish();
			// android.os.Process.killProcess(android.os.Process.myPid());
			//
			// uncaughtException.uncaughtException(thread, ex);
			AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000,
					ProfileActivity.intent);
			System.exit(2);
			uncaughtException.uncaughtException(thread, ex);
		}
	};
}
