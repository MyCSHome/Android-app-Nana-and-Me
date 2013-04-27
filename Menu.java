package com.mybo.nanaAndme;



import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;


public class Menu extends Activity{

	private ImageButton toTimePickerButton;
	private ImageButton toTrainButton;
	private ImageButton nanaReport ;
	private String savedInfo = "savedInfo.txt";
	private File fileDir ;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);

		//set up the filepaths for files
		fileDir = getFilesDir();
		CentralKnowledge.setFilepathARFF(fileDir.getAbsolutePath() + "/userARFF.arff");
		CentralKnowledge.setFilepathModel(fileDir.getAbsolutePath() + "/usermodel.model");


		toTimePickerButton = (ImageButton)findViewById(R.id.toTimePickerButton);
		toTimePickerButton.setOnClickListener(new TimePageListener());

		toTrainButton = (ImageButton)findViewById(R.id.toTrainButton);
		toTrainButton.setOnClickListener(new TrainPageListener());

		nanaReport = (ImageButton)findViewById(R.id.nanaReport);
		nanaReport.setOnClickListener(new LastPage());
	}

	//inner class
	private class TimePageListener implements OnClickListener{

		public void onClick(View v) {
			File modelFile = new File(CentralKnowledge.getFilepathModel());
			File arffFile = new File(CentralKnowledge.getFilepathARFF());

			if (modelFile.exists() && (arffFile.exists())){
				AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
				builder.setMessage("Would you like to use your customized mode?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						CentralKnowledge.willUseDefaultModel(false);
						Intent gotoTime = new Intent(Menu.this, NanaAndMe.class);
						startActivity(gotoTime);
						Menu.this.onDestroy();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						CentralKnowledge.willUseDefaultModel(true);
						Intent gotoTime = new Intent(Menu.this, NanaAndMe.class);
						startActivity(gotoTime);
						Menu.this.onDestroy();
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();



			}else{
				Intent gotoTime = new Intent(Menu.this, NanaAndMe.class);
				startActivity(gotoTime);
				Menu.this.onDestroy();
			}



		}


	}

	//inner class
	private class TrainPageListener implements OnClickListener{

		public void onClick(View v) {
			Intent gotoTrain = new Intent(Menu.this, TrainModelActivity.class);
			startActivity(gotoTrain);
			Menu.this.onDestroy();
		}

	}

	//inner class
	private class LastPage implements OnClickListener{

		public void onClick(View v) {
			
			File fileSavedInfo = new File( fileDir.getAbsolutePath() + "/"+ savedInfo );
			
			if( fileSavedInfo.exists() ){
				Intent reportMETs = new Intent(Menu.this, ReportMETsActivity.class);
				startActivity(reportMETs);
				Menu.this.onDestroy();
			}else{
				Toast.makeText(Menu.this, "Ooops! Nana cannot find your report.", Toast.LENGTH_SHORT).show();
			}
			
		}

	}

}
