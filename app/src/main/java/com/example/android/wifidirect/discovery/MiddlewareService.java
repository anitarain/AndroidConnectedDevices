package com.example.android.wifidirect.discovery;



    import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

    /**
     * Created by anitaimani on 15/01/16.
     */
    public class MiddlewareService extends Service{

        private static final int NOTIFICATION = 1;
        public static final String CLOSE_ACTION = "close";


        @Nullable
        private NotificationManager mNotificationManager = null;
        private final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);
        private Activity context;


        @Override
        public void onCreate() {

            if (mNotificationManager == null) {
                mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, WiFiServiceDiscoveryActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
                    0);
            PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, WiFiServiceDiscoveryActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
                            .setAction(CLOSE_ACTION),
                    0);
            mNotificationBuilder
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setCategory(NotificationCompat.CATEGORY_SERVICE)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentTitle(getText(R.string.app_name))
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent)
                    .addAction(android.R.drawable.ic_menu_close_clear_cancel,
                            getString(R.string.action_exit), pendingCloseIntent)
                    .setOngoing(true);


            showNotification();

        }


        @Override
        public IBinder onBind(Intent arg0) {
            // TODO Auto-generated method stub
            return null;
        }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // TODO Auto-generated method stub
//        return START_STICKY;
//    }



        //Check running Activity

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.i("LocalService", "Received start id " + startId + ": " + intent);
            return START_NOT_STICKY;
        }


        @Override
        public void onTaskRemoved(Intent rootIntent) {
            // TODO Auto-generated method stub
            Intent restartService = new Intent(getApplicationContext(),
                    this.getClass());
            restartService.setPackage(getPackageName());
            PendingIntent restartServicePI = PendingIntent.getService(
                    getApplicationContext(), 1, restartService,
                    PendingIntent.FLAG_ONE_SHOT);

            //Restart the service once it has been killed android


            AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +100, restartServicePI);

        }

        @Override
        public void onStart(Intent intent, int startId) {
            // TODO Auto-generated method stub
            super.onStart(intent, startId);
            Log.d("StartedService", "FirstService started");
            //Stop the service, if it was previously started.
            this.stopSelf();
        }

        //    @Override
//    public void onDestroy() {
//        // TODO Auto-generated method stub
//        super.onDestroy();
//        Log.d("StopedService", "FirstService destroyed");
//    }
        @Override
        public void onDestroy() {
            // Cancel the persistent notification.
            mNotificationManager.cancel(NOTIFICATION);

            // Tell the user we stopped.
            Toast.makeText(this,  "FirstService destroyed" , Toast.LENGTH_SHORT).show();
        }


        private void showNotification() {
            mNotificationBuilder
                    .setTicker(getText(R.string.service_connected))
                    .setContentText(getText(R.string.service_connected));
            if (mNotificationManager != null) {
                mNotificationManager.notify(NOTIFICATION, mNotificationBuilder.build());
            }
        }





    }


