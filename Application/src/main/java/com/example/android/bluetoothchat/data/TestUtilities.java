package com.example.android.bluetoothchat.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.bluetoothchat.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your TemperatureContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {
    static final Integer TEST_SENSOR = 99;
    static final String TEST_DATE_STRING = "2015-10-01 10:11:12";

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + valueCursor.getString(idx) +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static ContentValues createSensorValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(TemperatureContract.SensorEntry.COLUMN_LOCATION, "Seoul");
        testValues.put(TemperatureContract.SensorEntry.COLUMN_INSTALLDATE, TEST_DATE_STRING);
        testValues.put(TemperatureContract.SensorEntry.COLUMN_SENSORTYPE, "PT100");
        testValues.put(TemperatureContract.SensorEntry.COLUMN_METRIC, 1);
        testValues.put(TemperatureContract.SensorEntry.COLUMN_CALIBRATED, 0);
        testValues.put(TemperatureContract.SensorEntry.COLUMN_CAL_A, 0);
        testValues.put(TemperatureContract.SensorEntry.COLUMN_CAL_B, 1);
        return testValues;
    }

    static long insertSensorValues(Context context) {
        // insert our test records into the database
        TemperatureDbHelper dbHelper = new TemperatureDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createSensorValues();

        long sensorRowId;
        sensorRowId = db.insert(TemperatureContract.SensorEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert sensor values", sensorRowId != -1);

        return sensorRowId;
    }

    static ContentValues createTemperatureValues(long sensorRowId) {
        ContentValues temperatureValues = new ContentValues();
        temperatureValues.put(TemperatureContract.TemperatureEntry.COLUMN_SENSORID, sensorRowId);
        temperatureValues.put(TemperatureContract.TemperatureEntry.COLUMN_CREATED, TEST_DATE_STRING);
        temperatureValues.put(TemperatureContract.TemperatureEntry.COLUMN_VALUE, 40.123);
        temperatureValues.put(TemperatureContract.TemperatureEntry.COLUMN_METRIC, 1);
        temperatureValues.put(TemperatureContract.TemperatureEntry.COLUMN_CALIBRATED, 0);
        return temperatureValues;
    }

    static ContentValues createCalibrationValues(long sensorRowId) {
        ContentValues calibrationValues = new ContentValues();
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_SENSORID, sensorRowId);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_CREATED, TEST_DATE_STRING);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_CAL_A_OLD, 0);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_CAL_B_OLD, 1);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_CAL_A_NEW, 0.12345);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_CAL_B_NEW, 0.999991);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_REF_VALUE_HIGH, 250);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_REF_VALUE_LOW, 10.1);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_READ_VALUE_HIGH, 250.123);
        calibrationValues.put(TemperatureContract.CalibrationEntry.COLUMN_READ_VALUE_LOW, 10.234);
        return calibrationValues;
    }

    /*
        The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
