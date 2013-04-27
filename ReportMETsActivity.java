package com.mybo.nanaAndme;





import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import java.util.List;



import com.mybo.nanaAndme.R;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageButton;
import android.widget.Toast;

import android.widget.TextView;


public class ReportMETsActivity extends Activity{

	private TextView reportTextView ;
	private ImageButton backButton ;
	private ImageButton shareButton;
	private ImageButton socialButton ;
	private String message ;
	private double met;
	private ImageButton graphButton ;
	private final String activityFile = "act_time.txt";
	private String savedInfo = "savedInfo.txt";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_2);

		reportTextView = (TextView)findViewById(R.id.reportTextView);


		backButton = (ImageButton)findViewById(R.id.backMeButton);
		shareButton = (ImageButton)findViewById(R.id.communityMeButton);
		socialButton = (ImageButton)findViewById(R.id.socialMeButton);

		backButton.setOnClickListener(new BackPageListener()); 
		shareButton.setOnClickListener(new ShareListener()); 
		socialButton.setOnClickListener(new SocialListener()); 



		graphButton = (ImageButton)findViewById(R.id.reportMeButton);
		graphButton.setOnClickListener(new GraphListener());




		FileInputStream saved_fis   = null;
		BufferedReader saved_reader = null;
		String saved_str ; 
		try {
			saved_fis = openFileInput(savedInfo);
			saved_reader = new BufferedReader(new InputStreamReader(saved_fis));
		} catch (FileNotFoundException e1) {
			//Log.v("filename","local file can not found");
		}

		try{

			met = Double.parseDouble(saved_reader.readLine());
			message = saved_reader.readLine();
			saved_reader.close();
			
		}catch (IOException e) {
			//Log.v("CollectingData","Cannot read from act_time txt.");
		}

		CentralKnowledge.lastwordfromNana = message ;
		reportTextView.setText(message);




		/*
		if(CentralKnowledge.reportMETs() != 0 ){
			//metsDetail.setText(Double.toString(CentralKnowledgeKeeper.reportMETs())) ;

			met = CentralKnowledge.reportMETs() ; 
			double walkMin = (double)(CentralKnowledge.getWalkCounter()*5)/60 ;
			double runMin = (double)(CentralKnowledge.getRunCounter()*5)/60 ;
			double restMin = (double)(CentralKnowledge.getRestCounter()*5)/60 ;
			double totalTime ;

			if(CentralKnowledge.isUsingDefaultModel()==true){ //default mode
				totalTime = restMin+walkMin+runMin;

				message = "Today we've spent " + String.format("%.1f", met) +" kcal within "
						+ String.format("%.1f", totalTime)  + " minutes. We ran for " + String.format("%.1f", runMin) 
						+ ",  walked for " + String.format("%.1f", walkMin) + ",  and chilled out for " 
						+ String.format("%.1f", restMin) + " minutes." ; 

				if (runMin > restMin*0.2){
					message ="Wow! " + message + " Congratulations!";
				}



			}else{
				double cycMin = (double)(CentralKnowledge.getCyclingCounter()*5)/60 ;
				double upMin = (double)(CentralKnowledge.getUpstairsCounter()*5)/60 ;

				totalTime = restMin+walkMin+runMin+cycMin+upMin;



				message = "Today we've spent " + String.format("%.1f", met) +" kcal within "
						+ String.format("%.1f", totalTime)  + " minutes. We ran for " + String.format("%.1f", runMin) 
						+ ",  walked for " + String.format("%.1f", walkMin)
						+ ",  cycled for " + String.format("%.1f", cycMin) 
						+ ",  climbed stairs for " + String.format("%.1f", upMin) 
						+ ",  and chilled out for " + String.format("%.1f", restMin) + " minutes." ; 

				if ((runMin > restMin*0.2) || (cycMin>restMin*0.1) || (upMin>restMin*0.1) ){
					message ="Hooray ! " + message + " Congratulations!";
				}



			}
			CentralKnowledge.lastwordfromNana = message ;
			reportTextView.setText(CentralKnowledge.lastwordfromNana);

		}*/



		NanaAndMe.intentServiceIsOn = false ;

	}




	//inner class
	private class BackPageListener implements OnClickListener{

		public void onClick(View v) {
			NanaPage.resetNanaPageCountActivity();
			CentralKnowledge.resetInferActivityList();
			Intent backToFirstPage = new Intent(ReportMETsActivity.this,NanaAndMe.class);
			startActivity(backToFirstPage);
			ReportMETsActivity.this.finish();

		}

	}

	private class ShareListener implements OnClickListener{

		public void onClick(View v) {
			Intent shareMETs = new Intent();
			shareMETs.setAction(Intent.ACTION_VIEW);
			shareMETs.setData(Uri.parse("http://nana-and-me.appspot.com"));
			startActivity(shareMETs);
		}
	}


	public void onBackPressed(){
		NanaPage.resetNanaPageCountActivity();
		CentralKnowledge.resetInferActivityList();
		Intent backToFirstPage = new Intent(ReportMETsActivity.this,NanaAndMe.class);
		startActivity(backToFirstPage);
		ReportMETsActivity.this.finish();
	} 



	private class SocialListener implements OnClickListener{

		public void onClick(View v) {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(shareIntent, 0);
			//Log.v("package","haa");
			if (!resInfo.isEmpty()){
				//Log.v("package","haha");
				int i = 0;
				for (ResolveInfo resolveInfo : resInfo) {
					String packageName = resolveInfo.activityInfo.packageName;
					//Log.v("package",packageName);
					Intent targetedShareIntent = new Intent(android.content.Intent.ACTION_SEND);
					targetedShareIntent.setType("text/plain");
					targetedShareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "achievement");
					if (packageName.equalsIgnoreCase("com.facebook.katana") ){

						targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://nana-and-me.appspot.com/");
						//targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "comddd");
						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
						i++;
					}
					else if(packageName.equalsIgnoreCase("com.google.android.apps.plus") ){
						targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From Nana-and-Me:\nNana and me have burnt "+String.format("%.2f", met)+" Kcal today!");
						//targetedShareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "comddd");
						targetedShareIntent.setPackage(packageName);
						targetedShareIntents.add(targetedShareIntent);
						i++;}
					else {

					}

				}
				if(i>0){
					Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0), "share with friends on your favourite social app!");

					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));

					startActivity(chooserIntent);}
				else 
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com")));
			}

		}

	}


	private class GraphListener implements OnClickListener{

		public void onClick(View v) {
			File fileDir = getFilesDir();
			File act_time_file = new File( fileDir.getAbsolutePath() + "/" + activityFile );


			if ( act_time_file.exists() && (act_time_file.length() != 0) ){
				Intent gotoGraph = new Intent(ReportMETsActivity.this, GraphActivity.class);
				startActivity(gotoGraph);
			}else{
				Toast.makeText(ReportMETsActivity.this, "Ooops Can you try again please?", Toast.LENGTH_SHORT).show();
			}


		}


	}


	public void onResume(){
		reportTextView.setText(CentralKnowledge.lastwordfromNana);
		super.onResume();

	}





}
