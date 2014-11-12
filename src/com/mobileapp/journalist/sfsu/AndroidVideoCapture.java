package com.mobileapp.journalist.sfsu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;

import com.exercise.AndroidVideoCapture.ProfileActivity;
import com.exercise.AndroidVideoCapture.R;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidVideoCapture extends Activity implements
		SurfaceHolder.Callback, OnClickListener {

	Button buttonStop, buttonStart, source, interview, wide, extraWide,
			establish, scrollLeft, scrollRight;
	RadioButton source1, source2, source3;
	MediaRecorder mediaRecorder;
	SurfaceHolder surfaceHolder;
	boolean sourceVisible, recording;
	RelativeLayout cameraTypeTray;
	TextView tv1, tv2, tv3, tv4, tv5;
	EditText tv6;
	LinearLayout questionsRow;
	RadioGroup sourceInfo;
	File newFileAfterPause;
	HorizontalScrollView hsv;
	Boolean recordingState;

	String sourceName = "SourceName", angleName = "Angle";

	String ans1, ans2, ans3, ans4, ans5;
	static int pauseCount = 1;
	int lastFocusedTextView = 0;
	int changeTextViewColorNumber = 0;

	File file;
	File myStorageDir;

	int setWidth;
	SharedPreferences prefs;
	Editor edit;

	/** Called when the activity is first created. */

	private void findAllViewsToClass() {
		questionsRow = (LinearLayout) findViewById(R.id.innerLay);

		hsv = (HorizontalScrollView) findViewById(R.id.hsv);
		tv1 = (TextView) findViewById(R.id.text1);
		tv2 = (TextView) findViewById(R.id.text2);
		tv3 = (TextView) findViewById(R.id.text3);
		tv4 = (TextView) findViewById(R.id.text4);
		tv5 = (TextView) findViewById(R.id.text5);
		tv6 = (EditText) findViewById(R.id.text6);

		scrollLeft = (Button) findViewById(R.id.scroll_left);
		scrollRight = (Button) findViewById(R.id.scroll_right);
		interview = (Button) findViewById(R.id.interview);
		wide = (Button) findViewById(R.id.wide);
		extraWide = (Button) findViewById(R.id.extrawide);
		establish = (Button) findViewById(R.id.establish);
		source1 = (RadioButton) findViewById(R.id.source1);
		source2 = (RadioButton) findViewById(R.id.source2);
		source3 = (RadioButton) findViewById(R.id.source3);

		cameraTypeTray = (RelativeLayout) findViewById(R.id.camera_type_tray);
		sourceInfo = (RadioGroup) findViewById(R.id.source_info);

		buttonStop = (Button) findViewById(R.id.stop);
		source = (Button) findViewById(R.id.source);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sourceVisible = false;

		ans1 = ans2 = ans3 = ans4 = ans5 = "";
		mediaRecorder = new MediaRecorder();
		// initMediaRecorder();
		pauseCount = 1;

		prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

		edit = prefs.edit();
		setContentView(R.layout.main);
		findAllViewsToClass();

		SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview);
		surfaceHolder = myVideoView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		setWidth = width / 3;
		int height = display.getHeight();
		int setHeight = height / 2;
		System.out.println("height " + height + " set height " + setHeight);
		LayoutParams params = new LayoutParams(setWidth, setHeight);

		//
		questionsRow.scrollBy(setWidth / 2, 0);
		// questionsRow.scrollBy(-setWidth / 2, 0);

		params.leftMargin = 60;
		params.topMargin = 100;
		tv1.setLayoutParams(params);
		tv2.setLayoutParams(params);
		tv3.setLayoutParams(params);
		tv4.setLayoutParams(params);
		tv5.setLayoutParams(params);

		registerClickListener();
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

	}

	private void registerClickListener() {
		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
		tv3.setOnClickListener(this);
		tv4.setOnClickListener(this);
		tv5.setOnClickListener(this);

		scrollLeft.setOnClickListener(this);
		scrollRight.setOnClickListener(this);
		// buttonStart = (Button) findViewById(R.id.rec_start);
		// buttonStart.setOnClickListener(this);
		buttonStop.setOnClickListener(this);
		source.setOnClickListener(this);
		interview.setOnClickListener(this);
		establish.setOnClickListener(this);
		extraWide.setOnClickListener(this);
		wide.setOnClickListener(this);

	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		prepareMediaRecorder();
		recording = false;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	private void initMediaRecorderAfterPauseState(int count) {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		CamcorderProfile camcorderProfile_HQ = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		mediaRecorder.setProfile(camcorderProfile_HQ);

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
		newFileAfterPause = new File(myStorageDir + "/myRecording" + count
				+ ".mp4");

		mediaRecorder.setOutputFile(myStorageDir + "/myRecording" + count
				+ ".mp4");

		System.out.println(myStorageDir + "/myRecordingPart.mp4");
		pauseCount++;

	}

	private void prepareMediaRecorder() {
		mediaRecorder.setPreviewDisplay(surfaceHolder.getSurface());
		try {
			mediaRecorder.prepare();
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
		if (recording)
			mediaRecorder.stop();

		mediaRecorder.release();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.stop && recording) {
			System.out.println("Hello stopiing video recording");
			mediaRecorder.reset();
			//
			String angleNm = prefs.getString(angleName, "no angle");
			String sourceNm = prefs.getString(sourceName, "no source");

			System.out.println("File being saved name is"+myStorageDir + "/" + pauseCount
					+ " " + angleNm + "-" + sourceNm + "Reocrding.mp4");
			System.out.println(prefs.getString(sourceName, "no source"));
			newFileAfterPause.renameTo(new File(myStorageDir + "/" + pauseCount
					+ " " + angleNm + "-" + sourceNm + "Reocrding.mp4"));

			buttonStop.setText("Resume");

			recording = false;

		} else if (v.getId() == R.id.stop && !recording) {
			initMediaRecorderAfterPauseState(pauseCount);
			prepareMediaRecorder();

			mediaRecorder.start();
			System.out.println("Hello starting video recording");
			buttonStop.setText("STOP");
			recording = true;

		}

		if (v.getId() == R.id.source) {
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
		if (v.getId() == R.id.establish) {

			edit.putString(angleName, "Establish");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());
		}
		if (v.getId() == R.id.extrawide) {

			edit.putString(angleName, "ExtraWide");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());
		}
		if (v.getId() == R.id.wide) {

			edit.putString(angleName, "wide");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());
		}
		if (v.getId() == R.id.interview) {

			edit.putString(angleName, "interview");
			// prefs.edit().commit();

			edit.commit();
			// prefs.edit().apply();
			System.out.println(prefs.getString(angleName, "k").toString());

		}
		if (v.getId() == R.id.scroll_left) {

			hsv.smoothScrollTo((int) hsv.getScrollX() - setWidth,
					(int) hsv.getScrollY());
		}
		if (v.getId() == R.id.scroll_right) {
			hsv.smoothScrollTo((int) hsv.getScrollX() + setWidth,
					(int) hsv.getScrollY());
		}
	}

}