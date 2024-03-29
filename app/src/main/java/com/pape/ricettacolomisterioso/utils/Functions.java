package com.pape.ricettacolomisterioso.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.TypedValue;

import androidx.annotation.ColorInt;

import com.pape.ricettacolomisterioso.DeviceBootReceiver;
import com.pape.ricettacolomisterioso.NotificationReceiver;
import com.pape.ricettacolomisterioso.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Functions {
    public static int time_in_day_remain(Date expiring, Date today) {
        long time_in_millisecond = expiring.getTime() - today.getTime();
        return (int) TimeUnit.DAYS.convert(time_in_millisecond, TimeUnit.MILLISECONDS);
    }


    public static int percentual_for_bar(Date purchase, Date expiring, Date today) {
        long exp_pur = expiring.getTime() - purchase.getTime();
        long tod_pur = today.getTime() - purchase.getTime();
        double percentual_value_for_progress_bar = ((double) tod_pur / (double) exp_pur) * 100;
        return (int) Math.round(percentual_value_for_progress_bar);
    }

    public static Date ExcludeTime(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        final int day = c.get(Calendar.DAY_OF_MONTH);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);
        c.clear();
        c.set(year, month, day);
        return c.getTime();
    }

    public static @ColorInt
    int getThemeColor(Context context, int ResId) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(ResId, typedValue, true);
        @ColorInt int color = typedValue.data;
        return color;
    }

    public static String getProductCategoryString(Context context, int categoryId) {
        return context.getResources().getStringArray(R.array.categoriesString)[categoryId];
    }

    public static String getRecipeCategoryString(Context context, int categoryId) {
        return context.getResources().getStringArray(R.array.recipeCategoriesString)[categoryId];
    }

    public static int getRecipeCategoryIndex(Context context, String category) {
        List<String> CATEGORIES = Arrays.asList(context.getResources().getStringArray(R.array.recipeCategoriesString));
        return CATEGORIES.indexOf(category);
    }

    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, DeviceBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


    public static void SetAlarmManager(Context context) {
        //enable boot receiver
        enableBootReceiver(context);

        //set alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar timeLunch = Calendar.getInstance();
        timeLunch.setTimeInMillis(System.currentTimeMillis());
        timeLunch.set(Calendar.HOUR_OF_DAY, 12);
        timeLunch.set(Calendar.MINUTE, 0);

        Calendar timeDinner = Calendar.getInstance();
        timeDinner.setTimeInMillis(System.currentTimeMillis());
        timeDinner.set(Calendar.HOUR_OF_DAY, 19);
        timeDinner.set(Calendar.MINUTE, 0);

        Intent intentLaunch = new Intent(context, NotificationReceiver.class);
        intentLaunch.putExtra("TIME", 0);
        PendingIntent pendingIntentLaunch = PendingIntent.getBroadcast(context, 0, intentLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intentDinner = new Intent(context, NotificationReceiver.class);
        intentDinner.putExtra("TIME", 1);
        PendingIntent pendingIntentDinner = PendingIntent.getBroadcast(context, 1, intentDinner, PendingIntent.FLAG_UPDATE_CURRENT);

        //boolean lunchAlarmUp = (PendingIntent.getBroadcast(context, 0, intentLaunch, PendingIntent.FLAG_NO_CREATE)!= null);
        //boolean dinnerAlarmUp = (PendingIntent.getBroadcast(context, 1, intentDinner, PendingIntent.FLAG_NO_CREATE)!= null);

        if (alarmManager != null/* && !lunchAlarmUp && !dinnerAlarmUp*/) {
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeLunch.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentLaunch);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, timeDinner.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentDinner);
        }
    }

    public static void ClearAlarmManager(Context context) {
        //disable boot receiver
        disableBootReceiver(context);

        //cancel alarm manager
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            Intent intentLaunch = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntentLaunch = PendingIntent.getBroadcast(context, 0, intentLaunch, PendingIntent.FLAG_UPDATE_CURRENT);
            Intent intentDinner = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntentDinner = PendingIntent.getBroadcast(context, 1, intentDinner, PendingIntent.FLAG_UPDATE_CURRENT);

            alarmManager.cancel(pendingIntentLaunch);
            alarmManager.cancel(pendingIntentDinner);
        }
    }

    public static String SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/QKing/saved_images");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
