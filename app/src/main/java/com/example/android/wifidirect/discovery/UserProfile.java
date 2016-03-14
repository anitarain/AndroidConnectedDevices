package com.example.android.wifidirect.discovery;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by anitaimani on 24/02/16.
 */
public class UserProfile extends Activity {

        //The "x" and "y" position of the "Show Button" on screen.
        Point p;
//
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.main);
//
//            Button btn_show = (Button) findViewById(R.id.show_popup);
//            btn_show.setOnClickListener(new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(View arg0) {
//
//                    //Open popup window
//                    if (p != null)
//                        showPopup(UserProfile.this, p);
//                }
//            });
//        }

        // Get the x and y position after the button is draw on screen
// (It's important to note that we can't get the position in the onCreate(),
// because at that stage most probably the view isn't drawn yet, so it will return (0, 0))
        @Override
        public void onWindowFocusChanged(boolean hasFocus) {

            int[] location = new int[2];
            Button button = (Button) findViewById(R.id.userRole);

            // Get the x, y location and store it in the location[] array
            // location[0] = x, location[1] = y.
            button.getLocationOnScreen(location);

            //Initialize the Point with x, and y positions
            p = new Point();
            p.x = location[0];
            p.y = location[1];
        }

        // The method that displays the popup.
        public void showPopup(Activity context) {
            int popupWidth = 200;
            int popupHeight = 150;

            // Inflate the popup_layout.xml
            LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup);
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = layoutInflater.inflate(R.layout.user_profile, viewGroup);

            // Creating the PopupWindow
            final PopupWindow popup = new PopupWindow(context);
            popup.setContentView(layout);
            popup.setWidth(popupWidth);
            popup.setHeight(popupHeight);
            popup.setFocusable(true);

            // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
            int OFFSET_X = 30;
            int OFFSET_Y = 30;

            // Clear the default translucent background
            popup.setBackgroundDrawable(new BitmapDrawable());

            // Displaying the popup at the specified location, + offsets.
          //  popup.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y);

            // Getting a reference to Close button, and close the popup when clicked.
            Button close = (Button) layout.findViewById(R.id.buttonDismiss);
            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    popup.dismiss();
                }
            });
        }
    }

