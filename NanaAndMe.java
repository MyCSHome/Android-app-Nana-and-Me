package com.mybo.nanaAndme;

import java.util.Calendar;
import com.mybo.nanaAndme.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.StrictMode;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;


@SuppressLint("NewApi")
public class NanaAndMe extends Activity{

	
	//private TextView metView ;
	private ImageButton buttonStart ;
	private ImageButton backButton ; 
	private ImageButton gotoNanaButton ;
	public Toast t;
	//private Handler handler ;
	private TimePicker timePicker;
	public static boolean intentServiceIsOn = false;

	//public static long systemEndtime ; 
	//public static int serviceDuration = 60000; // 1 minute 
	//public static int smallSamplingCycle = 5000 ; 


	/*
	private final Runnable myLittleTimer = new Runnable(){

		public void run() {
			
				metView.setText(String.format("%.2f", CentralKnowledge.getCurrentSumMETs()));

				//if ( (System.currentTimeMillis() > HelloIntentService.endTime) && (intentServiceIsOn == true)) {
				
				if ( (System.currentTimeMillis() > HelloIntentService.endTime) && (intentServiceIsOn == true)) {
					Log.v("CollectingData", "System.currentTimeMillis() > systemEndtime");
					intentServiceIsOn = false ;
					NanaAndMe.this.finish();
					
				}else{
					Log.v("CollectingData", "NanaAndMe  : Handler is called");
					//Intent intent = new Intent(NanaAndMe.this, HelloIntentService.class);
					//startService(intent);
					//Log.v("CollectingData", "NanaAndMe  : Intent is sent to start a service for " + serviceDuration/60000 + " minutes");
					handler.postDelayed(this, HelloIntentService.smallSamplingCycle);
				}
		} 

	}; */


	/*
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {


		@Override
		public void onReceive(Context context, Intent intent) {
			
			Bundle extras = intent.getExtras();
			HelloIntentService.endTime  = (Long) extras.get("endtime") ;
			CentralKnowledge.rebornCurrentSumMETs( (Double)extras.get("sumMETs") );
			CentralKnowledge.rebornSetLastActivity((Integer)extras.get("lastActivity"));
			
	
			Log.v("CollectingData","NanaAndMe received REBORN_ACTION");
			Log.v("CollectingData","EndTime is " + HelloIntentService.endTime );
			
			Log.v("CollectingData","SumMETS is " + CentralKnowledge.getCurrentSumMETs());
			Log.v("CollectingData","lastActivity is " + CentralKnowledge.getActivityType_Int(CentralKnowledge.getLastActivity()));
			
			Intent rebornIntent = new Intent(NanaAndMe.this, HelloIntentService.class);
			startService(rebornIntent);
			//switchToNanaPage();
		}
	};  */




	@Override
	public void onCreate(Bundle savedInstanceState) {
		
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_collect_1);

			//handler = new Handler();

			//giffView = (WebView)findViewById(R.id.webView1);
			timePicker = (TimePicker)findViewById(R.id.timePicker1);

			buttonStart = (ImageButton)findViewById(R.id.buttonOK);
			buttonStart.setOnClickListener(new OKListener()); 

			backButton = (ImageButton)findViewById(R.id.backmenuButton);
			backButton.setOnClickListener(new BackListener()); 
			gotoNanaButton = (ImageButton)findViewById(R.id.clickNanaButton);
			gotoNanaButton.setImageResource(R.drawable.clickmenot);
			//metView = (TextView)findViewById(R.id.metView);

	}




	private void switchToNanaPage(){
		Intent gotoNana = new Intent(NanaAndMe.this, NanaPage.class);
		startActivity(gotoNana);
	}


	//inner class
	private class BackListener implements OnClickListener{

		public void onClick(View v) {
			Intent gotoMenu = new Intent(NanaAndMe.this, Menu.class);
			startActivity(gotoMenu);
			NanaAndMe.this.finish();
		}

	}

	//inner class
	private class GotoNanaPageListener implements OnClickListener{

		public void onClick(View v) {
			if(intentServiceIsOn == true){
				switchToNanaPage();
			}
			else{
				Toast.makeText(NanaAndMe.this, "Can you and Nana set a valid endtime first?", Toast.LENGTH_SHORT).show();
			}

		}

	} 

	//inner class
	private class OKListener implements OnClickListener{ //OnClickListener is an interface

		public void onClick(View v) {

			
			/* Set the time */

			Calendar c = Calendar.getInstance(); 
			int minute = c.get(Calendar.MINUTE);
			int hour = c.get(Calendar.HOUR_OF_DAY);

			CentralKnowledge.targetHour = timePicker.getCurrentHour();
			CentralKnowledge.targetMinute = timePicker.getCurrentMinute();

			int hourDiff = CentralKnowledge.targetHour - hour;
			int minuteDiff = CentralKnowledge.targetMinute - minute;

			int cycleTime = hourDiff*3600000 + minuteDiff*60000;


			if(cycleTime<=0){
				Toast.makeText(NanaAndMe.this, "Can you and Nana set a new endtime?", Toast.LENGTH_SHORT).show();
			}else{
				
				
				HelloIntentService.endTime = System.currentTimeMillis() + cycleTime; 
				//systemEndtime = System.currentTimeMillis() + cycleTime ; 
				
				
				gotoNanaButton.setOnClickListener(new GotoNanaPageListener());
				gotoNanaButton.setImageResource(R.drawable.clickme);
				//Toast.makeText(NanaAndMe.this, "Start Nana, me, and today", Toast.LENGTH_LONG).show();
				
				

				if(intentServiceIsOn==false){
					//Log.v("CollectingData", "intentServiceIsOn is false and will change to true");
					intentServiceIsOn = true ;
					Intent intent = new Intent(NanaAndMe.this, HelloIntentService.class);
					startService(intent);
					
				}
				
				
				//handler.post(myLittleTimer);
				
				//Log.v("CollectingData", "About to go to NanaPage");
				switchToNanaPage();
				

				
			}
		}
	}



	protected void onResume() {

		super.onResume();
		
		
		if (intentServiceIsOn==true){
			backButton.setEnabled(false);
			backButton.setImageResource(R.drawable.menu_dis);
		}else{
			backButton.setEnabled(true);
			backButton.setImageResource(R.drawable.menu2);
		}
		
		
		
		
		//registerReceiver(myReceiver, new IntentFilter("REBORN_ACTION") );

	}

	


}