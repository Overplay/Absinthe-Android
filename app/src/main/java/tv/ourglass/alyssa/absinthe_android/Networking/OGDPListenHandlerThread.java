package tv.ourglass.alyssa.absinthe_android.Networking;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import tv.ourglass.alyssa.absinthe_android.Models.OGConstants;
import tv.ourglass.alyssa.absinthe_android.Scenes.Control.OGDevice;


/**
 * Created by mkahn on 11/13/16.
 */


// This thread serves ONLY to listen for discovery enumeration reponse packets

public class OGDPListenHandlerThread extends HandlerThread {

    public static final String TAG = "OGDPListenHandlerThread";
    public Context mContext;

    private Handler mWorkerHandler;
    private DatagramSocket ogdpSocket;

    private OGDPListenerListener mListener;
    private int mTimeout;

    private HashMap<String, JSONObject> mFoundOGs = new HashMap<>();

    public interface OGDPListenerListener {
        void devicesFound(HashMap<String, JSONObject> devices);
        void processDevice(OGDevice device);
        void error(Exception e);
    }

    public OGDPListenHandlerThread(String name) {
        super(name);
    }


    public void start(Context context, DatagramSocket socket, OGDPListenerListener listener){

        super.start();
        ogdpSocket = socket;
        mContext = context;
        mListener = listener;
        mWorkerHandler = new Handler(getLooper());

    }

    public void listen(int timeout){

        mTimeout = timeout;
        Log.d(TAG, "Issuing ogdp listener thread.");
        mWorkerHandler.post(listenerRunnable);

    }

    private Runnable listenerRunnable = new Runnable() {
        @Override
        public void run() {

            mFoundOGs.clear();  // zero out from previous run

            try {

                while (true) {

                    DatagramPacket p = new DatagramPacket(new byte[1200], 1200);

                    // TODO: do we want to have a timeout?
                    ogdpSocket.setSoTimeout(mTimeout);

                    ogdpSocket.receive(p);

                    String s = new String(p.getData(), 0, p.getLength());
                    Log.d(TAG, "Got response from: " + p.getAddress().getHostAddress());

                    try {
                        // The next two lines will puke if: a) response is not JSON or, b) response
                        // does not have a name field in the JSON (probably not an OG).
                        JSONObject respJson = new JSONObject(s);

                        // This will throw a JSON exception if the name or randomFactoid fields are
                        // missing which means it definitely is not an OG response. This is kind of
                        // a crappy test and we should add a more definitive signature down the line.
                        String systemName = respJson.getString("name");
                        Log.d(TAG, systemName);
                        String randomFactoid = respJson.getString("randomFactoid");

                        OGDevice device = new OGDevice();
                        device.systemName = systemName;
                        device.ipAddress = p.getAddress().getHostAddress();
                        device.ttl = OGConstants.maxTTL;

                        try {
                            device.location = respJson.getString("locationWithinVenue");
                        } catch (JSONException e) {
                            device.location = "";
                        }

                        try {
                            device.venue = respJson.getString("venue");
                        } catch (JSONException e) {
                            device.venue = "";
                        }

                        //Log.d(TAG, "Found OG! \n" + device.description());
                        mListener.processDevice(device);

                    } catch (JSONException e) {
                        Log.e(TAG, "Received reponse, but not a valid OG response so ignoring");
                    }
                }

            } catch (SocketTimeoutException e) {
                Log.d(TAG, "Socket timed out waiting for more data, we're done searching");
                if (mListener!=null)
                    mListener.devicesFound(mFoundOGs);

            } catch (IOException e) {
                Log.e(TAG, "Error on packet receive!");
                if (mListener!=null)
                    mListener.error(e);
            }

        }

    };


}
