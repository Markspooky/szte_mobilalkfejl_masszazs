package com.example.masszzsapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {
    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationHelper.showNotification(getApplicationContext(), 
            "Emlékeztető", "Hamarosan masszázs időpontod lesz!");
        return Result.success();
    }
}