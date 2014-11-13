package edu.temple.mystockapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//Brandon Shain

@SuppressLint({ "DefaultLocale", "HandlerLeak" })
public class MainActivity extends Activity {

	TextView textView_name, textView_symbol, textView_price, textView_volume, textView_threadStatus;
	Button button_go;
	EditText editText_stockSymbol;
	JSONObject jObj;
	JSONObject subObj;
	String jsonString;
	String stockSymbol;
	Handler webHandle, threadHandle, stopHandle;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		button_go = (Button) findViewById(R.id.button_go);
		textView_name = (TextView) findViewById(R.id.name_textView);
		textView_symbol = (TextView) findViewById(R.id.symbol_textView);
		textView_price = (TextView) findViewById(R.id.price_textView);
		textView_volume = (TextView) findViewById(R.id.volume_textView);
		textView_threadStatus = (TextView) findViewById(R.id.threadStatus);
		editText_stockSymbol = (EditText) findViewById(R.id.stockSymbol_editText);
		//Thread WebThread;
		//urlString = getResources().getString(R.string.default_url);
		
		//Create a new thread that takes a runnable object, so you can 
		//start the thread whenever you choose
		final Thread webThread = new Thread(new Runnable(){
			
			public void run(){
				URL url;
				   try {
					   int seconds = 0;

					      while(!Thread.currentThread().isInterrupted()) {			   
									try 
									{	
										String urlString = "http://finance.yahoo.com/webservice/v1/symbols/" + stockSymbol + "/quote?format=json";
										url = new URL(urlString);
										//htmlString = "";
										jsonString = "";
										InputStreamReader streamReader = new InputStreamReader(url.openStream()); 
										BufferedReader reader = new BufferedReader(streamReader);
										for(String line = reader.readLine(); line != null; line = reader.readLine())
										{
											//concatenates the retrieved JSON code from the buffered reader
											jsonString = jsonString + line;
											
										}
					
										jObj = new JSONObject(jsonString);
										
										//create a new message object and retrieves the message from the handler
										Message msg = webHandle.obtainMessage();
										msg.obj = jObj;
										webHandle.sendMessage(msg);
										
										msg = threadHandle.obtainMessage();
										msg.obj = Integer.toString(seconds);
										threadHandle.sendMessage(msg);
										
										Thread.sleep(5000);
										seconds = seconds + 5;
									} 
									catch (MalformedURLException e) 
									{
										e.printStackTrace();
									}
									catch (IOException e) 
									{
										e.printStackTrace();
									}		
									catch (JSONException e) {
										e.printStackTrace();
									}		      
					      }					   
				   }
				   catch (InterruptedException consumed){
		      /* Allow thread to exit */
		   }
				   
			}		   
		});//end webThread
		
		webHandle = new Handler(){
			@Override
			public void handleMessage(Message msg) {		 
				//display.setLayerType(View.LAYER_TYPE_SOFTWARE, null);					
				//to enable javascript
				//display.getSettings().setJavaScriptEnabled(true);
					
				String name = "", volume = "", price = "", symbol = "";
				//to display stock info
				try {
					jObj = (JSONObject) msg.obj;
					subObj =  jObj.getJSONObject("list");
					jObj = subObj.getJSONArray("resources").getJSONObject(0).getJSONObject("resource").getJSONObject("fields");
					name = jObj.getString("name");
					volume = jObj.getString("volume");
					price = jObj.getString("price");
					symbol = jObj.getString("symbol");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				textView_name.setText((getResources().getString(R.string.name)) + " " + name);
				textView_symbol.setText((getResources().getString(R.string.symbol)) + " " + symbol);
				textView_price.setText((getResources().getString(R.string.price)) + " " + price);
				textView_volume.setText((getResources().getString(R.string.volume)) + " " + volume);
				//textView_threadStatus.setText("Seconds Ellapsed Since Last Update:" + seconds);
				
		    }				
		};//End Handler
		
		threadHandle = new Handler(){
			@Override
			public void handleMessage(Message msg) {		 
					
				String seconds = (String) msg.obj;
				textView_threadStatus.setText("Seconds Ellapsed Since Last Update:" + seconds);
				
		    }				
		};//End Handler
		

		
		
		View.OnClickListener listener = new View.OnClickListener() {
			
						
			@Override
			public void onClick(View v) {
				
				if(!webThread.isAlive())
				{webThread.start();}
				
				stockSymbol = editText_stockSymbol.getText().toString().toUpperCase();

				//if(webThread.isAlive())
				//{webThread.}
				//start the thread
				

				//Use this to figure out if a thread is still running
				//webThread.isAlive()
			}//end OnClick
		};//end listener
		
		
		button_go.setOnClickListener(listener);
		
	}
}
