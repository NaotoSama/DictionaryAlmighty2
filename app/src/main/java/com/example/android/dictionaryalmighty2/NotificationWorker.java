//package com.example.android.dictionaryalmighty2;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.app.TaskStackBuilder;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Build;
//import android.provider.Settings;
//import android.widget.RemoteViews;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
//import static com.example.android.dictionaryalmighty2.NotificationReceiver.wordToMemorize;
//
//public class NotificationWorker extends Worker {
//    private static final String WORK_RESULT = "work_result";
//
//
//    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//
//        showNotification("Hey I'm your worker", "Work is done");
//
//        return Result.success();
//
//    }
//
//
//    public int createID(){
//        Date now = new Date();
//        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmssSS",  Locale.TAIWAN).format(now));
//        return id;
//    }
//
//
//    private void showNotification(String task, String desc) {
//
//        // Inflate custom normal and expanded notification views
//        RemoteViews collapsedNotificationView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification_normal_view);
//                    collapsedNotificationView.setTextViewText(R.id.normal_notification_title, "還記得這個字嗎：" + wordToMemorize + "?");
//        RemoteViews expandedNotificationView = new RemoteViews(getApplicationContext().getPackageName(),R.layout.custom_notification_expanded_view);
//                    expandedNotificationView.setTextViewText(R.id.expanded_notification_title, "還記得這個字嗎：" + wordToMemorize + "?");
//
//        // Creates an explicit intent for an Activity in your app
//        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
//
//        // The stack builder object will contain an artificial back stack for the started Activity.
//        // This ensures that navigating backward from the Activity leads out of your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
//
//        // Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(MainActivity.class);
//
//        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//
//
//        Intent broadcastIntent = new Intent(getApplicationContext(), NotificationReceiver.class);
//        broadcastIntent.putExtra("vocabularyToBeMemorized", wordToMemorize);
//        PendingIntent actionIntent = PendingIntent.getBroadcast(getApplicationContext(),
//                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        // Get notification manager
//        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            NotificationChannel channel = new
//                    NotificationChannel("simplfiedcoding", "simplfiedcoding", NotificationManager.IMPORTANCE_DEFAULT);
//            manager.createNotificationChannel(channel);
//        }
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "simplfiedcoding")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLights(Color.YELLOW , 1000 , 1000)  //設定閃爍燈：3個引數：顏色，燈亮時長，燈暗時長
//                .setColor(Color.BLUE)
//                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setAutoCancel(true)  //Dismiss on tap
//                .setOnlyAlertOnce(true)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())  //Set custom style
//                .setCustomContentView(collapsedNotificationView)
//                .setCustomBigContentView(expandedNotificationView)
//                .setContentIntent(resultPendingIntent)
//                .addAction(R.mipmap.bio_medical_dictionary,"yes",actionIntent);
//
//
//        int id = createID();
//        manager.notify(id, builder.build());
//
//    }
//
//}
