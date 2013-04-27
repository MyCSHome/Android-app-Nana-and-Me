package com.mybo.nanaAndme;

import java.util.ArrayList;

public class PeakDetector {

	public static int findPeakFrom1D_data(ArrayList<Double> data_1D){
		
		int count = 0 ;
		double previousSlope = 0.0;
		double slope = 0.0 ; 
		
		for (int i = 1 ; i < data_1D.size() ; i++){
			slope = data_1D.get(i) - data_1D.get(i-1); 
			if ((slope*previousSlope < 0) & (slope<-0.1 | previousSlope>0.1) ){
				count++;
			}
			previousSlope = slope;
			
		}
		return count ;
	}

}
