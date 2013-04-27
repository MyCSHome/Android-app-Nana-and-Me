package com.mybo.nanaAndme;

import java.util.ArrayList;


class SimpleMovingAverage {
	
	
	public static ArrayList<Double> getFilterOutput(ArrayList<Double> dataSet, int window){
		double sum = 0.0 ;
			
		ArrayList<Double> output = new ArrayList<Double>();
			
		for (int i = 0 ; i < window ; i++){ 
			/*System.out.println(dataSet.size());
			System.out.println(window);*/
			sum += dataSet.get(i) ;
		}
		
		output.add(sum/window) ;
		
			
		for(int i = window ; i < dataSet.size() ; i++){
			sum = sum + dataSet.get(i) - dataSet.get(i-window);
			output.add(sum/window) ;
		}
		return output ; 
		
	}
	

}