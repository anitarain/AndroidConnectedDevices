
package com.example.android.wifidirect.discovery;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment handles chat related UI which includes a list view for messages
 * and a message entry field with send button.
 */
public class WiFiChatFragment extends Fragment{

    private View view;
    private ChatManager chatManager;
    private List<ChatManager> listchatManager;
    private String role;
    private TextView chatLine;
    private ListView listView;
    private ListView listPack;
    ChatMessageAdapter adapter = null;
    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    MessageType messageType;

    private List<String> items = new ArrayList<String>();

    public Context context;
    public Parcel parcel;

    public WiFiChatFragment(){
    	
    }
    
  public WiFiChatFragment(String role){
    	this.role=role;
    	listchatManager = new ArrayList<ChatManager>();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatLine = (TextView) view.findViewById(R.id.txtChatLine);
        listView = (ListView) view.findViewById(android.R.id.list);
        adapter = new ChatMessageAdapter(getActivity(), android.R.id.text1,
                items);
        listView.setAdapter(adapter);
        view.findViewById(R.id.button1).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (chatManager != null) {
                    	//if (! listchatManager.isEmpty()) {


                            Intent chooseImage = new Intent(Intent.ACTION_PICK);
                            chooseImage.setType("image/*");
                            startActivityForResult(chooseImage, CHOOSE_FILE_RESULT_CODE);
                        }
                    }
                });
        return view;
    }




//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Bitmap selectedphoto = null;
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == getActivity().RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//           // Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
////            cursor.moveToFirst();
////            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////            String filePath = cursor.getString(columnIndex);
////            selectedphoto = BitmapFactory.decodeFile(filePath);
////            cursor.close();
//
//            Toast.makeText(getActivity().getApplicationContext(), "Image Selected chatManager", Toast.LENGTH_LONG).show();
//            chatManager.writeObject("55");
//
//        }
//    }


    public void distributeIntent(){
    	if (! listchatManager.isEmpty()) { 
    	Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+391234567"));
        pushMessage("Me: " + "Intent .." + callIntent.getAction());
        chatLine.setText("");
        chatLine.clearFocus();
        for(ChatManager m: listchatManager){
        	m.writeObject(callIntent.toUri(0));
        }
    	 }
    	
    }



    public interface MessageTarget {
        public Handler getHandler();
    }

    public void setChatManager(ChatManager obj) {
        this.chatManager = obj;
    }
    
    //public void addChatManager(ChatManager obj) {
     //   this.listchatManager.add(obj);
    //}


    public void pushMessage(String readMessage) {
        adapter.add(readMessage);
        adapter.notifyDataSetChanged();
    }



    /**
     * ArrayAdapter to manage chat messages.
     */
    public class ChatMessageAdapter extends ArrayAdapter<String> {

        List<String> messages = null;

        public ChatMessageAdapter(Context context, int textViewResourceId,
                List<String> items) {
            super(context, textViewResourceId, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(android.R.layout.simple_list_item_1, null);
            }
            String message = items.get(position);
            if (message != null && !message.isEmpty()) {
                TextView nameText = (TextView) v
                        .findViewById(android.R.id.text1);

                if (nameText != null) {
                    nameText.setText(message);
                    if (message.startsWith("Me: ")) {
                        nameText.setTextAppearance(getActivity(),
                                R.style.normalText);
                    } else {
                        nameText.setTextAppearance(getActivity(),
                                R.style.boldText);
                    }
                }
            }
            return v;
        }
    }
}
