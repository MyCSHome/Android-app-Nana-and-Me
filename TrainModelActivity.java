package com.mybo.nanaAndme;



import java.io.BufferedReader;
import java.io.File;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import java.io.OutputStreamWriter;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;

import weka.core.Instances;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import android.widget.Toast;

public class TrainModelActivity extends Activity{

	private Button buttonStart;
	private Button buttonBack ;
	private Button buttonTrain ;
	private Button buttonReset ;
	private RadioGroup  radioGroup;
	private RadioButton radioButton ;
	private Handler hand ;
	private String activity = "" ;
	private int progressBarStatus2 ; 
	private ProgressDialog progressBar2;
	private int progressBarStatus1 ; 
	private ProgressDialog progressBar1;
	private File checkFile ;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.train);

		buttonStart = (Button)findViewById(R.id.startButton);
		buttonStart.setOnClickListener(new StartListener()); 

		buttonBack = (Button)findViewById(R.id.backToMenuButton);
		buttonBack.setOnClickListener(new BackMenuListener());

		buttonTrain = (Button)findViewById(R.id.trainButton);
		buttonTrain.setOnClickListener(new TrainListener());
		
		radioGroup = (RadioGroup)findViewById(R.id.radioGroup);

		buttonReset = (Button)findViewById(R.id.ResetTrainingButton);
		buttonReset.setOnClickListener(new ResetListener());
		hand = new Handler();
		
		checkFile = new File(CentralKnowledge.getFilepathARFF());
		
		if(checkFile.exists() ){
			buttonTrain.setEnabled(true);
		}else{
			buttonTrain.setEnabled(false);
		}
	}

	
	
	

	//inner class
	private class StartListener implements OnClickListener{

		
		

		
		public void onClick(final View v) {

			//inner class
			final Runnable myTimer = new Runnable(){


				public void run() {
					
					Intent startCollect = new Intent(TrainModelActivity.this, CollectIntentService.class);
					startService(startCollect);
					//buttonStart.setEnabled(false);

					progressBar1 = new ProgressDialog(v.getContext());
					progressBar1.setCancelable(true);
					progressBar1.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressBar1.setMessage(activity + " to train.");
					progressBar1.setProgress(0);
					progressBar1.setMax(4);
					progressBar1.show();
					progressBarStatus1= 0 ; 



					new Thread(new Runnable() {

						public void run() {
							while (progressBarStatus1 < 4){

								try {
									Thread.sleep(5010);
								} catch (InterruptedException e) {
									//Log.v("CollectingData", "Thread cannot sleep.");
								}
								progressBarStatus1++;
								progressBar1.setProgress(progressBarStatus1);


							}
							progressBar1.dismiss();

						}

					}).start(); 
					
				}
			};

			
			
			
			
			
			AlertDialog.Builder builder = new AlertDialog.Builder(TrainModelActivity.this);
			builder.setMessage("This app will allow 10 sec for you to prepare and the next 20 sec to collect data. Are you ready to start collecting data?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					
					int selectedId = radioGroup.getCheckedRadioButtonId();
					radioButton = (RadioButton) findViewById(selectedId);


					if(radioButton.getText().toString().equals("Running")){
						CentralKnowledge.setLabel("RUNNING");
						activity = "Run";
					}else if(radioButton.getText().toString().equals("Walking")){
						CentralKnowledge.setLabel("WALKING");
						activity = "Walk";
					}else if(radioButton.getText().toString().equals("Cycling")){
						CentralKnowledge.setLabel("CYCLING");
						activity = "Ride a cycle";
					}else{
						CentralKnowledge.setLabel("UPSTAIRS");
						activity = "Go up the stairs";
					}



					progressBar2 = new ProgressDialog(v.getContext());
					progressBar2.setCancelable(true);
					progressBar2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					progressBar2.setMessage("Keep the phone in the pocket.");
					progressBar2.setProgress(0);
					progressBar2.setMax(10);
					progressBar2.show();
					progressBarStatus2 = 0 ; 


					new Thread(new Runnable() {

						public void run() {
							while (progressBarStatus2 < 10){

								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									//Log.v("CollectingData", "Thread cannot sleep.");
								}
								progressBarStatus2++;
								progressBar2.setProgress(progressBarStatus2);


							}
							progressBar2.dismiss();
							//post handler
							hand.post(myTimer);
						}

					}).start(); 

					/*long waitTime = System.currentTimeMillis() + 10100 ;
					while(System.currentTimeMillis() < waitTime){
						//wait for 10 sec
					}*/

					
					buttonTrain.setEnabled(true);
					buttonStart.setEnabled(true);


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
	private class BackMenuListener implements OnClickListener{

		public void onClick(View v) {
			Intent goBackToMenu = new Intent(TrainModelActivity.this, Menu.class);
			startActivity(goBackToMenu);
			//TrainModelActivity.this.onDestroy();
		}

	}

	//inner class
	private class TrainListener implements OnClickListener{

		public void onClick(View v) {

			String fullFileNameARFF = CentralKnowledge.getFilepathARFF();

			BufferedReader reader = null; 
			Instances data = null;
			try {

				reader = new BufferedReader(new FileReader(fullFileNameARFF));
				data = new Instances(reader);
				reader.close();
				data.setClassIndex(data.numAttributes() - 1);

			}
			catch(Exception e) {
				//Log.v("CollectingData",".....");
			}
			try{
				// setting class attribute

				//File fpth = getFilesDir();
				String absFilepathModel = CentralKnowledge.getFilepathModel();

				J48 myJ48tree = new J48 ();
				myJ48tree.buildClassifier(data);


				//CentralKnowledge.setFilepathModel(absFilepathModel);
				//Log.v("CollectingData","in activity: " + absFilepathModel);
				weka.core.SerializationHelper.write(absFilepathModel, myJ48tree);


				Evaluation eTest = new Evaluation(data);
				eTest.evaluateModel(myJ48tree, data);
				String summary = eTest.toSummaryString();
				//Log.v("CollectingData", summary);

				//The previously saved model is deserialized


				//Classifier cls = (Classifier) weka.core.SerializationHelper.read(absFilepathModel);
				//Log.v("CollectingData", "Can inflate the model ");
				//CentralKnowledge.classifier = cls ;
				//CentralKnowledge.willUseDefaultModel(false);


			} catch (Exception e) {
				//Log.v("CollectingData","...");
			}
			//Log.v("CollectingData","Training part has been done...");

			Toast.makeText(TrainModelActivity.this, "Training has been successful.", Toast.LENGTH_SHORT).show();




			/* to log the newUserARFF.arff file */
			/*
			BufferedReader actreader = null;
			FileOutputStream newArff = null ;
			OutputStreamWriter newWriter = null ; 
			File checkFile2 = new File("/data/data/com.mybo.nanaAndme/files/newUserARFF.arff");
			try {
				if (checkFile2.exists()){
					checkFile2.delete();
					//Log.v("exist","newUserARFF has been deleted.");
				}
				newArff = openFileOutput("newUserARFF.arff", MODE_WORLD_READABLE);
				newWriter = new OutputStreamWriter(newArff);
				actreader = new BufferedReader(new FileReader(fullFileNameARFF));
			} catch (FileNotFoundException e1) {
				//Log.v("filename","local file can not found");
			}
			String str ; 
			try{
				while ((str=actreader.readLine()) != null){
					newWriter.write(str+ "\n");
				}
				newWriter.close();
				actreader.close();
			}catch (IOException e) {
				//Log.v("CollectingData","Cannot read from userARFF.arff.");
			}
			*/


		}

	}

	//inner class
	private class ResetListener implements OnClickListener{

		public void onClick(View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(TrainModelActivity.this);
			builder.setMessage("Are you sure you want to reset?")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

					buttonTrain.setEnabled(false);
					CentralKnowledge.willUseDefaultModel(true);
					//Log.v("CollectingData","About to reset the userarff file");
					//Log.v("CollectingData", "Check the existence of the userARFF file " + CentralKnowledge.getFilepathARFF());

					//check if userARFF file exists
					//File checkFile = new File(CentralKnowledge.getFilepathARFF());
					//Log.v("CollectingData","The filepath for userARFF is "+ CentralKnowledge.getFilepathARFF());
					if(checkFile.exists()){
						//Can delete
						checkFile.delete();



						//Log.v("CollectingData","Exist?? "+ checkFile.exists());
					}else{
						//Nothing to delete
						//Log.v("CollectingData","Nothing to delete.");
					}


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
}
