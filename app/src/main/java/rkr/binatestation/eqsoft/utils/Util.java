package rkr.binatestation.eqsoft.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import rkr.binatestation.eqsoft.activities.LoginActivity;

/**
 * Created by RKR on 27-01-2016.
 * Util.
 */
public class Util {

    /**
     * static variable for saving images in external storage directory.
     */

    private static final String databasePath = Environment.getExternalStorageDirectory().toString() +
            File.separator + "EQSoft" + File.separator + "DATABASE" + File.separator;

    /**
     * static method used to get the External Storage path which ensures whether it exits or not
     */

    public static String getDatabasePath() {
        File file = new File(databasePath);
        if (file.exists()) {
            return databasePath;
        } else {
            if (file.mkdirs()) {
                return databasePath;
            } else {
                return Environment.getExternalStorageDirectory().toString() + File.separator;
            }
        }
    }

    public static String getCurrentDate(String dateFormat) {
        return new SimpleDateFormat(dateFormat, Locale.getDefault()).format(new Date());
    }


    public static void alert(final Activity context, String title, String message, final Boolean isBack) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (isBack) {
                                context.onBackPressed();
                            }
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * Shows the progress UI and hides the content form.
//     */
//    public static void showProgressOrError(FragmentManager fragmentManager, int contentFormId, int type, String tag) {
//        try {
//            fragmentManager.beginTransaction()
//                    .add(contentFormId, ProgressError.newInstance(type), tag)
//                    .commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void showAlert(final Activity activity, String title, String message, final Boolean goBack) {
        try {
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (goBack) {
                                activity.onBackPressed();
                            }
                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void logoutAlert(final Activity activity, String title, String message) {
        try {
            new AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.getSharedPreferences(activity.getPackageName(), Context.MODE_PRIVATE)
                                    .edit().putBoolean(Constants.KEY_IS_LOGGED_IN, false)
                                    .putString(Constants.KEY_LAST_SELECTED_CUSTOMER, "").apply();
                            activity.startActivity(new Intent(
                                    activity,
                                    LoginActivity.class
                            ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(final Context context, String title, String message) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void putStringsToSharedPreferences(Context context, Map<String, String> values) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
        for (String key : values.keySet()) {
            editor.putString(key, values.get(key));
        }
        editor.apply();
    }

    public static void putBooleansToSharedPreferences(Context context, Map<String, Boolean> values) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
        for (String key : values.keySet()) {
            editor.putBoolean(key, values.get(key));
        }
        editor.apply();
    }

    public static void putIntsToSharedPreferences(Context context, Map<String, Integer> values) {
        SharedPreferences.Editor editor = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).edit();
        for (String key : values.keySet()) {
            editor.putInt(key, values.get(key));
        }
        editor.apply();
    }

    public static String getStringFromSharedPreferences(Context context, String key) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getString(key, "");
    }

    public static Boolean getBooleanFromSharedPreferences(Context context, String key) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE).getBoolean(key, false);
    }

}
