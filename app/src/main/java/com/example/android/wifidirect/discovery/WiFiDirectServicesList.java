
package com.example.android.wifidirect.discovery;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple ListFragment that shows the available services as published by the
 * peers
 */
public class WiFiDirectServicesList extends ListFragment  {

    WiFiDevicesAdapter listAdapter = null;
    private List<WiFiP2pService> peers = new ArrayList<WiFiP2pService>();
    ProgressDialog progressDialog = null;

    interface DeviceClickListener {
        public void connectP2p(WiFiP2pService wifiP2pService);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.devices_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listAdapter = new WiFiDevicesAdapter(this.getActivity(),
                android.R.layout.simple_list_item_2, android.R.id.text1,
                peers);
        setListAdapter(listAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        ((DeviceClickListener) getActivity()).connectP2p((WiFiP2pService) l
                .getItemAtPosition(position));
        ((TextView) v.findViewById(android.R.id.text2)).setText("Connecting");

    }


    public void clearData() {
        // clear the data
        listAdapter.getItem().clear();
    }


    public class WiFiDevicesAdapter extends ArrayAdapter<WiFiP2pService> {

        public List<WiFiP2pService> items;

        public WiFiDevicesAdapter(Context context, int resource,
                int textViewResourceId, List<WiFiP2pService> items) {
            super(context, resource, textViewResourceId, items);
            this.items = items;

        }

        public List<WiFiP2pService> getItem(){
            return this.items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_2, null);
            }
            WiFiP2pService service = items.get(position);
            if (service != null) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);

                if (nameText != null) {
                    nameText.setText(service.device.deviceName + " - " + service.instanceName);
                }
                TextView statusText = (TextView) v
                        .findViewById(android.R.id.text2);
                statusText.setText(getDeviceStatus(service.device.status));
            }
            return v;
        }



    }
//    @Override
//    public void onPeersAvailable(WifiP2pDeviceList peerList) {
//
//
//        List<WifiP2pDevice> ProximityGroups = new ArrayList<WifiP2pDevice>();
//        //ProximityGroups.clear();
//        ProximityGroups.addAll(peers.getDeviceList());
//        if (progressDialog != null && progressDialog.isShowing()) {
//            progressDialog.dismiss();
//        }
//        peers.clear();
//        peers.addAll(peerList.getDeviceList());
//        ((WiFiDevicesAdapter) getListAdapter()).notifyDataSetChanged();
//        if (peers.size() == 0) {
//            Log.d(WiFiServiceDiscoveryActivity.TAG, "No devices found");
//            return;
//        }
//
//    }




    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }


    public static String getDeviceStatus(int statusCode) {
        switch (statusCode) {
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";

        }
    }

    /**
     * An interface-callback for the activity to listen to fragment interaction
     * events.
     */
    public interface DeviceActionListener {

      //  void showDetails(WifiP2pDevice device);

      //  void cancelDisconnect();

        void connectP2p(WiFiP2pService service) ;

        void disconnect();
    }

}
