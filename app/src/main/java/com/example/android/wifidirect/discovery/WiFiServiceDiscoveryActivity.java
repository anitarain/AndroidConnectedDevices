
package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.wifidirect.discovery.WiFiChatFragment.MessageTarget;
import com.example.android.wifidirect.discovery.WiFiDirectServicesList.DeviceActionListener;
import com.example.android.wifidirect.discovery.WiFiDirectServicesList.DeviceClickListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * The main activity for the sample. This activity registers a local service and
 * perform discovery over Wi-Fi p2p network. It also hosts a couple of fragments
 * to manage chat operations. When the app is launched, the device publishes a
 * chat service and also tries to discover services published by other peers. On
 * selecting a peer published service, the app initiates a Wi-Fi P2P (Direct)
 * connection with the peer. On successful connection with a peer advertising
 * the same service, the app opens up sockets to initiate a chat.
 * {@code WiFiChatFragment} is then added to the the main activity which manages
 * the interface and messaging needs for a chat session.
 */
public class WiFiServiceDiscoveryActivity extends AppCompatActivity implements
        DeviceClickListener, Handler.Callback, MessageTarget, PeerListListener,
        ConnectionInfoListener,DeviceActionListener {

    public static final String TAG = "wifidirectdemo";

    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_wifidemotest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    public static final int MESSAGE_READ = 0x400 + 1;
    public static final int MY_HANDLE = 0x400 + 2;
    public static final int OBJECT_READ = 0x400 + 3;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private static final int SELECT_PICTURE = 19 ;
    private WifiP2pManager manager;
   // private List<String> permittedMac;
    public Button sendImage;
    private Activity activity;
    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    public WifiP2pDevice clientDevice;


    static final int SERVER_PORT = 4545;


    private String role;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;

    private Handler handler = new Handler(this);
    private WiFiChatFragment chatFragment;
    private WiFiDirectServicesList servicesList;
    private ChatManager chatManager;
    private List<ChatManager> chatManagerList;
    private MessageType receivedMessageType;
    private String receivedString;
    private String whatIsMessage;
    private MessageType messageType;
    private TextView statusTxtView;
    private ImageView imageView;
    private Button userRole;
    private Button rotate;
    private String imgDecodableString;
    public Context context;
    public static String FolderName = "MiddlewareDemo";
    public String filePath;

    public List<WiFiP2pService> serviceList;
    public List<String> clientsList;

   // public WiFiDevicesAdapter adapter;



    private boolean isWifiP2pEnabled = false;
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
        appendStatus("Wifi Enabled = " + isWifiP2pEnabled);
    }

    private WifiP2pConfig config;

    private boolean isConnected;
    public void setConnected(boolean cn){
        isConnected = cn;
        appendStatus("Connected = " + cn);
    }


    private boolean hasBitmap;
    public void isBitmap(boolean bi){
        hasBitmap = bi;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        statusTxtView = (TextView) findViewById(R.id.status_text);

        //permittedMac = new ArrayList<String>();
        //permittedMac.add("9c:d9:17:6c:e8:84");
        //permittedMac.add("cc:c3:ea:e5:77:45");
        context = getApplicationContext();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter
                .addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        config = new WifiP2pConfig();
        config.wps.setup = WpsInfo.PBC;


        serviceList = new ArrayList<WiFiP2pService>();

       servicesList = new WiFiDirectServicesList();

        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        getFragmentManager().beginTransaction().add(R.id.container_root, servicesList, "services").commit();
        registerReceiver(receiver, intentFilter);

        FragmentTransaction mFragmentTransaction = getFragmentManager()
                .beginTransaction().add(R.id.container_root, servicesList, "services");

        mFragmentTransaction.addToBackStack(null);
        startRegistrationAndDiscovery();


        sendImage = (Button)findViewById(R.id.imageSend);
        rotate = (Button)findViewById(R.id.rotate);
        imageView = (ImageView) findViewById(R.id.imageView);



        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
//        mDetector = new GestureDetectorCompat(sendImage.getContext(),this);
//        sendImage.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//           public boolean onTouch(View v, MotionEvent event) {
//////                textView.setText("Touch coordinates : " +
//////                        String.valueOf(event.getX()) + "x" + String.valueOf(event.getY()));
//                switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                       // startActivity(new Intent(A.this, B.class));
//
//                        break;
//                    case MotionEvent.ACTION_UP:
//
//                        // Obtain MotionEvent object
//                        long downTime = SystemClock.uptimeMillis();
//                        long eventTime = SystemClock.uptimeMillis() + 100;
//                        float x = 0.0f;
//                        float y = 0.0f;
//                        // List of meta states found here:               developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
//                        int metaState = 0;
//                        MotionEvent motionEvent = MotionEvent.obtain(
//                                downTime,
//                                eventTime,
//                                MotionEvent.ACTION_UP,
//                                x,
//                                y,
//                                metaState
//                        );
//
//                        // Dispatch touch event to activity (make B static or get the activity var some other way)
//
//                        //B.OnTouchEvent(motionEvent);
//                       // ArrayList<String> motionEventList =
//                        //MessageType messageType = new MessageType(motionEvent.ACTION_UP, "sendImage");
//                        distributeContent(messageType);
//                        break;
//                }
//                // false doesn't work either
//                return true;
//            }
////
////                MessageType messageType = new MessageType(sendImage.dispatchTouchEvent(motionEvent), "sendImage");
////                distributeContent(messageType);
////                Log.d(DEBUG_TAG, "Touch coordinates :  " + sendImage.dispatchTouchEvent(motionEvent) + "........" +event.toString());
////                return true;
////            }
//        });



        rotate.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
//                        if (chatManager != null) {
                        if (! chatManagerList.isEmpty()) {

                          //  rotateImage(filePath);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://www.yahoo.com"));
                            MessageType messageType = new MessageType();
                            messageType.setString("intent");
                            messageType.setIntent(intent.toUri(0));
                            distributeContent(messageType);
                            


                        }
                    }
                });







        // mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
       // mDetector.setOnDoubleTapListener(this);

        userRole = (Button)findViewById(R.id.userRole);

        sendImage.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
//                        if (chatManager != null) {
                        if (! chatManagerList.isEmpty()) {


//                            MessageType messageType = new MessageType("55");
//                            chatManager.writeObject(messageType);
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);


                        }
                    }
                });


        userRole.setOnClickListener(
                new View.OnClickListener() {


                    @Override
                    public void onClick(View arg0) {

                    LayoutInflater layoutInflater
                            = (LayoutInflater)getBaseContext()
                            .getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = layoutInflater.inflate(R.layout.user_profile, null);
                    final PopupWindow popupWindow = new PopupWindow(
                            popupView,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);

                        Button btnDismiss = (Button)popupView.findViewById(R.id.buttonDismiss);
                            btnDismiss.setOnClickListener(new Button.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();
                        }});

                        RadioButton slaveRole = (RadioButton)popupView.findViewById(R.id.master);
                        slaveRole.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                if (chatManagerList == null) {
                                    Toast.makeText(getApplicationContext(), "You are not connected to any device !", Toast.LENGTH_SHORT).show();
                                }else {
                                    MessageType messageType1 = new MessageType();
                                    messageType1.setString("Master");
                                    distributeContent(messageType1);
                                    popupWindow.dismiss();
                            }
                        }});

                    RadioButton masterRole = (RadioButton)popupView.findViewById(R.id.slave);
                        masterRole.setOnClickListener(new Button.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                if (chatManagerList == null) {
                                    Toast.makeText(getApplicationContext(), "You are not connected to any device !", Toast.LENGTH_SHORT).show();
                                } else {
                                    MessageType messageType1 = new MessageType();
                                    messageType1.setString("Slave");
                                    distributeContent(messageType1);
                                    popupWindow.dismiss();
                                }
                            }
                        });

                    //popupWindow.showAsDropDown(userRole, 50, -30);
                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                    }});


    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        if(resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

                	        /*
    	         * get actual file name and size of file, it will be send to socket and recieved at other device.
    	         * File size help in displaying progress dialog actual progress.
    	         */
            String selectedfilePath = null;
            try {
                selectedfilePath = getPath(uri, getApplicationContext());




                //Log.e("Original Selected File Path-> ", selectedfilePath);
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }
            String Extension = "";
            final Bitmap bitmap;
            if(selectedfilePath!=null) {
                File f = new File(selectedfilePath);
                System.out.println("file name is   ::" + f.getName());
                Long FileLength = f.length();
                // ActualFilelength = FileLength;
                try {
                    Extension = f.getName();
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    // Log.d(TAG, String.valueOf(bitmap));
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] byteArray = stream.toByteArray();
                   //MessageType messageTypeImage = new MessageType(bitmap,Extension);
                    //BitmapSerialized bitmapSerialized = new BitmapSerialized(bitmap);
                  // chatManager.writeObject(messageType);


                    imageView.setImageBitmap(bitmap);

                    MessageType messageTypeImage = new MessageType();
                    messageTypeImage.setBitmap(bitmap);
                    messageTypeImage.setString(Extension);
                    distributeContent(messageTypeImage);

                    saveImage(bitmap, Extension);


                    Log.e("Name of File-> ", "" + Extension);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            }
        }
    }




                public void saveImage(final Bitmap bitmap1, String Extension) {

                    //final Bitmap bitmap = bitmap1;
                    final File f = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + FolderName + "/"
                                    + Extension);

                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    try {
                        f.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(f);

                        bitmap1.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                        filePath = f.getAbsolutePath();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


    public void rotateImage(String filePath) {


        final Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);

        Random generator = new Random();
        int n = 1000;
        n = generator.nextInt(n);
        Matrix mtx = new Matrix();
        mtx.postRotate(n);


        Bitmap rotatedImage = bitmap2.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), mtx, true);

        imageView.setImageBitmap(rotatedImage);

        Update update = new Update();
       // update.setContext(getApplicationContext());
        update.setInteger(n);
        update.setString("Update Received");

        MessageType messageType = new MessageType();
        messageType.setUpdate(update);
        distributeContent(messageType);

        Toast.makeText(getApplicationContext(), "degree= " + n + " Update " + update, Toast.LENGTH_LONG).show();
    }






    public static String getPath(Uri uri, Context context) {
        if (uri == null) {

            return null;
        }
        // this will only work for images selected from gallery
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        //CommonMethods.e("", "get path method->> after cursor init");
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);

        }
        cursor.close();
        return uri.getPath();
    }


    public void distributeContent(MessageType messageType){
        if (! chatManagerList.isEmpty()) {
            Log.d("chatManagerList", "chatManagerList" + chatManagerList);
            for(ChatManager m: chatManagerList) {
                m.writeObject(messageType);

            }
        }

    }




    @Override
    protected void onRestart() {
//        Fragment frag = getFragmentManager().findFragmentByTag("services");
//        if (frag != null) {
//            getFragmentManager().beginTransaction().remove(frag).commit();
//        }
        super.onRestart();
    }

    @Override
    protected void onStop() {
//        if (manager != null && channel != null) {
//            manager.removeGroup(channel, new ActionListener() {
//
//                @Override
//                public void onFailure(int reasonCode) {
//                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
//                }
//
//                @Override
//                public void onSuccess() {
//                }
//
//            });
//        }
        super.onStop();
    }

    /**
     * Registers a local service and then initiates a service discovery
     */
    private void startRegistrationAndDiscovery() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        manager.addLocalService(channel, service, new ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                appendStatus("Failed to add a service");
            }
        });

       discoverService();

    }

    private void discoverService() {

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        manager.setDnsSdResponseListeners(channel,
                new DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {

                        // A service has been discovered. Is this our app?
                        Log.d("onPeersAvailable", "DNS Service Started");

                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {

                            // update the UI and add the item the discovered
                            // device.

                            //  WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                            //          .findFragmentByTag("services");
                            //   if (fragment != null) {
                            //      WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment
                            //               .getListAdapter());

                            WiFiP2pService service = new WiFiP2pService();
                            service.device = srcDevice;
                            service.instanceName = instanceName;
                            service.serviceRegistrationType = registrationType;
                            //    adapter.add(service);
                            //        adapter.notifyDataSetChanged();
                            Log.d(TAG, "onBonjourServiceAvailable "
                                    + instanceName);
                            Log.d("onPeersAvailable", "DNS Service Founded these Service: " + service);
                        }


                    }
                    //  }


                }, new DnsSdTxtRecordListener() {

                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Log.d(TAG,
                                device.deviceName + " is "
                                        + record.get(TXTRECORD_PROP_AVAILABLE));
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        manager.addServiceRequest(channel, serviceRequest,
                new ActionListener() {

                    @Override
                    public void onSuccess() {
                        appendStatus("Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        appendStatus("Failed adding service discovery request");
                    }
                });
        manager.discoverServices(channel, new ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                appendStatus("Service discovery failed");

            }
        });
    }

    @Override
    public void connectP2p(final WiFiP2pService service) {

        config.deviceAddress = service.device.deviceAddress;
        clientDevice = service.device;

        if (serviceRequest != null)
            manager.removeServiceRequest(channel, serviceRequest,
                    new ActionListener() {

                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int arg0) {
                        }
                    });

       // if(permittedMac.contains(config.deviceAddress)) {
        manager.connect(channel, config, new ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Connecting to service");

            }

            @Override
            public void onFailure(int errorCode) {
                appendStatus("Failed connecting to service");
            }
        });
        }



    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
//            case MESSAGE_READ:
//                byte[] readBuf = (byte[]) msg.obj;
//
//               //  construct a string from the valid bytes in the buffer
//                String readMessage = new String(readBuf, 0, msg.arg1);
//                Log.d(TAG, readMessage);
//                Toast.makeText(getApplicationContext(), "Anita Sent object" , Toast.LENGTH_SHORT).show();
//                (chatFragment).pushMessage("Buddy: " + readMessage);


//                break;


            case OBJECT_READ:
                Log.d("ObjectRead", "Object Read");

                receivedMessageType = (MessageType) msg.obj;
                receivedString = receivedMessageType.getString();
               // whatIsMessage = receivedMessageType.getWhatIsMessage();




                //sendImage.onTouchEvent(receivedMessageType.event);
                   // Toast.makeText(getApplicationContext(), "Motion Event" + receivedMessageType.getMotionEvent(), Toast.LENGTH_LONG).show();




                if(receivedMessageType.getBitmap() != null) {
                   // imageView.setImageBitmap(receivedMessageType.getBitmap());
                    final File f = new File(
                            Environment.getExternalStorageDirectory() + "/"
                                    + FolderName + "/"
                                    + receivedMessageType.getString());

                    File dirs = new File(f.getParent());
                    if (!dirs.exists())
                        dirs.mkdirs();
                    try {
                        f.createNewFile();
                        FileOutputStream fOut = new FileOutputStream(f);

                        receivedMessageType.getBitmap().compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                        filePath= f.getAbsolutePath();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


//                    PackageManager pm = context.getPackageManager();
//                    Intent launchIntent = pm.getLaunchIntentForPackage(receivedMessageType.s3);
//                    context.startActivity(launchIntent);

//                    Intent intent = new Intent();
//                    intent.setAction(android.content.Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.parse("file://" + filePath), "image/*");
//                    startActivity(intent);

                    imageView.setImageBitmap(BitmapFactory.decodeFile(filePath));

                    Toast.makeText(getApplicationContext(), "Image " + receivedMessageType.getString(), Toast.LENGTH_SHORT).show();
                }

                if(receivedString != null) {
                    if (receivedString.equals("Master")) {
                        Toast.makeText(getApplicationContext(), "Wait for receiving Documents from Master ", Toast.LENGTH_SHORT).show();
                        sendImage.setVisibility(View.GONE);
                        userRole.setVisibility(View.GONE);

                    } else if (receivedString.equals("Slave")) {
                        sendImage.setVisibility(View.VISIBLE);
                        userRole.setVisibility(View.VISIBLE);
                    } else if (receivedString.equals("intent")){
                            try {
                                Intent intent = Intent.parseUri(receivedMessageType.getIntent(),0);
                                startActivity(intent);
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                    }
                }



//                if(receivedMessageType.getWhatMessage() != null) {
//                    if (receivedMessageType.getWhatMessage().equals("update")) {

                if (receivedMessageType.getUpdate() != null) {

                    String deviceType = DeviceTypes.getDeviceTypes(getApplication(), DeviceTypes.Device.DEVICE_TYPE);

                    Log.d("DeviceType","Device Type is :" + deviceType );



//
                        Update update = receivedMessageType.getUpdate();

                        String string = update.getString();

                       //Context context =  update.getContext();
//
//                        int n = update.getInteger();
//
//                        this.context = context;
////                    PackageManager pm = context.getPackageManager();
////                    Intent intent = pm.getLaunchIntentForPackage("com.example.android.wifidirect.discovery");
////                    context.startActivity(intent);
//
//
//                        final Bitmap bitmap2 = BitmapFactory.decodeFile(filePath);
//
//
//                        Toast.makeText(getApplicationContext(), "Integer value" + n, Toast.LENGTH_LONG).show();
//                        //   Integer n = receivedMessageType.getInteger();
//                        Matrix mtx = new Matrix();
//                        mtx.postRotate(n);
//                        Bitmap rotatedImage = bitmap2.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(), mtx, true);
//                        imageView.setImageBitmap(rotatedImage);
//                        // //zoom = new Zoom(imageView);


                }


                break;



            case MY_HANDLE:
                Object obj = msg.obj;

               //When we have just two devices that we want to connect them together we use : chatManager = (ChatManager) msg.obj;
              // if (role.equals("groupOwner")){
                //	(chatFragment).setChatManager((ChatManager) obj);

               if (chatManagerList == null) {
                   chatManagerList = new ArrayList<ChatManager>(5);
                   chatManagerList.add((ChatManager) obj);

               }
                chatManagerList.add((ChatManager) obj);
             //  }

        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
//        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
//        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        //unregisterReceiver(receiver);
    }


    @Override
	public void onPeersAvailable(WifiP2pDeviceList peerList) {

        // update the UI and add the item the discovered
        // device.



//            List<WifiP2pDevice> ProximityGroups = new ArrayList<WifiP2pDevice>();
//            serviceList.clear();
//
//            ProximityGroups.addAll(peerList.getDeviceList());

        serviceList.clear();
        Toast.makeText(getApplicationContext(),  "on Peers av", Toast.LENGTH_SHORT).show();

          WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                  .findFragmentByTag("services");
           if (fragment != null) {
              WiFiDirectServicesList.WiFiDevicesAdapter adapter = ((WiFiDirectServicesList.WiFiDevicesAdapter) fragment
                       .getListAdapter());

               for(WifiP2pDevice elements: peerList.getDeviceList()){
                   Log.d("onPeersAvailable", "onPeers discovery started" + elements);

                    WiFiP2pService service = new WiFiP2pService();
                    service.device = elements;
                    service.instanceName = "ff";
                    service.serviceRegistrationType = "ff";
                    serviceList.add(service);


                   String s="Debug-infos:";
                   s += "\n OS Version: " + System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
                   s += "\n OS API Level: " + android.os.Build.VERSION.SDK_INT;
                   s += "\n Device: " + android.os.Build.DEVICE;
                   s += "\n Model (and Product): " + android.os.Build.MODEL + " ("+ android.os.Build.PRODUCT + ")";


                   Log.d("DeviceInfo", ""+s);


               }
               adapter.clear();
               adapter.addAll(serviceList);
               adapter.notifyDataSetChanged();
           }else {
               Toast.makeText(getApplicationContext(), "fragment is null", Toast.LENGTH_SHORT).show();
           }

        if (peerList.getDeviceList().size() == 0) {
            Toast.makeText(getApplicationContext(),  "No devices found", Toast.LENGTH_SHORT).show();
            Log.d(WiFiServiceDiscoveryActivity.TAG, "No devices found");
            return;
        }
    }




    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Thread handler = null;

        /*
         * The group owner accepts connections using a server socket and then spawns a
         * client socket for every client. This is handled by {@code
         * GroupOwnerSocketHandler}
         */

        if (p2pInfo.isGroupOwner) {
            Toast.makeText(getApplicationContext(), "Group Owner", Toast.LENGTH_SHORT).show();
        	//role = "groupOwner";

            manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {


                   Collection<WifiP2pDevice> clientsCollection = group.getClientList();
                    clientsList = new ArrayList<String>();
                    for(WifiP2pDevice elements: clientsCollection) {
                        clientsList.add(elements.deviceName);
                        Log.d("onGroupInfoAvailable", "onGroupInfoAvailable" + clientsList);
                    }


                }
            });
            Log.d(TAG, "Connected as group owner");
//            manager.discoverPeers(channel, new ActionListener() {
//
//            @Override
//            public void onSuccess() {
//                //appendStatus("Connecting to service");
//            	Log.d("Discovery", "Discovery started again");
//            }
//
//            @Override
//            public void onFailure(int errorCode) {
//                //appendStatus("Failed connecting to service");
//            	Log.d("Discovery", "Discovery not started again");
//            }
//        });
            try {
                handler = new GroupOwnerSocketHandler(((MessageTarget) this).getHandler());
                handler.start();

               // chatFragment = new WiFiChatFragment("groupOwner");

            } catch (IOException e) {
                Log.d(TAG,"Failed to create a server thread - " + e.getMessage());
                return;
            } catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
            }

            //chatManager.writeObject(messageType1);
        } else {
            Log.d(TAG, "Connected as peer");


            //role = "client";
            handler = new ClientSocketHandler(((MessageTarget) this).getHandler(),p2pInfo.groupOwnerAddress);
            handler.start();

            }


//        if (receivedRole.equals("Group Owner")){
//            sendImage.setVisibility(View.VISIBLE);
//        } else{
//            sendImage.setVisibility(View.GONE);
//        }

//        getFragmentManager().beginTransaction().replace(R.id.container_root, chatFragment).commit();
  //      statusTxtView.setVisibility(View.GONE);
    }

    public void appendStatus(String status) {
        String current = statusTxtView.getText().toString();
        statusTxtView.setText(current + "\n" + status);
    }

//	@Override
//	public void onPeersAvailable(WifiP2pDeviceList peers) {
//		List<WifiP2pDevice> ProximityGroups = new ArrayList<WifiP2pDevice>();
//		ProximityGroups.addAll(peers.getDeviceList());
//
//		for(WifiP2pDevice device: ProximityGroups){
//			if (device.isGroupOwner()){
//				Log.d("GroupOwner", "GroupOwner found" + device.deviceName);
//				config.deviceAddress = device.deviceAddress;
//				manager.connect(channel, config, null);
//				break;
//			}
//		}
////		// TODO Auto-generated method stub
////
//	}



    public void disconnect() {
    	if (manager != null && channel != null) {
			manager.removeGroup(channel, new ActionListener() {

				@Override
				public void onFailure(int reasonCode) {
					appendStatus("Group cannot be Removed: ");
				}

				@Override
				public void onSuccess() {
					Log.d("OnExit", "Remove group successfull");
					appendStatus("Group Removed");
				}

			});


			if (true){
				manager.cancelConnect(channel, new ActionListener() {

					@Override
					public void onSuccess() {
						Log.d("OnExit", "cancel connect successfull");
					appendStatus("Current Connection Terminated");
					}

					@Override
					public void onFailure(int arg0) {
						appendStatus("Current Connection not Terminated: " );
					}
				});
			}
			manager.clearLocalServices(channel, new ActionListener() {

				@Override
				public void onSuccess() {
					Log.d("OnExit", "Clear local services successfull");
					appendStatus("Local Services Cleared");
				}

				@Override
				public void onFailure(int arg0) {
					appendStatus("Local Services not Cleared: " );
				}
			});
			manager.clearServiceRequests(channel, new ActionListener() {

				@Override
				public void onSuccess() {
					Log.d("OnExit", "Clear service requests successfull");
					appendStatus("All Service Requests Cleared");
					unregisterReceiver(receiver);
					appendStatus("Receiver Unregistered");
					android.os.Process.killProcess(android.os.Process.myPid());
					System.exit(0);
				}

				@Override
				public void onFailure(int arg0) {
					appendStatus("All Service Requests not Cleared: ");
				}
			});
		}
	}



	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clean:
           // chatManager.writeObject("SALAM Anita");
        	    disconnect();

            case R.id.Wifi_direct_enable:
                if (manager != null && channel != null) {

                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.

                   // startActivity(new Intent(Settings.ACTION_WIFI_IP_SETTINGS));
                    startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
                } else {
                    Log.e(TAG, "channel or manager is null");
                }
                return true;

            case R.id.Wifi_direct_discover:
                if (!isWifiP2pEnabled) {
                    Toast.makeText(WiFiServiceDiscoveryActivity.this, R.string.Wifi_Direct_off_warning,
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                manager.discoverPeers(channel, new ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery Initiated",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WiFiServiceDiscoveryActivity.this, "Discovery Failed : " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return true;

//            case  R.id.applications:
//                Intent intent = new Intent(this, ApkListActivity.class);
//                startActivity(intent);


        default:
        return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }


}
