package com.example.walk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.model.LatLng;

public class MyWalkService extends Service {
    static Thread mThread;
    private static int time;
    static Location lastLocation;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("서비스", String.valueOf(mThread == null));

        if (lastLocation == null) {
            lastLocation = WalkStart.gpsTracker.getLocation();
        }

        if (mThread == null) {
            mThread = new Thread("ServiceThread") {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        try {

                            Thread.sleep(1000);
                            time++;
                            location latLng = new location(WalkStart.gpsTracker.getLatitude(),WalkStart.gpsTracker.getLongitude());
                            Location location = WalkStart.gpsTracker.getLocation();
                            Log.d("로케이션", String.valueOf(lastLocation));
                            Log.d("로케이션", String.valueOf(location));

                            double km = lastLocation.distanceTo(location)/1000.0;
                            double mtime = time;
                            double speed = km / mtime;
                            Log.d("시간과 거리",String.valueOf(mtime)+"/"+String.valueOf(km));
                            WalkInformation information = new WalkInformation(latLng, km, speed, 0, time);

                            Message message = new Message();
                            message.what = 1;
                            message.obj = information;

                            WalkStart.handler.sendMessage(message);
/*                            lastLocation = location;*/

                        } catch (InterruptedException e) {
                            break;
                        }
                        Log.d("서비스", "서비스 동작중 " + time);
                    }
                }
            };
            mThread.start();
        }

        startForegroundService();

        return super.onStartCommand(intent, flags, startId);
    }

    static void onStop() {
        if (mThread != null) {
            mThread.interrupt();
        }
    }

    static void onReStart() {
        if (mThread != null) {
            mThread = null;
            mThread = new Thread("ServiceThread") {
                @Override
                public void run() {
                    for (int i = 0; i < 1000; i++) {
                        try {
                            Thread.sleep(1000);
                            time++;
                            location latLng = new location(WalkStart.gpsTracker.getLatitude(),WalkStart.gpsTracker.getLongitude());
                            Location location = WalkStart.gpsTracker.getLocation();
                            Log.d("로케이션", String.valueOf(lastLocation));
                            Log.d("로케이션", String.valueOf(location));

                            double km = lastLocation.distanceTo(location)/1000.0;
                            double mtime = time;
                            double speed = km / mtime;
                            Log.d("시간과 거리",String.valueOf(mtime)+"/"+String.valueOf(km));
                            WalkInformation information = new WalkInformation(latLng, km, speed, 0, time);

                            Message message = new Message();
                            message.what = 1;
                            message.obj = information;

                            WalkStart.handler.sendMessage(message);
                            /*                            lastLocation = location;*/

                        } catch (InterruptedException e) {
                            break;
                        }
                        Log.d("서비스", "서비스 동작중 " + time);
                    }
                }
            };
            mThread.start();
        }
    }

    @Override
    public void onDestroy() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
            time = 0;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void startForegroundService() {
        Log.d("서비스", "포그라운드 서비스 메소드");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");
        builder.setSmallIcon(R.mipmap.ic_launcher);     //노티피케이션 아이콘
        builder.setContentTitle("즐겁게 산책하는 중");  //노티피케이션 타이틀
        builder.setContentText("산책이 기록되고 있는 중");    //노티피케이션 내용

        Intent notificationIntent = new Intent(this, WalkStart.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(new NotificationChannel("default", "기본채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        startForeground(1, builder.build());

    }
}
