package com.mybo.nanaAndme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

import weka.classifiers.Classifier;
import weka.core.Instances;

import com.mybo.nanaAndme.CentralKnowledge.ActivityType;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;




public class HelloIntentService extends IntentService implements SensorEventListener{


	private PowerManager powerMan ;
	private final String activityFile = "act_time.txt";
	private String toPredictArffFilename = "toPredict.arff";
	private String savedInfo = "savedInfo.txt";

	public PrintWriter writer;
	public OutputStreamWriter o;
	public SensorManager manager;
	public Sensor accelerometer;
	public static long endTime;
	private Classifier cls;
	private double latestActivity = -1;
	private int intPredicted = -1 ; 
	private OutputStreamWriter osw_act ;

	private boolean hasWrittenFirstActivity ; 
	private int rightNow ;
	private int latestPredicted ;
	public static int smallSamplingCycle = 5000 ; 



	public HelloIntentService() {
		super("HelloIntentService");
	}



	@Override
	protected void onHandleIntent(Intent intent) {


		String rawdataFilename = null;

		Double predictedActivity = 0.0 ;
		hasWrittenFirstActivity = false ; 

		/* Check what model is being used */
		if(CentralKnowledge.isUsingDefaultModel()==false){

			String modelFilepath = CentralKnowledge.getFilepathModel();
			////Log.v("CollectingData","Use customized model.");
			////Log.v("CollectingData","in intent: " + modelFilepath);

			try {
				cls = (Classifier) weka.core.SerializationHelper.read(modelFilepath);
				//Log.v("CollectingData","cls is at " + modelFilepath + " and it can be inflated.");
			} catch (Exception e3) {
				//Log.v("CollectingData","Cannot inflate.");
			}
		}else{
			//Log.v("CollectingData","Use default model.");
		}




		/* Prepare the outputStream for act/time files */
		try {
			FileOutputStream fos_act = openFileOutput(activityFile, MODE_WORLD_READABLE);
			osw_act = new OutputStreamWriter(fos_act); 

		} catch (FileNotFoundException e) {
			//Log.v("CollectingData","FileOutputStream cannot be created.");
		}





		powerMan= (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = powerMan.newWakeLock(
				powerMan.PARTIAL_WAKE_LOCK, "My wakelook");
		// This will make the screen and power stay on
		// This will release the wakelock after 1000 ms
		wakeLock.acquire();




		while (System.currentTimeMillis() < endTime) {

			try {
				rawdataFilename = "0.txt";
				FileOutputStream f = openFileOutput(rawdataFilename, MODE_WORLD_READABLE);
				//Log.v("CollectingData",rawdataFilename);
				o = new OutputStreamWriter(f); 
			} catch (Exception e1) {
				//Log.v("CollectingData","file output stream cannot establish");
			}

			try {
				manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
				accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

				long intTime = System.currentTimeMillis() + smallSamplingCycle; 
				while (System.currentTimeMillis() < intTime) {
					//wait for 5 seconds to collect raw data
				}
				o.write("\n");
				o.flush();
				o.close();
				
				//Log.v("time","0.txt has been written at " + System.currentTimeMillis() );
			}
			catch (Exception e2) {
				//Log.v("CollectingData","failure during small cycles");
			}





			/* classify */





			//check model flag
			if(CentralKnowledge.isUsingDefaultModel()==true){
				predictedActivity = useDefaultModel(rawdataFilename);
				if(predictedActivity== -1.0){
					//Log.v("CollectingData","Classifier with default model does not work.");
					predictedActivity = 0.0 ; //force to be RESTING
				}
			}else{
				//use customized model
				predictedActivity = useCustomizedModel(rawdataFilename);

				if(predictedActivity== -1.0){
					//Log.v("CollectingData","Classifier with customized model does not work.");
					predictedActivity = 0.0 ; //force to be RESTING
				}
			}

			////Log.v("CollectingData","Predicted as " + Double.toString(predictedActivity));

			latestPredicted = CentralKnowledge.getActivityType_Int(CentralKnowledge.getLastActivity()) ;

			intPredicted = predictedActivity.intValue();

			//Log.v("time",intPredicted + " has been classified at " + System.currentTimeMillis() );

			//once we get the predictedActivity
			if (intPredicted==0){
				CentralKnowledge.updateLastActivity(ActivityType.RESTING);
			}else if (intPredicted==1){
				CentralKnowledge.updateLastActivity(ActivityType.WALKING);
			}else if (intPredicted==2){
				CentralKnowledge.updateLastActivity(ActivityType.RUNNING);
			}else if (intPredicted==3){
				CentralKnowledge.updateLastActivity(ActivityType.CYCLING);
			}else { //UPSTAIRS
				CentralKnowledge.updateLastActivity(ActivityType.UPSTAIRS);
			}




			if((hasWrittenFirstActivity == true)  && (CentralKnowledge.activityList.size() < 3) && (intPredicted != latestPredicted)){

				//add info into 2 arrayLists

				CentralKnowledge.activityList.add(intPredicted);
				//Log.v("CollectingData","Add the act list with " + intPredicted);

				rightNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*3600 + Calendar.getInstance().get(Calendar.MINUTE)*60
						+ Calendar.getInstance().get(Calendar.SECOND);

				CentralKnowledge.timeList.add(rightNow);
				//Log.v("CollectingData","Add the time list with " + rightNow);
			}


			if(hasWrittenFirstActivity == false){
				//this is the first time of activity
				rightNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*3600 + Calendar.getInstance().get(Calendar.MINUTE)*60
						+ Calendar.getInstance().get(Calendar.SECOND);

				try {
					osw_act.write(intPredicted + "\n");
					////Log.v("CollectingData","Write act: " + intPredicted + " into file.");
					osw_act.write(rightNow + "\n");
					////Log.v("CollectingData","Write time: " + rightNow + " into file.");

				} catch (IOException e) {
					//Log.v("CollectingData","Cannot write onto osw act or time.");
				}
				hasWrittenFirstActivity = true ;
			}

			//Log.v("CollectingData","Size of act list = "+ CentralKnowledge.activityList.size());

			if (CentralKnowledge.activityList.size() == 3){
				//write onto 2 files
				try {

					for(int k = 0 ; k < 3 ; k++){
						osw_act.write(CentralKnowledge.activityList.get(k) + "\n");
						////Log.v("CollectingData","Write act: " + CentralKnowledge.activityList.get(k) + " into file.");
						osw_act.write(CentralKnowledge.timeList.get(k) + "\n");
						////Log.v("CollectingData","Write time: " + CentralKnowledge.timeList.get(k) + " into file.");
					}

				} catch (IOException e) {
					//Log.v("CollectingData","Cannot write onto osw act or time.");
				}
				//delete arrayLists
				CentralKnowledge.activityList.clear();
				CentralKnowledge.timeList.clear();

				//Log.v("CollectingData","Lists are cleared " + CentralKnowledge.activityList.size() + " & " + CentralKnowledge.timeList.size());
			}




		}
		
		manager.unregisterListener(this);
		flushArrayListToFile();
		//wakeLock.release();
		
		
		
		
		String message = "" ;
		
			

			double met = CentralKnowledge.reportMETs() ; 
			double totalTime ;
			
			double walkMin = (double)(CentralKnowledge.getWalkCounter()*5)/60 ;
			double runMin  = (double)(CentralKnowledge.getRunCounter()*5)/60 ;
			double restMin = (double)(CentralKnowledge.getRestCounter()*5)/60 ;
			
			
			//default mode
			if(CentralKnowledge.isUsingDefaultModel()==true){ 
				totalTime = restMin +  walkMin + runMin;
				
				message = "Today we've spent " + String.format("%.1f", met) +" kcal within "
						+ String.format("%.1f", totalTime)  + " minutes. We ran for " + String.format("%.1f", runMin) 
						+ ",  walked for " + String.format("%.1f", walkMin) + ",  and chilled out for " 
						+ String.format("%.1f", restMin) + " minutes." ; 


			}else{
				double cycMin = (double)(CentralKnowledge.getCyclingCounter()*5)/60 ;
				double upMin = (double)(CentralKnowledge.getUpstairsCounter()*5)/60 ;

				totalTime = restMin + walkMin + runMin + cycMin + upMin;

			
				message = "Today we've spent " + String.format("%.1f", met) +" kcal within "
						+ String.format("%.1f", totalTime)  + " minutes. We ran for " + String.format("%.1f", runMin) 
						+ ",  walked for " + String.format("%.1f", walkMin)
						+ ",  cycled for " + String.format("%.1f", cycMin) 
						+ ",  climbed stairs for " + String.format("%.1f", upMin) 
						+ ",  and chilled out for " + String.format("%.1f", restMin) + " minutes." ; 

			}
			CentralKnowledge.lastwordfromNana = message ;
			//reportTextView.setText(CentralKnowledge.lastwordfromNana);

		
		
		/* write onto a file */
		
		try {
			
			FileOutputStream savedInfo_fos = openFileOutput(savedInfo, MODE_WORLD_READABLE);
			OutputStreamWriter savedInfo_osw = new OutputStreamWriter(savedInfo_fos);
			savedInfo_osw.write( String.format("%.1f", CentralKnowledge.reportMETs() ) + "\n");
			savedInfo_osw.write(message + "\n");
			savedInfo_osw.flush();
			savedInfo_osw.close();
			
		} catch (FileNotFoundException e) {
			//Log.v("CollectingData",rawdataFilename);
		} catch (IOException e) {
			//Log.v("CollectingData",rawdataFilename);
		}
		

		
		
		
		
		
		
		wakeLock.release();
		
		
		
		
		

		/* TO TEST */
		/*Intent in = new Intent();
	    in.setAction("REBORN_ACTION");
	    in.putExtra("endtime", endTime+10000);
	    in.putExtra("sumMETs", CentralKnowledge.getCurrentSumMETs());
	    in.putExtra("lastActivity", CentralKnowledge.getActivityType_Int(CentralKnowledge.getLastActivity()) );
	    sendBroadcast(in);
	    //Log.v("CollectingData","REBORN_ACTION is sent at the end."); */


		//Log.v("CollectingData","service finished.");

		/* read from act_time.txt file */
		/*
		BufferedReader actreader = null;
		FileInputStream actis = null;
		try {
			actis = openFileInput(activityFile);
			actreader = new BufferedReader(new InputStreamReader(actis));
		} catch (FileNotFoundException e1) {
			//Log.v("filename","local file can not found");
		}
		String str ; 
		try{
			while ((str=actreader.readLine()) != null){
				//Log.v("CollectingData","act_time txt : " + str + "\n");
			}
		}catch (IOException e) {
			//Log.v("CollectingData","Cannot read from act_time txt.");
		}
		*/






	}

	private void flushArrayListToFile(){
		try {
			if(CentralKnowledge.activityList.size() != 0 ){
				for(int k = 0 ; k < CentralKnowledge.activityList.size() ; k++){
					osw_act.write(CentralKnowledge.activityList.get(k) + "\n");
					osw_act.write(CentralKnowledge.timeList.get(k) + "\n");	
				}
			}
			osw_act.write(intPredicted + "\n");
			rightNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)*3600 + Calendar.getInstance().get(Calendar.MINUTE)*60
					+ Calendar.getInstance().get(Calendar.SECOND);
			osw_act.write(rightNow + "\n");
			osw_act.flush();
			osw_act.close();

			CentralKnowledge.activityList.clear();
			CentralKnowledge.timeList.clear();

		} catch (IOException e) {
			//Log.v("CollectingData","Cannot close the osw_act / time.");
		}
	}

	public void onDestroy(){

		super.onDestroy();
		//Log.v("Process","intentservice destroy");
		
		/*
		if(System.currentTimeMillis() < endTime){
			//Log.v("CollectingData","OnDestroy is called prematurely.");
			flushArrayListToFile(); 

			Intent in = new Intent();
		    in.setAction("REBORN_ACTION");
		    in.putExtra("endtime", endTime+10000);
		    in.putExtra("sumMETs", CentralKnowledge.getCurrentSumMETs());
		    in.putExtra("lastActivity", CentralKnowledge.getActivityType_Int(CentralKnowledge.getLastActivity()) );
		    sendBroadcast(in);
		    //Log.v("CollectingData","Broacast is sent"); */
		/*
			try {
				//Log.v("CollectingData","OnDestroy is called prematurely.");
				flushArrayListToFile();


				if (CentralKnowledge.isUsingDefaultModel()==true){
					o_reborn.write(1 + "\n");
				}else{
					o_reborn.write(0 + "\n");
				}
				o_reborn.write(Double.toString(CentralKnowledge.getCurrentSumMETs()) + "\n");
				o_reborn.write(CentralKnowledge.getActivityType_Int(CentralKnowledge.getLastActivity())+ "\n");
				o_reborn.flush();
				o_reborn.close();

			} catch (IOException e) {
				//Log.v("CollectingData","IntentService is destroyed but not yet finished its job.");
			}



		}else{
			flushArrayListToFile();
			//Log.v("CollectingData","Mature call for onDestroy().");


		    ////Log.v("CollectingData","REBORN_ACTION is sent from mature.");

			/*try {
				//Log.v("CollectingData","Mature call for onDestroy().");
				o_reborn.write("");
				o_reborn.close();
			} catch (IOException e) {
				//Log.v("CollectingData","o_reborn cannot write \n into file.");
				e.printStackTrace();
			}
		} */
		
	}

	private double useDefaultModel(String rawdatafilename){

		Double predicted = -1.0 ; 
		//BufferedReader reader = null;
		//FileInputStream is = null;

		File fpth1 = getFilesDir();
		String absRawDataFilepath1 = fpth1.getAbsolutePath() + '/' + rawdatafilename ; 

		//one file leads to one vector
		CreateAttributeWeka CreAttVec = new CreateAttributeWeka(absRawDataFilepath1);
		ArrayList<Double> vector = CreAttVec.outputAttributeVector();

		
		
		try {
			//Classify from a vector
			predicted = J48Classifier.classify(vector.toArray());
		} catch (Exception e) {
			//Log.v("Classifier","Classifier does not work.");
		}
		
		return predicted ;
		
		

		
	}

	private double useCustomizedModel(String rawdatafilename){

		//on the fly arff for one instance
		Double predicted = -1.0 ; 
		
		FileOutputStream toPredictFOS;
		File fpth1 = getFilesDir();
		String absRawDataFilepath1 = fpth1.getAbsolutePath() + '/' + rawdatafilename ; 

		try {
			toPredictFOS = openFileOutput(toPredictArffFilename, MODE_WORLD_READABLE);
			OutputStreamWriter toPredictWriter = new OutputStreamWriter(toPredictFOS);

			ArffGenerator arffBuilder = new ArffGenerator(absRawDataFilepath1, toPredictWriter);
			arffBuilder.makeHeader();
			arffBuilder.extendARFF();
			toPredictWriter.close();


		} catch (FileNotFoundException e) {
			//Log.v("CollectingData","Cannot do openFileOutput and OutputStreamWriter.");
		} catch (IOException e) {
			//Log.v("CollectingData","Cannot close the OutputStreamWriter.");
		}



		//read on-the-fly arff for one instance
		BufferedReader arffreader = null; 
		Instances unlabeled  = null;
		try {
			String abdFPTH = fpth1.getAbsolutePath() + '/' + toPredictArffFilename;
			//Log.v("CollectingData","toPredictArffFilename is " + abdFPTH);
			arffreader = new BufferedReader(new FileReader(abdFPTH));
			//Log.v("CollectingData","arffreader = ok");
			unlabeled  = new Instances(arffreader);
			//Log.v("CollectingData","new Instances() = ok");
			arffreader.close();
			//Log.v("CollectingData","arffreader.close() = ok");
			unlabeled.setClassIndex(unlabeled.numAttributes() - 1);

			//Classify the instance
			predicted = cls.classifyInstance(unlabeled.instance(0));
			//Log.v("CollectingData","cls.classifyInstance predicts as " + predicted);
		}
		catch(Exception e) {
			//Log.v("CollectingData",".....");
		}



		return predicted ;

	}




	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// can be safely ignored for now
	}



	public void onSensorChanged(SensorEvent event) {

		try {
			//writer.println(event.values[0] + "\n" + event.values[1] + "\n"
			//		+ event.values[2]);
			//writer.flush();
			o.write(event.values[0] + "\n" + event.values[1] + "\n"
					+ event.values[2]+ "\n");
			o.flush();
		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}


}





