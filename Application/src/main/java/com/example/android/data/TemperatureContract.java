package com.example.android.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the temperature database.
 */
public class TemperatureContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.bluetoothchat";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/temperature/ is a valid path for
    // looking at temperature data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_TEMPERATURE = "temperature";
    public static final String PATH_SENSOR = "sensor";
    public static final String PATH_CALIBRATION = "calibration";

    /* Inner class that defines the table contents of the sensor table */
    public static final class SensorEntry implements BaseColumns {
        // Table name and columns
        public static final String TABLE_NAME = "sensor";
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_INSTALLDATE = "installdate";
        public static final String COLUMN_SENSORTYPE = "sensortype";
        public static final String COLUMN_METRIC = "metric";
        public static final String COLUMN_CALIBRATED = "calibrated";
        public static final String COLUMN_CAL_A = "cal_a";
        public static final String COLUMN_CAL_B = "cal_b";

        // Contract URIs
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SENSOR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SENSOR;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SENSOR;

        public static Uri buildSensorUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the table contents of the temperature table */
    public static final class TemperatureEntry implements BaseColumns {
        // Table name and columns
        public static final String TABLE_NAME = "temperature";
        // Column with the foreign key into the sensor table.
        public static final String COLUMN_SENSORID = "sensorid";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_VALUE = "value";
        public static final String COLUMN_METRIC = "metric";
        public static final String COLUMN_CALIBRATED = "calibrated";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TEMPERATURE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEMPERATURE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TEMPERATURE;

        // sacar temperaturas
        public static Uri buildTemperatureUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // sacar temperaturas de um sensor
        public static Uri buildTemperatureSensor(int sensorId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(sensorId)).build();
        }

        // sacar temperatura máxima de um sensor
        public static Uri buildMaxTemperatureSensor(int sensorId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(sensorId)).build();
        }

        // sacar temperatura mínima de um sensor
        public static Uri buildMinTemperatureSensor(int sensorId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(sensorId)).build();
        }

        public static Uri buildTemperatureSensorWithDateParameter(int sensorId, String date) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(sensorId))
                    .appendQueryParameter(COLUMN_CREATED, date).build();
        }

        public static Uri buildTemperatureSensorWithDate(int sensorId, String date) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(sensorId))
                    .appendPath(date).build();
        }

        // sacar o sensorId presente num URL
        public static String getSensorFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static String getDateParameterFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_CREATED);
            if (null != dateString && dateString.length() > 0)
                return dateString;
            else
                return null;
        }
    }

    public static final class CalibrationEntry implements BaseColumns {
        // Table name and columns
        public static final String TABLE_NAME = "calibration";
        // Column with the foreign key into the sensor table.
        public static final String COLUMN_SENSORID = "sensorid";
        public static final String COLUMN_CREATED = "created";
        public static final String COLUMN_CAL_A_OLD = "cal_a_old";
        public static final String COLUMN_CAL_B_OLD = "cal_b_old";
        public static final String COLUMN_CAL_A_NEW = "cal_a_new";
        public static final String COLUMN_CAL_B_NEW = "cal_b_new";
        public static final String COLUMN_REF_VALUE_HIGH = "ref_value_high";
        public static final String COLUMN_REF_VALUE_LOW = "ref_value_low";
        public static final String COLUMN_READ_VALUE_HIGH = "read_value_high";
        public static final String COLUMN_READ_VALUE_LOW = "read_value_low";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CALIBRATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALIBRATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CALIBRATION;

        public static Uri buildCalibrationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // get calibrations from one sensor
        public static Uri buildCalibrationSensor(int sensorId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(sensorId)).build();
        }

        // get last calibration from one sensor
        public static Uri buildLastCalibrationSensor(int sensorId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(sensorId)).build();
        }

    }

}
