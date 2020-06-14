package com.example.testtask.models;

import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.testtask.presenters.IResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FetchWentSteps {
    private static final String TAG = "FetchWentSteps";

    private FitnessOptions mFitnessOptions;
    private Context mContext;
    private IResult mIresult;

    public IResult registerListener(IResult result) {
        mIresult = result;
        return mIresult;
    }

    public FetchWentSteps(Context context) {
        mContext = context;
    }

    public FitnessOptions fitnessOptions() {
        if (mFitnessOptions == null) {
            mFitnessOptions = FitnessOptions.builder()
                    .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                    // .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                    .build();
        }
        return mFitnessOptions;
    }

    public void findSensors() {
        DataSourcesRequest dataSourcesRequest = new DataSourcesRequest.Builder().
                setDataTypes(DataType.TYPE_STEP_COUNT_DELTA).
                setDataTypes(DataType.TYPE_DISTANCE_DELTA).
                setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE).
                setDataSourceTypes(DataSource.TYPE_RAW, DataSource.TYPE_DERIVED).build();

        Fitness.getSensorsClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext)).
                findDataSources(dataSourcesRequest).addOnSuccessListener(new OnSuccessListener<List<DataSource>>() {
            @Override
            public void onSuccess(List<DataSource> dataSources) {
                for (DataSource dataSource : dataSources) {
                    if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE)) {
                        Log.d(TAG, "@findSensors: sensors found");
                        countSteps(dataSource, dataSource.getDataType());
                        Fitness.getRecordingClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext)).subscribe(dataSource);
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "findSensors: onFailure: ", e);
            }
        });


    }

    private void countSteps(DataSource dataSource, DataType dataType) {
        Fitness.getSensorsClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext)).
                add(new SensorRequest.Builder().
                                setDataSource(dataSource).
                                setDataType(dataType).setSamplingRate(3, TimeUnit.SECONDS).build()
                        , new OnDataPointListener() {
                            @Override
                            public void onDataPoint(DataPoint dataPoint) {
                                for (Field field : dataPoint.getDataSource().getDataType().getFields()) {
                                    Value value = dataPoint.getValue(field);
                                    Log.d(TAG, "went steps: " + value);
                                    fetchWeeklySteps();
                                    mIresult.result(value.asInt());
                                }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "@countSteps: onSuccess ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "@countSteps: onFailure: ", e);
            }
        });

    }

    public void fetchWeeklySteps() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        Fitness.getHistoryClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext)).
                readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA).addOnSuccessListener(
                new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        for (Field field : dataSet.getDataSource().getDataType().getFields()) {
                            for (DataPoint dataPoint : dataSet.getDataPoints()) {
                                Log.d(TAG, "onSuccess daily : " + dataPoint.getValue(field));
                            }

                        }

                    }
                }
        );

        Fitness.getHistoryClient(mContext, GoogleSignIn.getLastSignedInAccount(mContext)).
                readData(new DataReadRequest.Builder().
                        setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS).
                        bucketByTime(1, TimeUnit.DAYS).
                        aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA).
                        build()).addOnSuccessListener(
                new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        if (dataReadResponse.getBuckets().size() > 0) {
                            for (Bucket bucket : dataReadResponse.getBuckets()) {
                                List<DataSet> dataSets = bucket.getDataSets();
                                for (DataSet dataset : dataSets) {
                                    for (DataPoint dataPoint : dataset.getDataPoints()) {
                                        resultWeekly(dataPoint);
                                    }
                                }
                            }
                        } else {
                            for (DataSet dataset : dataReadResponse.getDataSets()) {
                                for (DataPoint dataPoint : dataset.getDataPoints()) {
                                    resultWeekly(dataPoint);
                                }
                            }
                        }
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: ", e);
            }
        });

    }


    public void resultWeekly(DataPoint dp) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        for (Field field: dp.getDataSource().getDataType().getFields()) {
            Value value = dp.getValue(field);
            Log.d(TAG, "resultWeekly - date: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " steps: " + value.asInt() );
        }

    }

}
