/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.data.TemperatureContract.SensorEntry;
import com.example.android.data.TemperatureContract.TemperatureEntry;
import com.example.android.data.TemperatureContract.CalibrationEntry;

/**
 * Manages a local database for weather data.
 */
public class TemperatureDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "temperature.db";

    public TemperatureDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        /*
        CREATE TABLE sensor(
            _id INTEGER PRIMARY KEY autoincrement,
            location TEXT NOT NULL,
            installdate DATETIME DEFAULT CURRENT_TIMESTAMP,
            sensortype TEXT NOT NULL,
            metric INTEGER DEFAULT 1 NOT NULL,
            calibrated INTEGER DEFAULT 0 NOT NULL,
            cal_a FLOAT DEFAULT 0 NOT NULL,
            cal_b FLOAT DEFAULT 1 NOT NULL
        );
        */
        final String SQL_CREATE_SENSOR_TABLE = "CREATE TABLE " + SensorEntry.TABLE_NAME + " (" +
                SensorEntry._ID + " INTEGER PRIMARY KEY," +
                SensorEntry.COLUMN_LOCATION + " TEXT NOT NULL, " +
                SensorEntry.COLUMN_INSTALLDATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                SensorEntry.COLUMN_SENSORTYPE + " REAL NOT NULL, " +
                SensorEntry.COLUMN_METRIC + " REAL NOT NULL, " +
                SensorEntry.COLUMN_CALIBRATED + " REAL NOT NULL, " +
                SensorEntry.COLUMN_CAL_A + " REAL NOT NULL, " +
                SensorEntry.COLUMN_CAL_B + " REAL NOT NULL " +
                " );";
        /*
        CREATE TABLE temperature(
            _id INTEGER PRIMARY KEY autoincrement,
            sensorid integer NOT NULL,
            created DATETIME DEFAULT CURRENT_TIMESTAMP,
            value FLOAT NOT NULL,
            metric INTEGER DEFAULT 1 NOT NULL,
            calibrated INTEGER DEFAULT 0 NOT NULL,
            FOREIGN KEY(sensorid) REFERENCES sensor(_id)
        );
        */
        final String SQL_CREATE_TEMPERATURE_TABLE = "CREATE TABLE " + TemperatureEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                TemperatureEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this weather data
                TemperatureEntry.COLUMN_SENSORID + " INTEGER NOT NULL, " +
                TemperatureEntry.COLUMN_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                TemperatureEntry.COLUMN_VALUE + " FLOAT NOT NULL, " +
                TemperatureEntry.COLUMN_METRIC + " INTEGER DEFAULT 1 NOT NULL," +
                TemperatureEntry.COLUMN_CALIBRATED + " INTEGER DEFAULT 0 NOT NULL, " +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + TemperatureEntry.COLUMN_SENSORID + ") REFERENCES " +
                SensorEntry.TABLE_NAME + " (" + SensorEntry._ID + ")" +
                ");";
        /*
        CREATE TABLE calibration(
            _id INTEGER PRIMARY KEY autoincrement,
            sensorid integer NOT NULL,
            created DATETIME DEFAULT CURRENT_TIMESTAMP,
            cal_a_old FLOAT NOT NULL,
            cal_b_old FLOAT NOT NULL,
            cal_a_new FLOAT NOT NULL,
            cal_b_new FLOAT NOT NULL,
            ref_value_high FLOAT NOT NULL,
            ref_value_low FLOAT NOT NULL,
            read_value_high FLOAT NOT NULL,
            read_value_low FLOAT NOT NULL,
            FOREIGN KEY(sensorid) REFERENCES sensor(_id)
        );
         */
        final String SQL_CREATE_CALIBRATION_TABLE = "CREATE TABLE " + CalibrationEntry.TABLE_NAME + " (" +
                CalibrationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                // the ID of the location entry associated with this weather data
                CalibrationEntry.COLUMN_SENSORID + " INTEGER NOT NULL, " +
                CalibrationEntry.COLUMN_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                CalibrationEntry.COLUMN_CAL_A_OLD + " FLOAT NOT NULL, " +
                CalibrationEntry.COLUMN_CAL_B_OLD + " FLOAT NOT NULL, " +
                CalibrationEntry.COLUMN_CAL_A_NEW + " FLOAT NOT NULL, " +
                CalibrationEntry.COLUMN_CAL_B_NEW + " FLOAT NOT NULL, " +
                CalibrationEntry.COLUMN_REF_VALUE_HIGH + " FLOAT NOT NULL, " +
                CalibrationEntry.COLUMN_REF_VALUE_LOW + " FLOAT NOT NULL, " +
                CalibrationEntry.COLUMN_READ_VALUE_HIGH + " FLOAT NOT NULL, " +
                CalibrationEntry.COLUMN_READ_VALUE_LOW + " FLOAT NOT NULL, " +
                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + CalibrationEntry.COLUMN_SENSORID + ") REFERENCES " +
                SensorEntry.TABLE_NAME + " (" + SensorEntry._ID + ")" +
                ");";

        sqLiteDatabase.execSQL(SQL_CREATE_SENSOR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TEMPERATURE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_CALIBRATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SensorEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TemperatureEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CalibrationEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
