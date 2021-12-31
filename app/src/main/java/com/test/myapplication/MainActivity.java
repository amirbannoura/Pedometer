package com.test.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.app.ActivityManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



import android.app.ActivityManager.RunningServiceInfo;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Button;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;
import android.content.ComponentName;
import android.os.IBinder;
import android.content.ServiceConnection;
import android.content.Context;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Buttons for the GUI
    private Button startServiceButton;
    private Button stopServiceButton;
    private Button updateButton;
    private Button exitButton;
    private Button aboutButton;
    private String serverIP = "192.168.1.100"; //192.168.1.100
    private float xOrientation,yOrientation,zOrientation;
    private float X,Y,Z,G,xGiro,yGiro,zGiro,MX,MY,MZ;
    private File sdDirectory,file;
    private BufferedWriter bufferedWriter;

    private PedoInterface pedoServiceProxy; // used to call methods from
    // the service
    private Toast toast;   // to display messages
    Handler h=new Handler();
    Handler h2=new Handler();
    private Intent i; // intent associated with the service

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        i = new Intent(this, PedoService.class); // instantiate an intent object
        sdDirectory = Environment.getExternalStorageDirectory();

        		/*
		in case the service is already started
		(user clicked start then exited then opened the program )
		it should be bind to the activity, so that if the user clicks update
		the values should be updated (even if started, values can't be updated
		until it is binded)
		if not started then this is not necessary
		*/
        if(isServiceRunning("PedoService"))
        {
            bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
        }
 		/*
		 Automatically update fields
		 */

        h.post(new Runnable(){

            @Override
            public void run() {

                if(isServiceRunning("PedoService")){
                    try {



                        xOrientation = pedoServiceProxy.getXOrientation();
                        yOrientation = pedoServiceProxy.getYOrientation();
                        zOrientation = pedoServiceProxy.getZOrientation();
                        xGiro = pedoServiceProxy.getGiroX();
                        yGiro = pedoServiceProxy.getGiroY();
                        zGiro = pedoServiceProxy.getGiroZ();
                        X = pedoServiceProxy.getAccelX();
                        Y = pedoServiceProxy.getAccelY();
                        Z = pedoServiceProxy.getAccelZ();
                        G = pedoServiceProxy.getAccelometer();
                        MX = pedoServiceProxy.getMX();
                        MY = pedoServiceProxy.getMY();
                        MZ = pedoServiceProxy.getMZ();

					//WriteFile();

					TextView tview= (TextView) findViewById(R.id.xOrientation);
					tview.setText(Float.toString(xOrientation));

					tview= (TextView) findViewById(R.id.yOrientation);
					tview.setText(Float.toString(yOrientation));

					tview= (TextView) findViewById(R.id.zOrientation);
					tview.setText(Float.toString(zOrientation));

					tview= (TextView) findViewById(R.id.xGiro);
					tview.setText(Float.toString(xGiro));

					tview= (TextView) findViewById(R.id.yGiro);
					tview.setText(Float.toString(yGiro));

					tview= (TextView) findViewById(R.id.zGiro);
					tview.setText(Float.toString(zGiro));

					tview = (TextView) findViewById(R.id.AccelX);
					tview.setText(Float.toString(X));

					tview = (TextView) findViewById(R.id.AccelY);
					tview.setText(Float.toString(Y));

					tview = (TextView) findViewById(R.id.AccelZ);
					tview.setText(Float.toString(Z));


					tview = (TextView) findViewById(R.id.AccelG);
					tview.setText(Float.toString(G));

					tview = (TextView) findViewById(R.id.mx);
					tview.setText(Float.toString(MX));

					tview = (TextView) findViewById(R.id.my);
					tview.setText(Float.toString(MY));

					tview = (TextView) findViewById(R.id.mz);
					tview.setText(Float.toString(MZ));
/*
					DatagramSocket clientSocketUdp;
					clientSocketUdp = new DatagramSocket(6001);


					String x = Float.toString(xOrientation) + "; " + Float.toString(yOrientation) + "; " + Float.toString(zOrientation) +"; "
							+ Float.toString(xGiro) + "; " + Float.toString(yGiro) + "; " + Float.toString(zGiro) +"; "
							+ Float.toString(X) + "; " + Float.toString(Y) + "; " + Float.toString(Z)+ "; " + Float.toString(G) + "; "
							+ Float.toString(MX) + "; " + Float.toString(MY) + "; " + Float.toString(MZ);



					// send one datagram
					byte[] data =  new byte[1];//null;//x.getBytes();//new byte[1];

					// put the string in the data object
					data = x.getBytes();
					DatagramPacket packet = new DatagramPacket(data, data.length);

					// set receiver address and port
					packet.setAddress(InetAddress.getByName(serverIP));
					packet.setPort(6000);

					// send the packet and close connection
					clientSocketUdp.send(packet);
					clientSocketUdp.close();

*/
                    } catch (Exception e) {
                    }
                }
                h.postDelayed(this,12);

            }

        });

		/*
		 Automatically update fields
		 */

        h2.post(new Runnable(){

            @Override
            public void run() {

                if(isServiceRunning("PedoService")){
                    try {
                        toast = Toast.makeText(getApplicationContext(), "Running H2 Function", Toast.LENGTH_SHORT);
                        toast.show();

                    } catch (Exception e) {
                    }
                }
                h2.postDelayed(this,5000);

            }

        });

		/*
		 listener for the start button
		 bind and start the service
		 bind is used to associate the service with this activity
		 start is used to keep the service running after unbinding it with
		 this activity
		*/
        OnClickListener startServiceButtonListener = new OnClickListener() {
            public void onClick(View v) {
                if(!isServiceRunning("PedoService"))
                {
                    Date date = new Date() ;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;

                    file = new File(sdDirectory, dateFormat.format(date) + ".txt");

                    bufferedWriter = null;

                    try {
                        bufferedWriter = new BufferedWriter(new FileWriter(file
                                .getAbsolutePath(), true),500000);

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    bindService(i, serviceConnection, Context.BIND_AUTO_CREATE);
                    startService(i);
                }
                else
                {
                    toast = Toast.makeText(getApplicationContext(), "Already Started", Toast.LENGTH_SHORT);
                    toast.show();

                }

            }
        };

		/*
		 listener for the stop button
		 check whether the service is running or not
		 if running the stop it (stop and unbind) in order not to keep running after leaving the activity
		 if not running the display a message for the user
		 */

        OnClickListener stopServiceButtonListener = new OnClickListener() {
            public void onClick(View v) {
                try {
                    if(isServiceRunning("PedoService")){
                        stopService(i);
                        unbindService(serviceConnection);
                        bufferedWriter.close();
                    }
                    else {
                        toast = Toast.makeText(getApplicationContext(), "Already stopped", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (Exception e) {
                }
            }
        };


        /*
         * listener for the update button
         * checks whether the service running or not
         * if running then get the values from the service through the tracking service proxy
         * (here the service will be binded onCreate, nevertheless it will not update)
         * calculates the time period in the form MM:SS
         * and display the values
         * if its not running then show a message for the user
         */

        OnClickListener updateButtonListener = new OnClickListener() {
            public void onClick(View v) {


/*
				if(isServiceRunning("servicePedo")){
				try {


					TextView tview= (TextView) findViewById(R.id.Orientation);//14
					tview.setText(Float.toString(pedoServiceProxy.getDegree()));

					tview = (TextView) findViewById(R.id.AccelX);//14.5
					tview.setText(Float.toString(pedoServiceProxy.getAccelX()));

					tview = (TextView) findViewById(R.id.AccelY);
					tview.setText(Float.toString(pedoServiceProxy.getAccelY()));


					tview = (TextView) findViewById(R.id.AccelZ);
					tview.setText(Float.toString(pedoServiceProxy.getAccelZ()));


					tview = (TextView) findViewById(R.id.AccelG);
					tview.setText(Float.toString(pedoServiceProxy.getAccelometer()));

				} catch (Exception e) {
				}

				}
				else {
					toast = Toast.makeText(getApplicationContext(), "Not Running", Toast.LENGTH_SHORT);
					toast.show();
				}
	*/
            }
        };

        /* listener for the exit button
         * exit by calling the finish method, which calls the onDestroy method
         */

        OnClickListener exitButtonListener = new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        };

        // Buttons
        startServiceButton = (Button) findViewById(R.id.start_service_button);
        startServiceButton.setOnClickListener(startServiceButtonListener);
        stopServiceButton = (Button) findViewById(R.id.stop_service_button);
        stopServiceButton.setOnClickListener(stopServiceButtonListener);
        //	updateButton = (Button) findViewById(R.id.update_values_button);
        //	updateButton.setOnClickListener(updateButtonListener);
        exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(exitButtonListener);

    }

    /* onDestroy method
     * exits the program
     * will unbind the service in case the start button was pressed
     */
    public void onDestroy() {
        super.onDestroy();

        try {
            unbindService(serviceConnection);
        } catch (Exception e) {
        }

    }


    //check whether a service is running or not!
    public boolean isServiceRunning(String serviceName) {
        boolean running = false;
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        am.getRunningServices(50);

        List<RunningServiceInfo> list = null;
        try {
            list = am.getRunningServices(100);
            for (ActivityManager.RunningServiceInfo info : list) {
                if (info.service.getClassName().contains(serviceName)) {

                    running = true;
                }
            }
        } catch (Exception e) {
        }

        return running;

    }


    // /service connection to connect to the service!
    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            pedoServiceProxy = PedoInterface.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className) {
            pedoServiceProxy = null;
        }
    };

    public void WriteFile()
    {
        try {
            bufferedWriter.write(Float.toString(xOrientation) + "; " + Float.toString(yOrientation) + "; " + Float.toString(zOrientation) +"; "
                    + Float.toString(xGiro) + "; " + Float.toString(yGiro) + "; " + Float.toString(zGiro) +"; "
                    + Float.toString(X) + "; " + Float.toString(Y) + "; " + Float.toString(Z)+ "; " + Float.toString(G) + "; "
                    + Float.toString(MX) + "; " + Float.toString(MY) + "; " + Float.toString(MZ));
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e1) {

            e1.printStackTrace();
        }

    }
}