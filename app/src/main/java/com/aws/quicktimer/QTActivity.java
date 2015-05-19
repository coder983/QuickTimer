package com.aws.quicktimer;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.sax.StartElementListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class QTActivity extends Activity{
	
	private static final String TIMING_SUSPENDED = "Timing Suspended";
	private static final String THREE_QUARTER_HOUR = ".75";
	private static final String HALF_HOUR = ".50";
	private static final String QUARTER_HOUR = ".25";
	private static final String MKEY = "mkey";
	private static final String HKEY = "hkey";
	private static final String HOURS = " hours";
	private static final String PRESS_START_TO_BEGIN_TIMER = "Press Start to Begin Timer";
	private static final String MINUTES = " minutes";
	private static final String TIMING_IN_PROGRESS = "Timing in Progress";
	private static final String STOP = "Stop";
	private static final String START = "Start";
	//Instance State Keys
	private static final String TOTAL_MINUTES_KEY = "tmin";
	private static final String START_MINUTES_KEY = "smin";
	private static final String QLABEL_KEY = "ql";
	private static final String TLABEL_KEY = "tl";
	private static final String CT_BUTTON_ENABLED_KEY = "cten";
    private static final String START_STOP = "ssb";

    long totalMinutes = 0;
	long start = 0;
	long end = 0; 
	
	Button startStopButton, currentTimeButton;
	
	TextView qLabel, tLabel;//tLabel is the Time Display
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qt);
        setStartStopButton((Button)findViewById(R.id.startstopbutton));
        setqLabel((TextView)findViewById(R.id.result));
        settLabel((TextView)findViewById(R.id.displayTime));
        setCurrentTimeButton((Button)findViewById(R.id.currentTime));
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.qt, menu);
        return true;
    }
    
    
    
    public void startStopTimer(View view) {
    	//Check StartStop Button Label
    	Button ssButton = (Button)view;
    	String ssLabel = (String) ssButton.getText();
    	
    	if (ssLabel.equals(START)) {
			ssButton.setText(STOP);
			setStart(System.currentTimeMillis());
			getqLabel().setText(TIMING_IN_PROGRESS);
			getCurrentTimeButton().setEnabled(true);
			gettLabel().setText("");
		} else {
			ssButton.setText(START);
			setEnd(System.currentTimeMillis());
			long minutes = ((getEnd() - getStart())/60000);
			setTotalMinutes(getTotalMinutes() + minutes);
		    Map<String, String> result = processTime(getTotalMinutes());
			gettLabel().setText(result.get(HKEY) + HOURS + " " + result.get(MKEY) + MINUTES);
			getqLabel().setText(TIMING_SUSPENDED);
			getCurrentTimeButton().setEnabled(false);
			setStart(0);			
		}		
	}
    
    private Map<String, String> processTime(long totalMins) {
		HashMap<String, String> res = new HashMap<String, String>();
		
		int totHours = (int)totalMins/60;
		int remainMins = (int)totalMins % 60;
		int fractionalHrs = remainMins/15;
		int extraMins = remainMins % 15;
		String sHours = processHours(totHours, fractionalHrs);
		String sMinutes = String.valueOf(extraMins);
		res.put(MKEY, sMinutes);
		res.put(HKEY, sHours);
		return res;
	}


	private String processHours(int totHours, int fractionalHrs) {
		
		String hoursString = "";
		
		hoursString = String.valueOf(totHours);
		
		if(fractionalHrs == 1){
			hoursString = hoursString + QUARTER_HOUR;
		} else if(fractionalHrs == 2){
			hoursString = hoursString + HALF_HOUR;
		} else if(fractionalHrs == 3){
			hoursString = hoursString + THREE_QUARTER_HOUR;
		}
		
		return hoursString;
		
	}


	public void resetTimer(View view){
    	//Button resTimer = (Button)view;
    	//Set Total Minutes to zero
    	setTotalMinutes(0);
    	//Set Textview to "Press Start to Begin Timer"
    	getqLabel().setText(PRESS_START_TO_BEGIN_TIMER);
    	//Clear Time Display
    	gettLabel().setText("");
    	//Set StartStop Button Label to "Start"
    	startStopButton.setText(START);
    	currentTimeButton.setEnabled(false);
    	
    }
	
	public void displayCurrentTime(View view) {
		
		setEnd(System.currentTimeMillis());
		long minutes = ((getEnd() - getStart())/60000) + getTotalMinutes();
		
		Map<String, String> result = processTime(minutes); 
				
		CharSequence text = result.get(HKEY) + HOURS + " " + result.get(MKEY) + MINUTES;
		int duration = Toast.LENGTH_SHORT;
		
		Toast toast = Toast.makeText(this, text, duration);
		toast.setGravity(Gravity.BOTTOM , 0, 0);
		toast.show();
		
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		//Save Quick Timer State
		getqLabel().setText((CharSequence) savedInstanceState.get(QLABEL_KEY));
		gettLabel().setText((CharSequence) savedInstanceState.get(TLABEL_KEY));
		setTotalMinutes(savedInstanceState.getLong(TOTAL_MINUTES_KEY));
		setStart(savedInstanceState.getLong(START_MINUTES_KEY));
		getCurrentTimeButton().setEnabled((Boolean) savedInstanceState.get(CT_BUTTON_ENABLED_KEY));
        getStartStopButton().setText((String)savedInstanceState.get(START_STOP));
		
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//Restore Quick Timer State
		outState.putString(QLABEL_KEY, (String) getqLabel().getText());
		outState.putLong(START_MINUTES_KEY, getStart());
		outState.putLong(TOTAL_MINUTES_KEY, getTotalMinutes());
		outState.putBoolean(CT_BUTTON_ENABLED_KEY, getCurrentTimeButton().isEnabled());
		outState.putString(TLABEL_KEY, (String) gettLabel().getText());
        outState.putString(START_STOP, (String) getStartStopButton().getText());
		
		super.onSaveInstanceState(outState);
	}

    
    /***************************************************************************************/
    /********************************* Getters and Setters *********************************/
    /***************************************************************************************/

	public long getTotalMinutes() {
		return totalMinutes;
	}


	public long getStart() {
		return start;
	}


	public long getEnd() {
		return end;
	}


	public void setStart(long start) {
		this.start = start;
	}


	public void setEnd(long end) {
		this.end = end;
	}


	public TextView getqLabel() {
		return qLabel;
	}


	public void setqLabel(TextView qLabel) {
		this.qLabel = qLabel;
	}


	public void setTotalMinutes(long totalMinutes) {
		this.totalMinutes = totalMinutes;
	}


	public Button getStartStopButton() {
		return startStopButton;
	}


	public void setStartStopButton(Button startStopButton) {
		this.startStopButton = startStopButton;
	}
	
    public TextView gettLabel() {
		return tLabel;
	}


	public void settLabel(TextView tLabel) {
		this.tLabel = tLabel;
	}

	public Button getCurrentTimeButton() {
		return currentTimeButton;
	}

	public void setCurrentTimeButton(Button currentTimeButton) {
		this.currentTimeButton = currentTimeButton;
	}
    
}
