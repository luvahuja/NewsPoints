package com.mobileapp.journalist.sfsu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Timer;
import java.util.TimerTask;

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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidVideoCapture extends Activity implements
		SurfaceHolder.Callback, OnClickListener {

	Button buttonStop, buttonStart, source, pause, interview, wide, extraWide,
			establish;
	RadioButton source1, source2, source3;
	MediaRecorder mediaRecorder;
	SurfaceHolder surfaceHolder;
	boolean sourceVisible, recording;
	RelativeLayout cameraTypeTray;
	TextView tv1, tv2, tv3, tv4, tv5, timeCount;
	EditText tv6;
	LinearLayout questionsRow;
	RadioGroup sourceInfo;
	File newFileAfterPause;
	// LinearLayout innerLayer;
	SlidingDrawer menuSlider;

	int minuteCount = 0, hourcount = 0;
	public static int countDown = 1, stopSequnetialCount = 1;
	String sourceName = "SourceName", angleName = "Angle";

	String ans1, ans2, ans3, ans4, ans5;
	static int pauseCount = 1;
	int lastFocusedTextView = 0;
	int changeTextViewColorNumber = 0;

	File file;
	File myStorageDir;

	SharedPreferences prefs;
	Editor edit;
	public Timer T = new Timer();

	/*
	 * Called when the activity is first created.
	 */

	private void findAllViewsToClass() {
		questionsRow = (LinearLayout) findViewById(R.id.innerLay);

		menuSlider = (SlidingDrawer) findViewById(R.id.menu_slider);
		tv1 = (TextView) findViewById(R.id.text1);
		tv2 = (TextView) findViewById(R.id.text2);
		tv3 = (TextView) findViewById(R.id.text3);
		tv4 = (TextView) findViewById(R.id.text4);
		tv5 = (TextView) findViewById(R.id.text5);
		tv6 = (EditText) findViewById(R.id.text6);

		timeCount = (TextView) findViewById(R.id.time_count);
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
		pause = (Button) findViewById(R.id.pause);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sourceVisible = false;

		ans1 = ans2 = ans3 = ans4 = ans5 = "";
		mediaRecorder = new MediaRecorder();
		// initMediaRecorder();
		pauseCount = 1;

		initMediaRecorderAfterPauseState(pauseCount);
		pauseCount++;
		setContentView(R.layout.main);
		findAllViewsToClass();

		SurfaceView myVideoView = (SurfaceView) findViewById(R.id.videoview);
		surfaceHolder = myVideoView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int setWidth = width / 3;
		int height = display.getHeight();
		int setHeight = height / 2;
		System.out.println("height " + height + " set height " + setHeight);
		// LayoutParams params = new LayoutParams(setWidth,
		// LayoutParams.WRAP_CONTENT);
		//
		// // questionsRow.scrollBy(setWidth / 2, 0);
		// // questionsRow.scrollBy(-setWidth / 2, 0);
		//
		// // params.leftMargin = 60;
		// params.topMargin = 20;
		// // params.height = 26;
		// tv1.setLayoutParams(params);
		// tv2.setLayoutParams(params);
		// tv3.setLayoutParams(params);
		// tv4.setLayoutParams(params);
		// tv5.setLayoutParams(params);
		timeCount.setText("0:00");

		prefs = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

		menuSlider
				.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {

					@Override
					public void onDrawerOpened() {
						// TODO Auto-generated method stub
						System.out.println("Height of questions row"
								+ questionsRow.getHeight());
						int width = questionsRow.getWidth();
						int setWidth = width / 3;
						LayoutParams params = new LayoutParams(setWidth,
								questionsRow.getHeight());

						// questionsRow.scrollBy(setWidth / 2, 0);
						// questionsRow.scrollBy(-setWidth / 2, 0);

						// params.leftMargin = 60;
						params.topMargin = 10;
						// params.height = 26;
						tv1.setLayoutParams(params);
						tv2.setLayoutParams(params);
						tv3.setLayoutParams(params);
						tv4.setLayoutParams(params);
						tv5.setLayoutParams(params);

					}
				});
		edit = prefs.edit();
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
						case 3:
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

		pause.setOnClickListener(this);
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

	private void initMediaRecorder() {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		CamcorderProfile camcorderProfile_HQ = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		mediaRecorder.setProfile(camcorderProfile_HQ);

		myStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/journalist");
		if (!myStorageDir.exists()) {

			myStorageDir.mkdir();

		}
		file = new File(myStorageDir + "/myRecording.mp4");
		mediaRecorder.setOutputFile(myStorageDir + "/myRecording.mp4");

		// System.out.println(myStorageDir + "/myRecording.mp4");

	}

	private void initMediaRecorderAfterPauseState(int count) {
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		CamcorderProfile camcorderProfile_HQ = CamcorderProfile
				.get(CamcorderProfile.QUALITY_HIGH);
		mediaRecorder.setProfile(camcorderProfile_HQ);

		myStorageDir = new File(Environment.getExternalStorageDirectory()
				+ "/journalist");
		if (!myStorageDir.exists()) {

			myStorageDir.mkdir();

		}
		newFileAfterPause = new File(myStorageDir + "/myRecording" + count
				+ ".mp4");

		mediaRecorder.setOutputFile(myStorageDir + "/myRecording" + count
				+ ".mp4");

		System.out.println(myStorageDir + "/myRecordingPart.mp4");
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.stop && recording) {
			System.out.println("Hello stopiing video recording");
			String angleNm = prefs.getString(angleName, "no angle");
			String sourceNm = prefs.getString(sourceName, "no source");

			newFileAfterPause.renameTo(new File(myStorageDir + "/" + angleNm
					+ "-" + sourceNm + "Reocrding.mp4"));

			System.out.println("Stopping finally");
			recording = false;
			// try {
			// FileInputStream f1 = new FileInputStream(myStorageDir
			// + "/myRecording.mp4");
			// FileInputStream f2 = new FileInputStream(myStorageDir
			// + "/myRecordingPart.mp4");
			//
			// SequenceInputStream sistream = new SequenceInputStream(f2, f1);
			// FileOutputStream fostream = new FileOutputStream(myStorageDir
			// + "/myRecordingComplete.mp4");// destinationfile
			//
			// int[] temp = new int[1024];
			//
			// int count = 1024;
			// byte[] buffer = new byte[count];
			// while ((count = sistream.read(buffer, 0, 1024)) != -1) {
			//
			// System.out.println("Mergining files" + buffer);
			// fostream.write(buffer, 0, 1024); // to write to file
			// }
			// fostream.close();
			// sistream.close();
			// f1.close();
			// f2.close();
			//
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			T.cancel();
			// T.purge();

			countDown = 0;
			minuteCount = 0;
			// mediaRecorder.stop();

			// mediaRecorder.release();
			mediaRecorder.reset();
			stopSequnetialCount = 2;
			buttonStop.setText("Record");

		} else if (v.getId() == R.id.stop && !recording) {

			if (stopSequnetialCount == 2) {
				initMediaRecorderAfterPauseState(pauseCount);

				pauseCount++;
				recording = false;
				prepareMediaRecorder();

				stopSequnetialCount = 1;
			}
			pause.setClickable(true);

			mediaRecorder.start();
			System.out.println("Hello starting video recording");

			buttonStop.setText("STOP");
			recording = true;
			T = new Timer();
			TimerTask tt = new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						public void run() {
							timeCount.setText(hourcount + minuteCount + ":"
									+ countDown);
							if (countDown == 60) {
								countDown = 0;
								minuteCount++;
							}
							if (minuteCount == 60) {
								hourcount++;
							}
							System.out.println("TT being called");
							countDown++;
						}
					});
				}
			};

			T.scheduleAtFixedRate(tt, (long) 1000, (long) 1000);
		}
		if (v.getId() == R.id.pause && recording) {
			mediaRecorder.reset();
			T.cancel();
			// T.purge();

			countDown = 0;
			minuteCount = 0;
			String angleNm = prefs.getString(angleName, "no angle");
			String sourceNm = prefs.getString(sourceName, "no source");

			newFileAfterPause.renameTo(new File(myStorageDir + "/" + angleNm
					+ "-" + sourceNm + "Reocrding.mp4"));

			buttonStop.setText("Resume");
			initMediaRecorderAfterPauseState(pauseCount);

			pauseCount++;
			recording = false;
			prepareMediaRecorder();

		} else if (v.getId() == R.id.pause && !recording) {

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
	}
}