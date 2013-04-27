package com.mybo.nanaAndme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.widget.ImageView;


public class GraphActivity extends Activity{


	private int xMax;
	private int yMax;
	private int xMin;
	private int yMin;
	XYMultipleSeriesDataset dataset; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		XYMultipleSeriesDataset x = getDemoDataset();

		XYMultipleSeriesRenderer y = getDemoRenderer(); 

		GraphicalView gView = null ;
		if (xMax-xMin<1800){
			try{
				gView = ChartFactory.getLineChartView(GraphActivity.this, x, y);}
			catch(Exception e){ 
				e.printStackTrace();
			}
		}else{
			try{
				gView = ChartFactory.getScatterChartView(GraphActivity.this, x, y);}
			catch(Exception e){ e.printStackTrace();
			}
		}

		//Log.v("ha","ddddddddddasfdewr222d");
		setContentView(gView);
	}

	public void onBackPressed(){
		GraphActivity.this.finish();
	}

	private XYMultipleSeriesDataset getDemoDataset() {

		//Log.v("ha","ddddddddddasfdewr333");

		BufferedReader reader = null;
		FileInputStream is = null;
		String line;
		XYSeries series = new XYSeries("activity checking point");
		try {
			File act = new File("/data/data/com.mybo.nanaAndme/files/act_time.txt");
			long length = act.length();	
			is = openFileInput("act_time.txt");
			//Log.v("graph","www: " + length);
		} catch (Exception e) {

			e.printStackTrace();
			//Log.v("graph","aaawww");
		}
		try{
			reader = new BufferedReader(new InputStreamReader(is));
		}catch(Exception e1){
			//Log.v("graph","find you!");

		}


		ArrayList<Integer> d = new ArrayList<Integer>();
		ArrayList<Integer> dd = new ArrayList<Integer>();
		try {

			while ((line = reader.readLine()) != null) {

				//Log.v("graph","hello");

				String b = reader.readLine();
				d.add(Integer.parseInt(b));
				dd.add(Integer.parseInt(line));
				////Log.v("haha","b:  "+Double.parseDouble(b));
			}
			reader.close();
		} catch (Exception e11) {
			//e.printStackTrace();
		} 
		//int timeperiod = d.get(d.size()-1)-d.get(0);
		//Log.v("graph","..."+d.size());
		try{
			FileOutputStream graph = openFileOutput("graphdata.txt", MODE_WORLD_READABLE);
			OutputStreamWriter graphwriter = new OutputStreamWriter(graph);



			int ac =0;
			while(ac<d.size()){

				graphwriter.write(d.get(ac)+"\n"+dd.get(ac)+"\n");
				ac++;
			}
			//Log.v("count","hhh");
			graphwriter.close();
		}catch(Exception e2){
			//Log.v("graph","...");
		}






		int index = d.get(0); //70000
		int i=0;
		//Log.v("haha","b:  "+index);
		//Log.v("haha","b:  "+d.get(d.size()-1));

		while (index < d.get(d.size()-1)) {
			if (d.get(i)==index|d.get(i)==index+1|d.get(i)==index+2){
				index=d.get(i);	
			}else{
				d.add(i,index);
				dd.add(i,dd.get(i-1));
			}
			index = index+5;
			i++;
			//Log.v("count",": "+index);
		}

		int b=d.get(0);
		//Log.v("count","hhh"+d.size());
		int count =1;
		while(b<d.get(d.size()-2)){
			int lo = dd.get(count);
			if(lo==dd.get(count-1)){
				;
			}else{
				d.add(count,d.get(count-1));
				dd.add(count,lo);
				count++;
			}
			count++;
			b=b+5;

		}

		xMax = Collections.max(d);
		xMin = Collections.min(d);
		yMax = Collections.max(dd);
		yMin = Collections.min(dd);

		//Log.v("ha","ddddddddddasfdewr333d");
		try{
			FileOutputStream graph = openFileOutput("graph.txt", MODE_WORLD_READABLE);
			OutputStreamWriter graphwriter = new OutputStreamWriter(graph);



			int a =0;
			while(a<d.size()){
				series.add(d.get(a), dd.get(a));
				graphwriter.write(d.get(a)+"\n"+dd.get(a)+"\n");
				a++;
			}
			//Log.v("count","hhh");
			graphwriter.close();
		}catch(Exception e3)
		{
			//Log.v("graph","...");
		}


		dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
		return dataset;
	}

	private XYMultipleSeriesRenderer getDemoRenderer() {
		//Log.v("ha","dddd1ddd");
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(35);
		renderer.setChartTitleTextSize(35);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setMargins(new int[] { 40, 55, -90, 30 });
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(Color.GRAY);

		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillBelowLine(false);
		r.setFillPoints(true);
		renderer.addSeriesRenderer(r);
		setChartSettings(renderer);
		//Log.v("ha","dddddd");
		return renderer;
	}

	private void setChartSettings(XYMultipleSeriesRenderer renderer) {
		renderer.setChartTitle("Activity History");
		renderer.setXTitle("time");
		//renderer.setYTitle("activity");
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.WHITE);
		//renderer.setRange(new double[] {0,6,-70,40});
		renderer.setFitLegend(false);
		renderer.setAxesColor(Color.BLACK);
		renderer.setShowGrid(false);

		String[] date={"resting","walking","running","cycling","upstairs"};  
		for (int i=yMin;i<=yMax;i++)
		{
			//Log.v("countt","hhh"+yMin);
			renderer.addYTextLabel(i,date[i] );
		}

		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setYLabels(0);



		int hour = xMin/3600;
		int min= (xMin-hour*3600)/60;
		int sec = xMin-hour*3600-min*60;
		//Log.v("countt","hhh"+hour);
		//Log.v("countt","hhh"+min);
		//Log.v("countt","hhh"+sec);

		String h;
		String m;
		String s;




		if(xMax-xMin<=180){ 
			for (int i=xMin;i<=xMax;i=i+20){


				if (hour<10)
					h="0"+hour+":";
				else 
					h=hour+":";

				if (min<10)
					m="0"+min+":";
				else 
					m=min+":";

				if (sec<10)
					s="0"+sec;
				else 
					s=""+sec;

				renderer.addXTextLabel(i,h+m+s);



				sec=sec+20;

				if (sec>=60)
				{
					sec=sec-60;
					min=min+1;
				}
				if (min>=60)
				{
					min=min-60;
					hour=hour+1;
				}
				if (hour>=24)
				{hour=hour-24;

				}


			}}



		else if(180<xMax-xMin & xMax-xMin<=1000)
		{ for (int i=xMin;i<=xMax;i=i+60)
		{


			if (hour<10)
				h="0"+hour+":";
			else 
				h=hour+":";

			if (min<10)
				m="0"+min+":";
			else 
				m=min+":";

			if (sec<10)
				s="0"+sec;
			else 
				s=""+sec;

			renderer.addXTextLabel(i,h+m+s);

			min=min+1;
			if (min>=60)
			{
				min=min-60;
				hour=hour+1;
			}
			if (hour>=24)
			{hour=hour-24;

			}


		}}
		else if (1000<xMax-xMin & xMax-xMin<=7200)
		{ for (int i=xMin;i<=xMax;i=i+300)
		{
			//Log.v("countt","hhh"+xMin);

			if (hour<10)
				h="0"+hour+":";
			else 
				h=hour+":";

			if (min<10)
				m="0"+min+":";
			else 
				m=min+":";

			if (sec<10)
				s="0"+sec;
			else 
				s=""+sec;

			renderer.addXTextLabel(i,h+m+s);

			min=min+5;
			if (min>=60)
			{
				min=min-60;
				hour=hour+1;
			}
			if (hour>=24)
			{hour=hour-24;

			}


		}}

		else if (7200<xMax-xMin & xMax-xMin<28800)
		{ for (int i=xMin;i<=xMax;i=i+1800)
		{
			//Log.v("countt","hhh"+xMin);

			if (hour<10)
				h="0"+hour+":";
			else 
				h=hour+":";

			if (min<10)
				m="0"+min+":";
			else 
				m=min+":";

			if (sec<10)
				s="0"+sec;
			else 
				s=""+sec;

			renderer.addXTextLabel(i,h+m+s);
			min=min+30;
			if (min>=60)
			{
				min=min-60;
				hour=hour+1;
			}
			if (hour>=24)
			{hour=hour-24;

			}


		}}


		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setXLabels(0);






		renderer.setXAxisMin(xMin-2);
		renderer.setXAxisMax(xMax+1);
		renderer.setYAxisMin(yMin-1);
		renderer.setZoomEnabled(false,false);
		renderer.setPanEnabled(false,false);
		renderer.setYAxisMax(yMax+1);

	}





}
