package com.example.masszzsapp;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class PointsWorker extends Worker {
    public PointsWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        String userId = getInputData().getString("userId");
        if (userId != null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .update("loyaltyPoints", FieldValue.increment(1));
        }
        return Result.success();
    }
}