package com.lqcode.adjump.frame;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.room.Room;

import com.lqcode.adjump.entity.db.AppDatabase;

public class XController {

    private static XController controller = null;
    private Toast toast;
    private Handler mHandler;
    private AppDatabase db;

    private XController() {
        toast = Toast.makeText(CacheTools.getInstance().getContext(), "", Toast.LENGTH_SHORT);
        mHandler = new Handler(Looper.getMainLooper());
        db = Room.databaseBuilder(
                CacheTools.getInstance().getContext(),
                AppDatabase.class,
                "ADJump")
                .fallbackToDestructiveMigration()
                .build();
    }

    public static XController getInstance() {
        if (controller == null)
            return controller = new XController();
        return controller;
    }

    /**
     * @param text
     */
    public void toastShow(String text) {
        mHandler.post(() -> {
            if (text != null) {
                toast.setText(text);
                toast.show();
            }
        });
    }

    public AppDatabase getDb() {
        return db;
    }
}
