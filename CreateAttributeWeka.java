package com.mybo.nanaAndme;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class CreateAttributeWeka {

	private String filename;
	private BufferedReader reader = null;
	private final int WINDOW = 5 ; 


	//	public static void main(String[] args){
	//		
	//		//to test
	//		CreateAttributeWeka CreAttVec = new CreateAttributeWeka("rsc/test_test.txt");
	//		ArrayList<Double> vector = CreAttVec.outputAttributeVector();
	//		
	//		for(int i=0; i<vector.size() ;i++){
	//			System.out.println(vector.get(i) + "\n");
	//		}
	//		
	//	}

	//constructor
	public CreateAttributeWeka(String f){
		filename = f; 

		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Double> outputAttributeVector(){

		ArrayList<Double> dataSet_X = new ArrayList<Double>() ;
		ArrayList<Double> dataSet_Y = new ArrayList<Double>() ;
		ArrayList<Double> dataSet_Z = new ArrayList<Double>() ;
		String str ;
		ArrayList<Double> vector = new ArrayList<Double>();


		//read the file and output 3 ArrayLists of x,y,z
		try {
			while ((str = reader.readLine()) != null){

				dataSet_X.add(Double.parseDouble(str));
				dataSet_Y.add(Double.parseDouble(reader.readLine()));
				dataSet_Z.add(Double.parseDouble(reader.readLine()));
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			System.out.println(filename);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		try{
			//smooth data before calculating the number of peak
			dataSet_X = SimpleMovingAverage.getFilterOutput(dataSet_X, WINDOW);
			dataSet_Y = SimpleMovingAverage.getFilterOutput(dataSet_Y, WINDOW);
			dataSet_Z = SimpleMovingAverage.getFilterOutput(dataSet_Z, WINDOW);
		}catch(Exception e){System.out.println("Problem is in this " + filename + "\n");}


		//now we get the complete set of each axis x,y,z
		//double sd_x = calculateSD(dataSet_X);
		//double sd_y = calculateSD(dataSet_Y);
		//double sd_z = calculateSD(dataSet_Z);





		//calculate vertical and horizontal components
		OrientationHandler orientHandler = new OrientationHandler(dataSet_X,dataSet_Y, dataSet_Z);
		ArrayList<Double> vertical_dataSet = orientHandler.getVerticalComponent();
		ArrayList<Double> horizon_dataSet = orientHandler.getHorizontalComponent();

		
		
		
		
		double meanVal = calculateMean(vertical_dataSet);
		double sdVal = calculateSD_2(vertical_dataSet, meanVal);
		
		vector.add(meanVal);
		vector.add(sdVal);
		
		meanVal = calculateMean(horizon_dataSet);
		sdVal = calculateSD_2(horizon_dataSet, meanVal);
		
		vector.add(meanVal);
		vector.add(sdVal);

		int numPeak = PeakDetector.findPeakFrom1D_data(vertical_dataSet);
		vector.add((double)(numPeak));

		

		
		/*
		double sd_Vertical = calculateSD(vertical_dataSet);
		double magnitude_mean = calculateMean(horizon_dataSet);
		int numPeak = PeakDetector.findPeakFrom1D_data(vertical_dataSet);
		vector.add(sd_Vertical);
		vector.add(magnitude_mean);
		vector.add((double)(numPeak));
		*/

		return vector;
	}




	//for now, get the average numPeak to be used 
	int getFinalPeak(int x, int y, int z){

		//int min = 0; 
		//min = Math.min(x,y);
		//min = Math.min(min,z);
		//return min;

		return (x+y+z)/3 ; 



	}

	//method: calculate the standard deviation
	/*
	double calculateSD(ArrayList<Double> rawData){

		double sd = 0.0 ; 
		double sum = 0.0;
		int axisLength = rawData.size();
		double mean = calculateMean(rawData);


		for(int i = 0 ; i < axisLength ; i++){
			double temp = rawData.get(i) - mean;
			sum = sum + (temp*temp) ;
		}


		if(sum>=0){
			sd = Math.sqrt(sum/axisLength);
		}
		else{
			System.out.println("Sum result is negative.");
		}

		return sd ; 

	} */

	double calculateSD_2(ArrayList<Double> rawData, double mean){

		double sd = 0.0 ; 
		double sum = 0.0;
		int axisLength = rawData.size();
		//mean = calculateMean(rawData);


		for(int i = 0 ; i < axisLength ; i++){
			double temp = rawData.get(i) - mean;
			sum = sum + (temp*temp) ;
		}


		if(sum>=0){
			sd = Math.sqrt(sum/axisLength);
		}
		else{
			System.out.println("Sum result is negative.");
		}

		return sd ; 
	}


		//method: calculate the mean
		double calculateMean(ArrayList<Double> rawData){

			double sum = 0.0 ;
			int axisLength = rawData.size();


			for(int i = 0 ; i < axisLength ; i++){
				sum = sum + rawData.get(i);
			}

			return sum/axisLength;
		}



	}
