package idv.android.hellouart;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private TextView view;
	private static ScrollView scrollView;
	private boolean Uart_Check;
	final Timer timer = new Timer();
	
	String Msg, ReStr;
	EditText EditMsg;
	private Button myButton1, myButton2, myButton3, myButton4, myButton5;
	
	public int fd, Uart_Port = -1, Baud_rate = -1;
	
	private static final String[] Uart_PortStr = {
		"ttymxc0", "ttymxc2", "ttymxc3", "ttymxc4"
	};
	
	private static final String[] Baud_rateStr = {
		"B9600", "B115200" , "B19200"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	
		
		scrollView = (ScrollView) this.findViewById(R.id.uart_scrollview);
		view = (TextView) findViewById(R.id.uart_view);
		EditMsg = (EditText) findViewById(R.id.uart_edit);
		EditMsg.setWidth(200);
		
		TimerTask task = new TimerTask(){
			public void run(){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
					
						if (Uart_Check) {
							ReStr = Uart2C.receiveMsgUart(fd);
							if ( ReStr != null) {
								Log.i("william","receive msg = " + ReStr);
								view.append(ReStr);
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);
								ReStr = null;
							}
						}
					}
					
				});
			}
		};
		
		timer.schedule(task, 1000, 100);
		
		Spinner spinner1 = (Spinner) findViewById(R.id.uart_select);
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Uart_PortStr);
		adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner1.setAdapter(adapter1);
		spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
				Uart_Port = position;
				adapterView.setVisibility(View.VISIBLE);
				Toast.makeText(MainActivity.this, "chose your port" + adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(MainActivity.this, "you don't have chose any port", Toast.LENGTH_LONG).show();
			}
		});
		
		Spinner spinner2 = (Spinner) findViewById(R.id.uart_mode);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, Baud_rateStr);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
				Baud_rate = position;
				adapterView.setVisibility(View.VISIBLE);
				Toast.makeText(MainActivity.this, "chose your Baud rate" + adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
			}
			
			public void onNothingSelected(AdapterView<?> arg0) {
				Toast.makeText(MainActivity.this, "you don't have chose any Bauld rate", Toast.LENGTH_LONG).show();
			}
		});
		
		myButton1 = (Button)findViewById(R.id.button1);
		myButton1.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if (Uart_Check == false && Uart_Port == 0) {
					fd = Uart2C.openUart(Uart_PortStr[0]); //ttymxc0
				}else if (Uart_Check == false && Uart_Port ==1) {
					fd = Uart2C.openUart(Uart_PortStr[1]); //ttymxc2
				}else if (Uart_Check == false && Uart_Port ==2) {
					fd = Uart2C.openUart(Uart_PortStr[2]); //ttymxc3
				}else if (Uart_Check == false && Uart_Port ==3) {
					fd = Uart2C.openUart(Uart_PortStr[3]); //ttymxc4
				}else if (Uart_Check == true) {
					Toast.makeText(MainActivity.this, "port is opend", Toast.LENGTH_LONG).show();
				}
				
				if(fd > 0 && Uart_Check == false) {
					setTitle(Uart_PortStr[Uart_Port] + "," + Baud_rateStr[Baud_rate]
							+"--"+String.valueOf(fd));
					Uart2C.setUart(Baud_rate);
					Uart_Check = true;
					Toast.makeText(
							MainActivity.this,
							"Open"+Uart_PortStr[Uart_Port]+"port\nBaud rate:"
							+Baud_rateStr[Baud_rate], Toast.LENGTH_LONG).show();	
				}else if (fd <= 0) {
					setTitle("open device false! --" + fd);
					Uart_Check = false;
					Toast.makeText(MainActivity.this, "open fail", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		myButton2 = (Button)findViewById(R.id.button2);
		myButton2.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				view.setText(null);
			}
		});
		
		myButton3 = (Button)findViewById(R.id.button3);
		myButton3.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				Uart_Check = false;
				Uart2C.closeUart(fd);
				setTitle(R.string.app_name);
				Toast.makeText(MainActivity.this, "port is closed", Toast.LENGTH_LONG).show();
			}
		});
		
		myButton4 = (Button)findViewById(R.id.button4);
		myButton4.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				timer.cancel();
				Uart2C.closeUart(fd);
				finish();
			}
		});
		
		myButton5 = (Button)findViewById(R.id.button5);
		myButton5.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if (Uart_Check == true){
					Msg = EditMsg.getText().toString();
					//Uart2C.sendMsgUart(Msg+ "\n");
					view.append(Msg + "\n");
					scrollView.fullScroll(ScrollView.FOCUS_DOWN);
				} else {
					Toast.makeText(MainActivity.this, "port is not open", Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}
}
