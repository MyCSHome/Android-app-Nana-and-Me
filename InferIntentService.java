package com.mybo.nanaAndme;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;


public class InferIntentService extends IntentService implements SensorEventListener{

	public PrintWriter writer;
	public Socket sock;
	public OutputStreamWriter o;

	public Handler handler1 = new Handler();
	public SensorManager manager;
	public Sensor accelerometer;
	public static long endTime ;
	Classifier cls;
	String rawdataFilename = "0.txt";


	public InferIntentService() {
		super("InferIntentService");
	}



	@Override
	protected void onHandleIntent(Intent intent) {


		int smallCycle = 5000;
		String modelFilepath = CentralKnowledge.getFilepathModel();
		//Log.v("CollectingData","in intent: " + modelFilepath);

		try {
			cls = (Classifier) weka.core.SerializationHelper.read(modelFilepath);
		} catch (Exception e3) {
			//Log.v("CollectingData","Cannot inflate.");
		}


		//Log.v("CollectingData","fff");
		endTime = System.currentTimeMillis() + 20000 ; 

		while (System.currentTimeMillis() < endTime) {

			//Log.v("CollectingData","while start");

			try {
				FileOutputStream f = openFileOutput(rawdataFilename, MODE_WORLD_READABLE);
				o = new OutputStreamWriter(f); 

			} catch (Exception e1) {
				//Log.v("CollectingData","fileoutputstream cannot establish");
			}


			manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
			accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

			long intTime = System.currentTimeMillis() + smallCycle; 
			while (System.currentTimeMillis() < intTime) {
			}



			try{
				o.write("\n");
				o.flush();
				o.close();
			}catch(Exception e){};


			/* classify */


			/*BufferedReader reader = null;
			FileInputStream is = null;
			try {
				is = openFileInput(rawdataFilename);
				reader = new BufferedReader(new InputStreamReader(is));
			} catch (FileNotFoundException e1) {
				//Log.v("filename","local file can not found");
			}


			ArrayList<Double> vector = AttCreator.outputAttributeVector(reader);

			try {
				reader.close();
			} catch (IOException e1) {
				//Log.v("readerClosing","Cannot close the reader.");
			}*/

			//ArrayList<Double> newVec = new ArrayList<Double>();
			//newVec.add(vector.get(0));
			//newVec.add(vector.get(1));
			//newVec.add(vector.get(2));
			//newVec.add(new Double(1.0));

			//int numAttributeForInstance = vector.size() + 1;


			//on the fly arff for one instance
			String toPredictArffFilename = "toPredict.arff";
			FileOutputStream toPredictFOS;

			File fpth1 = getFilesDir();
			String absRawDataFilepath1 = fpth1.getAbsolutePath() + '/' + rawdataFilename ; 

			try {
				toPredictFOS = openFileOutput(toPredictArffFilename, MODE_WORLD_READABLE);
				OutputStreamWriter toPredictWriter = new OutputStreamWriter(toPredictFOS);

				ArffGenerator arffBuilder = new ArffGenerator(absRawDataFilepath1, toPredictWriter);
				arffBuilder.makeHeader();
				arffBuilder.extendARFF();
				toPredictWriter.close();
				//Log.v("CollectingData","arff successful");

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
				
				arffreader = new BufferedReader(new FileReader(abdFPTH));
				
				unlabeled  = new Instances(arffreader);
				
				arffreader.close();
				
				unlabeled.setClassIndex(unlabeled .numAttributes() - 1);

				//Classify the instance

				double clsLabel = cls.classifyInstance(unlabeled.instance(0));

				//Log.v("CollectingData","Predicted as (Double) " + clsLabel);
			}
			catch(Exception e) {
				//Log.v("CollectingData",".....");
			}



		}

		manager.unregisterListener(this);
		//Log.v("CollectingData","done..........");



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





