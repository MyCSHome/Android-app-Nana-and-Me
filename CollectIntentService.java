package com.mybo.nanaAndme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Button;



public class CollectIntentService extends IntentService implements SensorEventListener{


	private final String filename = "0.txt";
	private FileOutputStream f ;
	public OutputStreamWriter o;
	public SensorManager manager;
	public Sensor accelerometer;
	private int smallCycle = 5000;
	private long endTime ;
	private final long cycleTime = 20000;
	private final String arffFilename = "userARFF.arff" ; 


	public CollectIntentService() {
		super("CollectIntentService");
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public void onSensorChanged(SensorEvent event) {
		try {
			o.write(event.values[0] + "\n" + event.values[1] + "\n"
					+ event.values[2]+ "\n");
			o.flush();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}



	@Override
	protected void onHandleIntent(Intent intent) {

		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		manager.registerListener(CollectIntentService.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);



		FileOutputStream arfffileOutputStm = null;
		OutputStreamWriter arffOutStmWrt = null ; 
		File fpth = getFilesDir();
		//String absFilepathUserArff = fpth.getAbsolutePath() + '/' + arffFilename ; 
		String absFilepathUserArff = CentralKnowledge.getFilepathARFF();
		String absRawFilename = fpth.getAbsolutePath() + '/'+ filename ;



		//check if userARFF file exists
		File checkFile = new File(absFilepathUserArff);

		//Log.v("AbsolutePath","Absolute path for arfffile is "+absFilepathUserArff);
		
		if(checkFile.exists()){
			try {
				arfffileOutputStm = openFileOutput(arffFilename, MODE_APPEND);
				arffOutStmWrt = new OutputStreamWriter(arfffileOutputStm);
			} catch (FileNotFoundException e) {
				//Log.v("CollectingData","FileOutputStream cannot be appended.");
			}
			//Log.v("CollectingData","userARFF exists, going to append.");
		}

		else{

			try {
				arfffileOutputStm = openFileOutput(arffFilename, MODE_WORLD_READABLE);
				arffOutStmWrt = new OutputStreamWriter(arfffileOutputStm);
			} catch (FileNotFoundException e) {
				//Log.v("CollectingData","FileOutputStream cannot be generated.");
			}


			/* Copy the file from arff in the asset */
			copyAsset(arffOutStmWrt);

		}


		//CentralKnowledge.setFilepathARFF(absFilepathUserArff);

		ArffGenerator arffGen = new ArffGenerator(absRawFilename, arffOutStmWrt, CentralKnowledge.getLabel());

		endTime = System.currentTimeMillis() + cycleTime ;

		while (System.currentTimeMillis() < endTime){


			//start collecting data from here
			try {	
				f = openFileOutput(filename, MODE_WORLD_READABLE);
				o = new OutputStreamWriter(f);  
			} catch (FileNotFoundException e) {
				//Log.v("CollectingData","Problem with fileOutputStream.");
			}


			long intTime = System.currentTimeMillis() + smallCycle; 
			while (System.currentTimeMillis() < intTime) {

			}

			//then close the file after 5 secs
			try{
				o.write("\n");
				o.flush();
				o.close();
				//Log.v("CollectingData", "0.txt file is saved");
			}
			catch(Exception e){};


			//transform raw data txt file to arff contents

			arffGen.extendARFF();
			//Log.v("CollectingData","Successfully generated !!");

		}


		//close the ARFF outputStreamWriter
		try {
			arffOutStmWrt.close();
		} catch (IOException e2) {
			//Log.v("CollectingData","Cannot close the ARFF outputStreamWriter.");
		}




		/*check arff file content */

		BufferedReader reader = null;
		FileInputStream is = null;
		try {
			is = openFileInput(arffFilename);
			reader = new BufferedReader(new InputStreamReader(is));
		} catch (FileNotFoundException e1) {
			//Log.v("CollectingData","local file can not found");
		}
		String tempStr ; 

		try {
			while ((tempStr = reader.readLine()) != null){
				//Log.v("CollectingData",tempStr);
			}
			reader.close();
		} catch (IOException e) {
			//Log.v("CollectingData","The reader cannot close.");
		}

	}




	private void copyAsset(OutputStreamWriter arffOut){

		InputStream inputStrm;
		BufferedReader rd = null;
		String tempStr ;
		try {
			inputStrm = getAssets().open("ARFF.arff");
			rd = new BufferedReader(new InputStreamReader(inputStrm));
		} catch (IOException e2) {
			//Log.v("CollectingData","local file can not be found");
		}

		//copy the arff file in the asset to new file "arffFilename" variable

		try {
			while ((tempStr = rd.readLine()) != null){
				arffOut.write(tempStr + "\n");
			}
			rd.close();
		}catch (IOException e) {
			//Log.v("CollectingData","local file can not be found");
		}



	}


}




