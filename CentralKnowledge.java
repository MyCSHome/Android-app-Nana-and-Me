package com.mybo.nanaAndme;



import java.util.ArrayList;





public class CentralKnowledge {


	public enum ActivityType {NONE,RESTING, WALKING, RUNNING, CYCLING, UPSTAIRS};
	public static ArrayList<Integer> activityList = new ArrayList<Integer>(3);
	public static ArrayList<Integer> timeList = new ArrayList<Integer>(3);
	private static double sumMETs = 0.0 ;
	private static ActivityType lastActivity = ActivityType.NONE ; 
	private static int weight = 55 ; 
	private static int restCounter = 0  ;
	private static int walkCounter = 0  ;
	private static int runCounter = 0 ;
	private static int cycCounter = 0 ; 
	private static int upCounter = 0 ; 
	private static String label = "" ; 
	private static String absoluteFilepathARFF ;
	private static String absoluteFilepathModel;
	private static boolean useDefaultModelFlag = true  ;
	public static int targetHour = 0 ;
	public static int targetMinute = 0 ;
	private static String startingTime ;
	public static boolean hasStartedPickerOnce = false ;
	public static String lastwordfromNana = "Nana says ..";
	
	public static void setStartingTime(String detail){
		startingTime = detail ;
		hasStartedPickerOnce = true;
	}
	public static String getStartingTime(){
		return startingTime ;
	}
	
	public static void resetStartingTime(){
		startingTime = "" ;
		hasStartedPickerOnce = false;
	}
	
	

	public static boolean isUsingDefaultModel(){
		return useDefaultModelFlag ; 
	}

	public static void willUseDefaultModel(boolean b){
		useDefaultModelFlag = b; 
	}

	public static void setLabel(String s){
		label = s;
	}

	public static String getLabel(){
		return label;
	}

	public static void setFilepathARFF(String s){
		absoluteFilepathARFF = s;
	}

	public static String getFilepathARFF(){
		return absoluteFilepathARFF;
	}

	public static void setFilepathModel(String s){
		absoluteFilepathModel = s;
	}

	public static String getFilepathModel(){
		return absoluteFilepathModel;
	}


	public static ActivityType getLastActivity(){
		return lastActivity;		
	}

	public static void rebornSetLastActivity(int a){
		switch(a){

		case 0:
			lastActivity =  ActivityType.RESTING ;
			break ;
		case 1:
			lastActivity =  ActivityType.WALKING ;
			break ;
		case 2:
			lastActivity =  ActivityType.RUNNING ;
			break ;
		case 3:
			lastActivity =  ActivityType.CYCLING ;  
			break ;
		case 4:
			lastActivity =  ActivityType.UPSTAIRS ;
			break ;
		}


	}

	public static int getActivityType_Int(ActivityType a){
		int intvalue = -1 ;

		switch(a){
		case NONE:
			intvalue=  -1 ;
			break ;
		case RESTING:
			intvalue=  0 ;
			break ;
		case WALKING:
			intvalue=  1 ;
			break ;
		case RUNNING:
			intvalue=  2 ;
			break ;
		case CYCLING:
			intvalue=  3 ;
			break ;
		case UPSTAIRS:
			intvalue=  4 ; 
			break ;

		}

		return intvalue ;

	}


	public static void updateLastActivity(ActivityType ac){
		lastActivity = ac;

		if(lastActivity == ActivityType.RESTING){
			sumMETs = sumMETs + (1.0*weight)/(60*12) ; 
			restCounter++ ; 

		}else if(lastActivity == ActivityType.WALKING){
			sumMETs = sumMETs + (2.5*weight)/(60*12) ;
			walkCounter++ ;

		}else if(lastActivity == ActivityType.RUNNING){
			sumMETs = sumMETs + (7.0*weight)/(60*12) ;
			runCounter++ ;

		}else if(lastActivity == ActivityType.CYCLING){
			sumMETs = sumMETs + (6.0*weight)/(60*12) ;
			cycCounter++ ;

		}else{ //UPSTAIRS
			sumMETs = sumMETs + (8.0*weight)/(60*12) ;
			upCounter++ ;

		}

	}

	public static double getCurrentSumMETs(){
		return sumMETs;
	}


	public static void rebornCurrentSumMETs(double rebornVal){
		sumMETs = rebornVal;
	}


	public static void resetInferActivityList(){

		//reset everything

		sumMETs = 0;
		lastActivity = ActivityType.NONE ; 
		restCounter = 0 ; 
		walkCounter = 0 ; 
		runCounter = 0 ; 
		cycCounter = 0 ; 
		upCounter = 0 ;
		resetStartingTime();
	}

	public static int getRestCounter(){
		return restCounter ; 
	}
	public static int getWalkCounter(){
		return walkCounter ; 
	}
	public static int getRunCounter(){
		return runCounter ; 
	}
	public static double getCyclingCounter(){
		return cycCounter ;
	}
	
	public static double getUpstairsCounter(){
		return upCounter ;
	}

	public static double reportMETs(){
		return sumMETs ;
	}



	//add new classified activity into the list
	//currentAct == 0 when resting, ==1 when walking, == 2 when running

	/*public static void addInferActivityToList(int currentAct){

		inferActivityList.add(currentAct);
		//updateLastActivity(currentAct);

		Calendar c = Calendar.getInstance(); 
		int minute = c.get(Calendar.MINUTE);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int second = c.get(Calendar.SECOND);
		int secTime =hour*60*60+minute*60+second;

		timeList.add(secTime);

		//Log.v("Time", " "+ hour +" "+ minute+" "+second);
		//Log.v("Time", " " +secTime);
		int hour1 = (int) (secTime/3600) ;
		int min = (int) ((secTime - hour*3600)/60) ;
		//Log.v("Time", "h "+hour1);
		//Log.v("Time", "m "+min);
	}*/

	//get the latest activity in the list
	/*public static int getLastElementInList(){

		////Log.d("CentralKnowledgeKeeper",Integer.toString(inferActivityList.size()) );

		if(inferActivityList.size()==0){
			////Log.v("CentralKnowledgeKeeper", "size is zero.");
			return -1; 
		}
		else{
			////Log.v("return index", Integer.toString(inferActivityList.get(inferActivityList.size()-1)));
			return inferActivityList.get(inferActivityList.size()-1);	
		}
	}*/

	/*public static int getLengthActivityList(){
	return inferActivityList.size();
}

public static void clearActivityAndTimeList(){
	inferActivityList.clear();
	timeList.clear();
}*/








}
