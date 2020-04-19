package com.example.flashbeepshake

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.util.*

class MainActivity : AppCompatActivity() {
    private var myUUID: UUID? = null
    private val mPREFIX = "Gan"
    private var mBluetoothAdapter: BluetoothAdapter? = null //holds the Bluetooth Adapter
    private var server: AcceptThread? = null                //server object
    private var client:ConnectThread? = null
    private var sendButton: Button? = null
    private var messageTextView: TextView? = null
    private var clientHasPaired = false                     //client will only pair once
    private var flashOnOff:Boolean = false

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i(LOG_TAG, "BroadcastReceiver onReceive()")
            handleBTDevice(intent)
        }
    }//when activated, scans for Bluetooth devices -- looks to see which ones it can detect

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        myUUID = UUID.fromString(MY_UUID_STRING)


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        flashBtn.setOnClickListener{
            client?.sendFlash()
            //flash()
        }
        beepBtn.setOnClickListener{
            client?.sendBeep()
            //beep()
        }
        shakeBtn.setOnClickListener {
            client?.sendShake()
            //shake()
        }
        scanBtn.setOnClickListener {
            getPairedDevices()
            setUpBroadcastReceiver()
        }
        connectBtn.setOnClickListener {
            connect()
        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun flash(){
        val cm:CameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val camera = cm.cameraIdList[0]

        if(flashOnOff) {
            cm.setTorchMode(camera, false)
            flashOnOff = false
        }else{
            cm.setTorchMode(camera, true)
            flashOnOff = true
        }
    }

    fun beep(){
        val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        tone.startTone(ToneGenerator.TONE_DTMF_3,600) //play specific tone for 600ms
    }

    fun shake() {
        val vibrator:Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(1000)
    }

    fun connect() {
        getPairedDevices()
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, N_SECONDS)
        startActivity(discoverableIntent)
        //create server thread
        server = AcceptThread()
        if (server != null) {   //start server thread
            Log.i(TSERVER, "Connect Button spawning server thread")
            server!!.start()     //calls AcceptThread's run() method
        } else {
            Log.i(TSERVER, "setupButtons(): server is null")
        }
    }



    public override fun onResume() {
        super.onResume()
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        Log.i(LOG_TAG, "onResume()")
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Log.i(LOG_TAG, "No Bluetooth on this device")
            Toast.makeText(baseContext,
                "No Bluetooth on this device", Toast.LENGTH_LONG).show()
        } else if (!mBluetoothAdapter!!.isEnabled) {
            Log.i(LOG_TAG, "enabling Bluetooth")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        Log.i(LOG_TAG, "End of onResume()")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i(LOG_TAG, "onActivityResult(): requestCode = $requestCode")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i(LOG_TAG, "  --    Bluetooth is enabled")
                getPairedDevices() //find already known paired devices
                setUpBroadcastReceiver()
            }
        }
    }

    private fun getPairedDevices() {//find already known paired devices
        val pairedDevices = mBluetoothAdapter!!.bondedDevices
        Log.i(LOG_TAG, "--------------------------\ngetPairedDevices() - Known Paired Devices")
        // If there are paired devices
        if (pairedDevices.size > 0) {
            for (device in pairedDevices) {
                Log.i(LOG_TAG, device.name + "\n" + device)
                if (!clientHasPaired && tryToPair(device, device.name)) {
                    clientHasPaired = true
                    break
                }
            }
        }
        Log.i(LOG_TAG, "getPairedDevices() - End of Known Paired Devices\n-------------------------")
    }

    private fun setUpBroadcastReceiver() {
        // Create a BroadcastReceiver for ACTION_FOUND
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)    {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION)
            Log.i(TCLIENT,"Getting Permission")
            return
            //Discovery will be setup in onRequestPermissionResult() if permission is granted
        }
        setupDiscovery()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(LOG_TAG, "Fine_Location Permission granted")
                    setupDiscovery()
                } else {    //tracking won't happen since user denied permission
                    Log.i(LOG_TAG, "Fine_Location Permission refused")
                }
                return
            }
        }
    }

    private fun setupDiscovery() {
        Log.i(TCLIENT,"Activating Discovery")
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        mBluetoothAdapter!!.startDiscovery()
    }

    private fun handleBTDevice(intent: Intent) {
        Log.i(TCLIENT, "handleBRDevice() -- starting   <<<<--------------------")
        val action = intent.action
        // When discovery finds a device
        if (BluetoothDevice.ACTION_FOUND == action) {
            // Get the BluetoothDevice object from the Intent
            val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
            val deviceName =
                if (device.name != null) {
                    device.name.toString()
                } else {
                    //"--Unnamed device found - should ignore this device--"
                    return      //ignoring this device
                }
            Log.i(TCLIENT, deviceName + "\n" + device)
            if (!clientHasPaired) clientHasPaired =  tryToPair(device, deviceName)
        }
    }

    private fun tryToPair(device:BluetoothDevice, who:String) : Boolean {
        // The following is specific to this App for the client
        if (who.length > 3) { //for now, looking for MSU prefix
            val prefix = who.subSequence(0,3)
            if (prefix == mPREFIX) {//This is the server
                Log.i(LOG_TAG,"Canceling Discovery")
                mBluetoothAdapter!!.cancelDiscovery()
                Log.i(LOG_TAG,"Connecting")
                client = ConnectThread(device)  //FIX** remember and reconnect if interrupted?
                Log.i(LOG_TAG,"Running Connect Thread")
                client?.start()
                return true
            }
        }
        return false
    }

    ///////////////////////////////////// Client Thread to talk to Server here //////////////////////
    private inner class ConnectThread(mmDevice: BluetoothDevice) : Thread() { //from android developer
        private var mmSocket: BluetoothSocket? = null

        init {
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            Log.i(TCLIENT, "ConnectThread: init()")
            try {
                // myUUID is the app's UUID string, also used by the server code
                mmSocket = mmDevice.createRfcommSocketToServiceRecord(myUUID)
            } catch (e: IOException) {
                Log.i(TCLIENT, "IOException when creating RFcommSocket\n $e")
            }
        }

        override fun run() {
            // Cancel discovery because it will slow down the connection
            Log.i(TCLIENT, "ConnectThread: run()")
            Log.i(TCLIENT, "in ClientThread - Canceling Discovery")
            mBluetoothAdapter!!.cancelDiscovery()
            if (mmSocket == null) {
                Log.e(TCLIENT,"ConnectThread:run(): mmSocket is null")
                return
            }
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception after 12 seconds (or so)
                val who:String = mmSocket!!.remoteDevice.name
                Log.i(TCLIENT, "Connecting to server: $who")
                mmSocket!!.connect()
            } catch (connectException: IOException) {
                Log.i(TCLIENT, "Connect IOException when trying socket connection\n $connectException")
                // Unable to connect; close the socket and get out
                try {
                    Log.i(TCLIENT, "Closing the connection\n $connectException")
                } catch (closeException: IOException) {
                    Log.i(TCLIENT, "Close IOException when trying socket connection\n $closeException")
                }
                Log.i(TCLIENT, "Client Thread run() returning having failed")

                return
            }
            Log.i(TCLIENT, "Connection Established")
        }

        //manage the connection over the passed-in socket

        fun sendMessage(message:String) {
            val out: OutputStream
            val msg = message.toByteArray()
            try {
                Log.i(TCLIENT, "In sendMessage: sending the message: [$message]")
                out = mmSocket!!.outputStream
                out.write(msg)
            } catch (ioe: IOException) {
                Log.e(TCLIENT, "IOException when opening outputStream\n $ioe")
                return
            }
        }

        fun sendFlash(){
            try{
                Log.i(TCLIENT, "In sendFlash: sending the message: [flash]")
                mmSocket!!.outputStream.write(("flash").toByteArray())
            } catch (ioe: IOException) {
                Log.e(TCLIENT, "IOException when opening outputStream\n $ioe")
                return
            }
        }

        fun sendBeep(){
            try{
                Log.i(TCLIENT, "In sendFlash: sending the message: [beep]")
                mmSocket!!.outputStream.write(("beep").toByteArray())
            } catch (ioe: IOException) {
                Log.e(TCLIENT, "IOException when opening outputStream\n $ioe")
                return
            }
        }

        fun sendShake(){
            try{
                Log.i(TCLIENT, "In sendFlash: sending the message: [shake]")
                mmSocket!!.outputStream.write(("shake").toByteArray())
            } catch (ioe: IOException) {
                Log.e(TCLIENT, "IOException when opening outputStream\n $ioe")
                return
            }
        }
    }

    //////////////////////SERVER SOCKET STUFF HERE////////////////////////////
    private inner class AcceptThread : Thread() {  //from android developer
        private var mmServerSocket: BluetoothServerSocket? = null

        private var flashOnOff = false

        init {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is supposed to be final
            val tmp: BluetoothServerSocket
            try {
                // myUUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter!!.listenUsingRfcommWithServiceRecord(SERVICE_NAME, myUUID)
                Log.i(TSERVER, "AcceptThread registered the server\n")
                mmServerSocket = tmp
            } catch (e: IOException) {
                Log.e(TSERVER, "AcceptThread registering the server failed\n $e")
            }
        }

        override fun run() {
            var socket: BluetoothSocket?
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                Log.i(TSERVER, "AcceptTread.run(): Server Looking for a Connection")
                try {
                    socket = mmServerSocket!!.accept()  //blocks until connection made or exception
                    Log.i(TSERVER, "Server socket accepting a connection")
                } catch (e: IOException) {
                    Log.e(TSERVER, "socket accept threw an exception\n $e")
                    break
                }

                // If a connection was accepted
                if (socket != null) {
                    Log.i(TSERVER, "Server Thread run(): Connection accepted")
                    // Do work to manage the connection (in a separate thread)
                    manageConnectedSocket(socket)
                    break
                } else {
                    Log.i(TSERVER, "Server Thread run(): The socket is null")
                }
            }
        }

        //manage the Server's end of the conversation on the passed-in socket
        fun manageConnectedSocket(socket: BluetoothSocket) {
            Log.i(TSERVER, "\nManaging the Socket\n")
            val inSt: InputStream
            val nBytes: Int
            val msg = ByteArray(255) //arbitrary size
            try {
                inSt = socket.inputStream
                nBytes = inSt.read(msg)
                Log.i(TSERVER, "\nServer Received $nBytes \n")
            } catch (ioe: IOException) {
                Log.e(TSERVER, "IOException when opening inputStream\n $ioe")
                return
            }

            /// Put your stuff here

            try {
                val msgString = msg.toString(Charsets.UTF_8)    //String(msg, "UTF-8") //convert byte array to string
                Log.i(TSERVER, "\nServer Received  $nBytes, Bytes:  [$msgString]\n")
                //runOnUiThread { echoMsg("\nReceived $nBytes:  [$msgString]\n") }
                if(msgString.contains("flash")){
                    flash()
                }
                if(msgString.contains("beep")){
                    beep()
                }
                if(msgString.contains("shake")){
                    shake()
                }
            } catch (uee: UnsupportedEncodingException) {
                Log.e(TSERVER, "UnsupportedEncodingException when converting bytes to String\n $uee")
            }
        }
    }

    companion object {
        private const val ACCESS_FINE_LOCATION = 1
        private const val N_SECONDS = 255
        private const val TCLIENT = "--Talker Client--"  //for Log.X
        private const val TSERVER = "--Talker SERVER--"  //for Log.X
        private const val REQUEST_ENABLE_BT = 3313  //our own code used with Intents
        private const val MY_UUID_STRING = "12ce62cb-60a1-4edf-9e3a-ca889faccd6c" //from www.uuidgenerator.net
        private const val SERVICE_NAME = "Talker"
        private const val LOG_TAG = "--Talker----"
    }

}


