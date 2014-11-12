package com.mobileapp.journalist.sfsu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Date;
import java.util.TimeZone;

import com.exercise.AndroidVideoCapture.ProfileActivity;
import com.exercise.AndroidVideoCapture.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AudioRecorderActivity extends Activity implements OnClickListener {

	public MediaRecorder audioRecorder;
	Button stop, pause, sourceAudio,interview, wide, extraWide,
	establish;
	Boolean lastPressedPause = false, backPressed = false;
	private Boolean m_bStart = Boolean.valueOf(false);
	private Handler handler;
	private View.OnClickListener listener;
	Runnable r;
	Boolean recording = false, sourceVisible = false;
	File myStorageDir, newFileAfterPause;
	String sourceName = "SourceName", angleName = "Angle";
	int hour = 0, minutes = 0;
	java.text.DateFormat df;
	File[] mergedFiles;
	TextView tv1, tv2, tv3, tv4, tv5, tv6, recordTime;
	String ans1, ans2, ans3, ans4, ans5;
	int freq_value = 1000;
	private boolean isPlay = true;
	long startTime = 0;
	Editor edit;

	SharedPreferences prefs;
	static int pauseCount = 1;
	int lastFocusedTextView = 0;
	int changeTextViewColorNumber = 0;
	private RelativeLayout cameraTypeTray;
	private RadioGroup sourceInfo;

	private void findAllViewsToClass() {
		stop = (Button) findViewById(R.id.stop);
		tv1 = (TextView) findViewById(R.id.text1);
		tv2 = (TextView) findViewById(R.id.text2);
		tv3 = (TextView) findViewById(R.id.text3);
		tv4 = (TextView) findViewById(R.id.text4);
		tv5 = (TextView) findViewById(R.id.text5);
		tv6 = (EditText) findViewById(R.id.text6);
		sourceAudio = (Button) findViewById(R.id.source_audio);
		cameraTypeTray = (RelativeLayout) findViewById(R.id.camera_type_tray);
		sourceInfo = (RadioGroup) findViewById(R.id.source_info);
		interview = (Button) findViewById(R.id.interview_audio);
		wide = (Button) findViewById(R.id.wide_audio);
		extraWide = (Button) findViewById(R.id.extrawide_audio);
		establish = (Button) findViewById(R.id.establish_audio);
		recordTime = (TextView) findViewById(R.id.time);

	}

	private void registerClickListener() {
		stop.setOnClickListener(this);
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
		tv4.setOnClickListener(this);
		sourceAudio.setOnClickListener(this);
		tv5.setOnClickListener(this);
		interview.setOnClickListener(this);
		establish.setOnClickListener(this);
		extraWide.setOnClickListener(this);
		wide.setOnClickListener(this);
}

	private void initMediarecorder(int count) {
		String subDir = prefs.getString(ProfileActivity.AUTHOR, "Anonymous");
		String dir = prefs.getString(ProfileActivity.ORGANIZATION, "Unknown");
		myStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/journalist");
		if (!myStorageDir.exists()) {

			myStorageDir.mkdir();
		}
		myStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/journalist/" + dir);
		if (!myStorageDir.exists()) {

			myStorageDir.mkdir();

		}
		myStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/journalist/" + dir + "/" + subDir);
		if (!myStorageDir.exists()) {

			myStorageDir.mkdir();

		}
		newFileAfterPause = new File(myStorageDir + "/myAudioRecording" + count
				+ ".mp3");

		audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
		audioRecorder.setOutputFile(myStorageDir + "/myAudioRecording" + count
				+ ".mp3");
		pauseCount++;

	}

	private void prepareMediaRecorder() {
		// audioRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		try {
			audioRecorder.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		backPressed = true;
		if (recording) {
			audioRecorder.stop();
			audioRecorder.release();
			timerHandler.removeCallbacks(timerRunnable);
		}

	}

	Handler timerHandler = new Handler();
	Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
			long millis = System.currentTimeMillis() - startTime;
			int seconds = (int) (millis / 1000);
			seconds = seconds % 60;
			if (seconds == 59) {
				seconds = 0;
				minutes++;
				if (minutes == 60) {
					hour++;
				}
			}
			recordTime.setText(String.format("%02d:%02d", minutes, seconds));

			System.out.println("Timer Runnable being called" + minutes
					+ "%d:%02d" + seconds);
			if (!backPressed)
				timerHandler.postDelayed(this, 1000);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audio_recorder);

		ans1 = ans2 = ans3 = ans4 = ans5 = "";

		findAllViewsToClass();
		registerClickListener();
		prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

		edit = prefs.edit();
		audioRecorder = new MediaRecorder();
		sourceInfo
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						switch (checkedId) {
						case R.id.source1:
							System.out.println("source1");
							edit.putString(sourceName, "source1");
							edit.commit();
							break;
						case R.id.source2:
							System.out.println("source2");

							edit.putString(sourceName, "source2");

							edit.commit();
							break;
						case R.id.source3:

							System.out.println("source3");

							edit.putString(sourceName, "source3");

							edit.commit();
							break;

						}
					}
				});

		prefs = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.stop && recording) {
			System.out.println("Hello stopiing audio recording");
			stop.setText("Record");
			recording = false;

			String angleNm = prefs.getString(angleName, "no angle");
			String sourceNm = prefs.getString(sourceName, "no source");

			newFileAfterPause.renameTo(new File(myStorageDir + "/" + pauseCount + angleNm
					+ "-" + sourceNm + "Reocrding.mp3"));

			System.out.println("The pause count is " + pauseCount);
			audioRecorder.reset();

			recording = false;
			lastPressedPause = false;
			backPressed = true;
		} else if (v.getId() == R.id.stop && !recording) {

			backPressed = false;

			initMediarecorder(pauseCount);
			prepareMediaRecorder();
			audioRecorder.start();
			System.out.println("Hello starting video recording");
			stop.setText("STOP");
			recording = true;
			startTime = System.currentTimeMillis();

			timerHandler.postDelayed(timerRunnable, 0);

			// handler.post(r);
			// ForShowingWave();

			// sampler.StartSampling();
		}

		/*
		 * Algo for question text color change
		 */
		System.out.println("Hello source clicked");

		if (v.getId() == R.id.source_audio) {
			System.out.println("Hello source clicked");
			if (!sourceVisible) {
				sourceInfo.setVisibility(0);

				cameraTypeTray.setVisibility(0);
				sourceVisible = true;
			} else {
				sourceVisible = false;
				sourceInfo.setVisibility(4);

				cameraTypeTray.setVisibility(4);
			}
		}
		if (v.getId() == R.id.text1) {

			switch (changeTextViewColorNumber) {
			case 1:
				if (!tv6.getText().toString().matches(""))
					ans1 = tv6.getText().toString();
				tv1.setBackgroundColor(0x3364FE2E);
				tv6.setText("");
				break;

			case 2:
				if (!tv6.getText().toString().matches(""))
					ans2 = tv6.getText().toString();
				tv6.setText("");
				tv2.setBackgroundColor(0x3364FE2E);

				break;

			case 3:
				if (!tv6.getText().toString().matches(""))
					tv3.setBackgroundColor(0x3364FE2E);
				ans3 = tv6.getText().toString();

				tv6.setText("");
				break;

			case 4:
				if (!tv6.getText().toString().matches(""))
					ans4 = tv6.getText().toString();
				tv4.setBackgroundColor(0x3364FE2E);
				tv6.setText("");

				break;
			case 5:
				if (!tv6.getText().toString().matches(""))
					tv5.setBackgroundColor(0x3364FE2E);
				ans5 = tv6.getText().toString();
				tv6.setText("");

				break;

			}

			changeTextViewColorNumber = 1;

			tv6.setText(ans1);
			// lastFocusedTextView = 1;
			// tv6.setText("Answer 1");

		}
		if (v.getId() == R.id.text2) {
			switch (changeTextViewColorNumber) {
			case 1:
				if (!tv6.getText().toString().matches(""))
					tv1.setBackgroundColor(0x3364FE2E);
				ans1 = tv6.getText().toString();
				tv6.setText("");

				break;

			case 2:
				if (!tv6.getText().toString().matches(""))
					tv2.setBackgroundColor(0x3364FE2E);
				ans2 = tv6.getText().toString();
				tv6.setText("");
				break;

			case 3:
				if (!tv6.getText().toString().matches(""))
					tv3.setBackgroundColor(0x3364FE2E);
				ans3 = tv6.getText().toString();
				tv6.setText("");

				break;

			case 4:
				if (!tv6.getText().toString().matches(""))
					tv4.setBackgroundColor(0x3364FE2E);
				ans4 = tv6.getText().toString();
				tv6.setText("");

				break;
			case 5:
				if (!tv6.getText().toString().matches(""))
					tv5.setBackgroundColor(0x3364FE2E);
				ans5 = tv6.getText().toString();
				tv6.setText("");

				break;

			}

			changeTextViewColorNumber = 2;
			// tv2.setBackgroundColor(0x3364FE2E);
			lastFocusedTextView = 2;
			tv6.setText(ans2);
			// tv6.setText("Answer 2");

		}
		if (v.getId() == R.id.text3) {
			switch (changeTextViewColorNumber) {
			case 1:
				if (!tv6.getText().toString().matches(""))
					tv1.setBackgroundColor(0x3364FE2E);
				ans1 = tv6.getText().toString();
				tv6.setText("");

				break;

			case 2:
				if (!tv6.getText().toString().matches(""))
					tv2.setBackgroundColor(0x3364FE2E);
				ans2 = tv6.getText().toString();
				tv6.setText("");

				break;

			case 3:
				if (!tv6.getText().toString().matches(""))
					tv3.setBackgroundColor(0x3364FE2E);
				ans3 = tv6.getText().toString();
				tv6.setText("");

				break;

			case 4:
				if (!tv6.getText().toString().matches(""))
					tv4.setBackgroundColor(0x3364FE2E);
				ans4 = tv6.getText().toString();
				tv6.setText("");
				break;
			case 5:
				if (!tv6.getText().toString().matches(""))
					tv5.setBackgroundColor(0x3364FE2E);
				ans5 = tv6.getText().toString();
				tv6.setText("");

				break;

			}

			changeTextViewColorNumber = 3;
			// tv3.setBackgroundColor(0x3364FE2E);
			lastFocusedTextView = 3;
			tv6.setText(ans3);

		}
		if (v.getId() == R.id.text4) {

			switch (changeTextViewColorNumber) {
			case 1:
				if (!tv6.getText().toString().matches(""))
					tv1.setBackgroundColor(0x3364FE2E);
				ans1 = tv6.getText().toString();
				tv6.setText("");
				break;

			case 2:
				if (!tv6.getText().toString().matches(""))
					tv2.setBackgroundColor(0x3364FE2E);
				ans2 = tv6.getText().toString();
				tv6.setText("");
				break;

			case 3:
				if (!tv6.getText().toString().matches(""))
					tv3.setBackgroundColor(0x3364FE2E);
				ans3 = tv6.getText().toString();
				tv6.setText("");
				break;

			case 4:
				if (!tv6.getText().toString().matches(""))
					tv4.setBackgroundColor(0x3364FE2E);
				ans4 = tv6.getText().toString();
				tv6.setText("");
				break;
			case 5:
				if (!tv6.getText().toString().matches(""))
					tv5.setBackgroundColor(0x3364FE2E);
				ans5 = tv6.getText().toString();
				tv6.setText("");
				break;

			}
			changeTextViewColorNumber = 4;
			// tv4.setBackgroundColor(0x3364FE2E);
			lastFocusedTextView = 4;

			tv6.setText(ans4);

		}
		if (v.getId() == R.id.text5) {
			switch (changeTextViewColorNumber) {
			case 1:

				if (!tv6.getText().toString().matches(""))
					tv1.setBackgroundColor(0x3364FE2E);
				ans1 = tv6.getText().toString();
				tv6.setText("");
				break;

			case 2:
				if (!tv6.getText().toString().matches(""))
					tv2.setBackgroundColor(0x3364FE2E);
				ans2 = tv6.getText().toString();
				tv6.setText("");
				break;

			case 3:
				if (!tv6.getText().toString().matches(""))
					tv3.setBackgroundColor(0x3364FE2E);
				ans3 = tv6.getText().toString();
				tv6.setText("");
				break;

			case 4:
				if (!tv6.getText().toString().matches(""))
					tv4.setBackgroundColor(0x3364FE2E);
				ans4 = tv6.getText().toString();
				tv6.setText("");
				break;
			case 5:
				if (!tv6.getText().toString().matches(""))
					tv5.setBackgroundColor(0x3364FE2E);
				ans5 = tv6.getText().toString();
				tv6.setText("");
				break;

			}

			changeTextViewColorNumber = 5;
			// tv5.setBackgroundColor(0x3364FE2E);
			lastFocusedTextView = 5;
			tv6.setText(ans5);

		}
		if (v.getId() == R.id.establish_audio) {

			edit.putString(angleName, "Establish");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());
		}
		if (v.getId() == R.id.extrawide_audio) {

			edit.putString(angleName, "ExtraWide");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());
		}
		if (v.getId() == R.id.wide_audio) {

			edit.putString(angleName, "wide");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());
		}
		if (v.getId() == R.id.interview_audio) {

			edit.putString(angleName, "interview");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());

		}
		/*
		 * text color change algo ends here
		 */
	}
}
