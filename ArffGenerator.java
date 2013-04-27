package com.mybo.nanaAndme;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import android.util.Log;

public class ArffGenerator {

	
	private String filepath ;
	private String label = "RESTING"; 
	private OutputStreamWriter outputStrmWriter ;



	//constructor with a specific label
	public ArffGenerator(String fp, OutputStreamWriter out, String l){
		filepath = fp ; 
		outputStrmWriter = out ;
		label = l ; 
	}

	//constructor without a specific label
	public ArffGenerator(String fp, OutputStreamWriter out){
		filepath = fp ;  
		outputStrmWriter = out ; 
	}


	//generate ARFF file
	public void extendARFF(){


		//assume the heading has existed


		//Log.v("CollectingData",filepath);

		//one file leads to one vector
		CreateAttributeWeka CreAttVec = new CreateAttributeWeka(filepath);
		ArrayList<Double> vector = CreAttVec.outputAttributeVector();

		for(int j = 0 ; j < vector.size() ; j++){

			try {
				outputStrmWriter.write( Double.toString(vector.get(j) ) + ",");
				//Log.v("CollectingData","haha"+j);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


		//after one vector, add newline 
		try {
			outputStrmWriter.write(label);
			outputStrmWriter.write("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



	// Be careful, this header has "OTHERS" label additionally
	void makeHeader(){

		try {
			outputStrmWriter.write("@RELATION activity"+ "\n"+ "\n" );
			outputStrmWriter.write("@ATTRIBUTE meanvertical  NUMERIC" + "\n");
			outputStrmWriter.write("@ATTRIBUTE sdvertical  NUMERIC" + "\n");
			outputStrmWriter.write("@ATTRIBUTE meanhorizontal  NUMERIC" + "\n");
			outputStrmWriter.write("@ATTRIBUTE sdhorizontal  NUMERIC" + "\n");
			outputStrmWriter.write("@ATTRIBUTE numPeak  NUMERIC" + "\n");
			outputStrmWriter.write("@ATTRIBUTE class  {RESTING,WALKING,RUNNING,CYCLING,UPSTAIRS}" + "\n" + "\n");
			outputStrmWriter.write("@DATA" + "\n");
			//fileWriter.close();
			//Log.v("CollectingData","finish");

		} catch (IOException e) {
			//Log.v("CollectingData","Cannot make header.");
		}
	}



}