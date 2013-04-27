package com.mybo.nanaAndme;



import java.util.ArrayList;


public class OrientationHandler {
	
	private ArrayList<Double> data_x ;
	private ArrayList<Double> data_y ;
	private ArrayList<Double> data_z ;
	private ArrayList<Double> innerProd ;
	private ArrayList<Double> horizon ;
	private double norm_x;
	private double norm_y;
	private double norm_z;
	
	//constructor
	OrientationHandler(ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> z){
		data_x = x ; 
		data_y = y ; 
		data_z = z ;
		calculateVerticalComponent();
		calculateHorizontalComponent();
	}
	
	private void calculateVerticalComponent(){
		
		double mx = calculateMean(data_x);
		double my = calculateMean(data_y);
		double mz = calculateMean(data_z);
		double sz = Math.sqrt(mx*mx + my*my + mz*mz);
		
		//normalize 
		norm_x = mx/sz;
		norm_y = my/sz;
		norm_z = mz/sz;
		
		//for every point of samples
		innerProd = new ArrayList<Double>();
		
		for (int i = 0 ; i < data_x.size(); i++){
			
			//do inner product
			innerProd.add(data_x.get(i)*norm_x + data_y.get(i)*norm_y + data_z.get(i)*norm_z); 	
		}

	}
	
	
	private void calculateHorizontalComponent(){
		
		horizon = new ArrayList<Double>();
		double h_x;
		double h_y; 
		double h_z;
		
		for(int i=0 ; i < data_x.size() ; i++){

			h_x = data_x.get(i) - innerProd.get(i)*norm_x; ;
			h_y = data_y.get(i) - innerProd.get(i)*norm_y; ;
			h_z = data_z.get(i) - innerProd.get(i)*norm_z; ;
			
			horizon.add(Math.sqrt(h_x*h_x + h_y*h_y + h_z*h_z));
		}
	}
	
	
	
	
	public ArrayList<Double> getVerticalComponent(){
		return innerProd ;
	}
	
	public ArrayList<Double> getHorizontalComponent(){
		return horizon ;
	}
	
	
	double calculateMean(ArrayList<Double> rawData){

		double sum = 0.0 ;
		int axisLength = rawData.size();


		for(int i = 0 ; i < axisLength ; i++){
			sum = sum + rawData.get(i);
		}

		return sum/axisLength;
	}

}
