package com.lqcode.adjump.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;
import com.lqcode.adjump.R;

import org.jetbrains.annotations.Nullable;

public class MyGuideActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance(
                "一步-轻松跳过开屏广告",
                "",
                R.mipmap.guide_one,
                Color.parseColor("#6289de")
        ));

        addSlide(AppIntroFragment.newInstance(
                "一步-免ROOT一键设置",
                "即可开启！",
                R.mipmap.big_icon,
                Color.parseColor("#d46700")
        ));

        setColorTransitionsEnabled(true);
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);

    }


    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }


    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
