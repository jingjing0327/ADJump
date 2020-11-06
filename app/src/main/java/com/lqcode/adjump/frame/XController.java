package com.lqcode.adjump.frame;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Room;

import com.lqcode.adjump.R;
import com.lqcode.adjump.entity.db.AppDatabase;

public class XController {

    private static XController controller = null;
    private Toast toast;
    private Handler mHandler;
    private AppDatabase db;
    private TextView toastTV;

    private XController() {

        View view = LayoutInflater.from(CacheTools.getInstance().getContext()).inflate(R.layout.toast_layout, null);
        toastTV = (TextView) view.findViewById(R.id.textView4);
        toast = new Toast(CacheTools.getInstance().getContext());
        toast.setView(view);

        mHandler = new Handler(Looper.getMainLooper());
        db = Room.databaseBuilder(
                CacheTools.getInstance().getContext(),
                AppDatabase.class,
                "ADJump")
                .allowMainThreadQueries()
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
            try {
                if (text != null) {
                    toastTV.setText(text);
                    toast.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public AppDatabase getDb() {
        return db;
    }
}
