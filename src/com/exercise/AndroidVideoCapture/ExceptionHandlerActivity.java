package com.exercise.AndroidVideoCapture;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ExceptionHandlerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exception_handler);
	}

	public UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {

		public UncaughtExceptionHandler uncaughtException = Thread
				.getDefaultUncaughtExceptionHandler();

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// TODO Auto-generated method stub

			System.out.println("An uncaught exception has occured");
			// Intent intent = new Intent(getApplicationContext(),
			// ProfileActivity.class);
			// startActivity(intent);

			// finish();
			// android.os.Process.killProcess(android.os.Process.myPid());
			AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, ProfileActivity.intent);
			System.exit(2);
			uncaughtException.uncaughtException(thread, ex);
		}
	};
}
