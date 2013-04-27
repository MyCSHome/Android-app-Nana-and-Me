package com.mybo.nanaAndme;


import java.util.Calendar;
import com.mybo.nanaAndme.CentralKnowledge.ActivityType;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



//@SuppressLint("NewApi")
public class NanaPage extends Activity{


	//private ImageView imVw ;
	private ImageView giffView ;
	private TextView metView ;

	private ImageButton backSetTimeButton ;
	private Button stopNowButton ;
	public Toast t;
	private Handler handler ;
	private static int countActivity = 0 ; 
	private TextView modelDetail;
	private TextView timeDetail ;
	private TextView startTimeDetail ;
	AnimationDrawable chillAnimation;
	AnimationDrawable runAnimation;
	AnimationDrawable walkAnimation ;
	AnimationDrawable upAnimation ;
	AnimationDrawable cycAnimation ;
	//private Handler handlerService ;

	/*
	private final Runnable myServiceTimer = new Runnable(){

		public void run() {

			metView.setText(String.format("%.2f", CentralKnowledge.getCurrentSumMETs()));

			//if ( (System.currentTimeMillis() > HelloIntentService.endTime) && (intentServiceIsOn == true)) {

			if  ( System.currentTimeMillis() > NanaAndMe.systemEndtime) {

				Log.v("CollectingData","NanaPage System.currentTimeMillis() > NanaAndMe.systemEndtime ");
				NanaPage.this.finish();
				switchToReportPage();
			}
			else{

				//do something
				Intent intent = new Intent(NanaPage.this, HelloIntentService.class);
				startService(intent);
				Log.v("CollectingData", 
						"NanaPage  : Intent is sent to start a service for " + NanaAndMe.serviceDuration/60000 + " minutes");

				handler.postDelayed(this, NanaAndMe.serviceDuration);

			}

		}
	};  */


	@SuppressLint({ "NewApi", "NewApi", "NewApi", "NewApi", "NewApi", "NewApi" })
	private final Runnable myLittleTimer = new Runnable(){

		@SuppressWarnings("deprecation")
		public void run() {
			
				ActivityType latestActivity = CentralKnowledge.getLastActivity();

				

				/* try animator here */

				if(latestActivity == ActivityType.RESTING){
					giffView.setBackgroundDrawable(chillAnimation);
					runAnimation.stop();
					walkAnimation.stop();
					upAnimation.stop();
					cycAnimation.stop();
					chillAnimation.start();
					chillAnimation.setOneShot(false);

				}
				else if (latestActivity == ActivityType.WALKING){
					giffView.setBackgroundDrawable(walkAnimation);
					runAnimation.stop();
					chillAnimation.stop();
					upAnimation.stop();
					cycAnimation.stop();
					walkAnimation.start();
					walkAnimation.setOneShot(false);

				}
				else if(latestActivity == ActivityType.RUNNING){
					giffView.setBackgroundDrawable(runAnimation);
					walkAnimation.stop();
					chillAnimation.stop();
					upAnimation.stop();
					cycAnimation.stop();
					runAnimation.start();
					runAnimation.setOneShot(false);

				}
				else if(latestActivity == ActivityType.CYCLING){ 
					giffView.setBackgroundDrawable(cycAnimation);
					walkAnimation.stop();
					chillAnimation.stop();
					upAnimation.stop();
					runAnimation.stop();
					cycAnimation.start();
					cycAnimation.setOneShot(false);

				}else if(latestActivity == ActivityType.UPSTAIRS){ 
					giffView.setBackgroundDrawable(upAnimation);
					walkAnimation.stop();
					chillAnimation.stop();
					runAnimation.stop();
					cycAnimation.stop();
					upAnimation.start();
					upAnimation.setOneShot(false);

				}else{
					giffView.setBackgroundDrawable(chillAnimation);
					runAnimation.stop();
					walkAnimation.stop();
					upAnimation.stop();
					cycAnimation.stop();
					chillAnimation.start();
					chillAnimation.setOneShot(false);
				}


				metView.setText(String.format("%.2f", CentralKnowledge.getCurrentSumMETs()));

				if ( System.currentTimeMillis() > HelloIntentService.endTime) {
					long delayed = System.currentTimeMillis() + 1500 ;
					while(System.currentTimeMillis() < delayed){
						giffView.setBackgroundDrawable(chillAnimation);
						runAnimation.stop();
						walkAnimation.stop();
						upAnimation.stop();
						cycAnimation.stop();
						chillAnimation.start();
						chillAnimation.setOneShot(false);
					}
					switchToReportPage();

				}else{
					handler.postDelayed(this, HelloIntentService.smallSamplingCycle);
				}
			
		} 

	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log.v("Process","NanaPage oncreate");

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_4);

		handler = new Handler();
		//handlerService = new Handler();

		giffView = (ImageView)findViewById(R.id.giffView);
		metView = (TextView)findViewById(R.id.metViewSecondPage);

		stopNowButton = (Button)findViewById(R.id.stopNowButton);
		stopNowButton.setOnClickListener(new StopNowListenter());

		backSetTimeButton = (ImageButton)findViewById(R.id.timeButton);
		backSetTimeButton.setOnClickListener(new BackToSetTimeListener());

		modelDetail = (TextView)findViewById(R.id.modelDetailView);
		
		if(CentralKnowledge.isUsingDefaultModel()==true){
			modelDetail.setText("Default mode");
		}else{
			modelDetail.setText("Customized mode");
		}

		timeDetail = (TextView)findViewById(R.id.timeDetailView);
		startTimeDetail = (TextView)findViewById(R.id.startTimeDetailView);

		timeDetail.setText("End  at " + 
				String.format("%02d",CentralKnowledge.targetHour) + 
				" : "+ 
				String.format("%02d",CentralKnowledge.targetMinute));
		
		
		if(CentralKnowledge.hasStartedPickerOnce==false){
			//this is the first time that the picker is set
			String startTime = "Start at " + 
					String.format("%02d",Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) + 
					" : "+ 
					String.format("%02d",Calendar.getInstance().get(Calendar.MINUTE));
			startTimeDetail.setText(startTime);
			CentralKnowledge.setStartingTime(startTime);
		}else{
			startTimeDetail.setText(CentralKnowledge.getStartingTime());
		}
		

		BitmapDrawable chill1 = (BitmapDrawable)getResources().getDrawable(R.drawable.chill1);
		BitmapDrawable chill2 = (BitmapDrawable)getResources().getDrawable(R.drawable.chill2);
		BitmapDrawable chill3 = (BitmapDrawable)getResources().getDrawable(R.drawable.chill3);
		BitmapDrawable chill4 = (BitmapDrawable)getResources().getDrawable(R.drawable.chill4);
		BitmapDrawable chill5 = (BitmapDrawable)getResources().getDrawable(R.drawable.chill5);
		BitmapDrawable chill6 = (BitmapDrawable)getResources().getDrawable(R.drawable.chill6);

		BitmapDrawable run1 = (BitmapDrawable)getResources().getDrawable(R.drawable.run1);
		BitmapDrawable run2 = (BitmapDrawable)getResources().getDrawable(R.drawable.run2);
		BitmapDrawable run3 = (BitmapDrawable)getResources().getDrawable(R.drawable.run3);
		BitmapDrawable run4 = (BitmapDrawable)getResources().getDrawable(R.drawable.run4);
		BitmapDrawable run5 = (BitmapDrawable)getResources().getDrawable(R.drawable.run5);

		BitmapDrawable walk1 = (BitmapDrawable)getResources().getDrawable(R.drawable.walk1);
		BitmapDrawable walk2 = (BitmapDrawable)getResources().getDrawable(R.drawable.walk2);
		BitmapDrawable walk3 = (BitmapDrawable)getResources().getDrawable(R.drawable.walk3);
		BitmapDrawable walk4 = (BitmapDrawable)getResources().getDrawable(R.drawable.walk4);

		BitmapDrawable up1 = (BitmapDrawable)getResources().getDrawable(R.drawable.up1);
		BitmapDrawable up2 = (BitmapDrawable)getResources().getDrawable(R.drawable.up2);
		BitmapDrawable up3 = (BitmapDrawable)getResources().getDrawable(R.drawable.up3);
		BitmapDrawable up4 = (BitmapDrawable)getResources().getDrawable(R.drawable.up4);
		BitmapDrawable up5 = (BitmapDrawable)getResources().getDrawable(R.drawable.up5);
		
		BitmapDrawable cyc1 = (BitmapDrawable)getResources().getDrawable(R.drawable.cyc1);
		BitmapDrawable cyc2 = (BitmapDrawable)getResources().getDrawable(R.drawable.cyc2);
		BitmapDrawable cyc3 = (BitmapDrawable)getResources().getDrawable(R.drawable.cyc3);
		BitmapDrawable cyc4 = (BitmapDrawable)getResources().getDrawable(R.drawable.cyc4);
		BitmapDrawable cyc5 = (BitmapDrawable)getResources().getDrawable(R.drawable.cyc5);
		BitmapDrawable cyc6 = (BitmapDrawable)getResources().getDrawable(R.drawable.cyc6);

		chillAnimation = new AnimationDrawable();
		chillAnimation.addFrame(chill1, 300);
		chillAnimation.addFrame(chill2, 300);
		chillAnimation.addFrame(chill3, 100);
		chillAnimation.addFrame(chill4, 100);
		chillAnimation.addFrame(chill5, 100);
		chillAnimation.addFrame(chill6, 100);

		runAnimation = new AnimationDrawable();
		runAnimation.addFrame(run1, 150);
		runAnimation.addFrame(run2, 150);
		runAnimation.addFrame(run3, 150);
		runAnimation.addFrame(run4, 150);
		runAnimation.addFrame(run5, 150);

		walkAnimation = new AnimationDrawable();
		walkAnimation.addFrame(walk1, 200);
		walkAnimation.addFrame(walk2, 200);
		walkAnimation.addFrame(walk3, 200);
		walkAnimation.addFrame(walk4, 200);

		upAnimation = new AnimationDrawable();
		upAnimation.addFrame(up1, 200);
		upAnimation.addFrame(up2, 200);
		upAnimation.addFrame(up3, 200);
		upAnimation.addFrame(up4, 200);
		upAnimation.addFrame(up5, 200);
		
		cycAnimation = new AnimationDrawable();
		cycAnimation.addFrame(cyc1, 200);
		cycAnimation.addFrame(cyc2, 200);
		cycAnimation.addFrame(cyc3, 200);
		cycAnimation.addFrame(cyc4, 200);
		cycAnimation.addFrame(cyc5, 200);
		cycAnimation.addFrame(cyc6, 200);
		


	}

	//inner class
	private class StopNowListenter implements OnClickListener {

		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(NanaPage.this);
			builder.setMessage("Are you sure you want to stop?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
					HelloIntentService.endTime = System.currentTimeMillis() ;
					
				}
			})
			.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			
			AlertDialog alert = builder.create();
			alert.show();
			

		}
	}


	//inner class
	private class BackToSetTimeListener implements OnClickListener {

		public void onClick(View v) {
			//Intent backToTimePage = new Intent(NanaPage.this, NanaAndMe.class);
			NanaPage.this.finish();
		}

	}

	protected void onResume() {

		super.onResume();
		//handlerService.post(myServiceTimer);
		handler.post(myLittleTimer);
		//Log.v("Process","NanaPage onResume()");

	}

	private void switchToReportPage(){
		
		if(countActivity==0){
			Intent reportMETs = new Intent(this, ReportMETsActivity.class);
			startActivity(reportMETs);
			countActivity++;}
		else
			NanaPage.this.finish();
	}

	public static void resetNanaPageCountActivity(){
		countActivity = 0 ; 
	}

	protected void onDestroy(){
		super.onDestroy();


	}
}
