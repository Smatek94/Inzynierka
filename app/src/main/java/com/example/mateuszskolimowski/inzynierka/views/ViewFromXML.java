package com.example.mateuszskolimowski.inzynierka.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.mateuszskolimowski.inzynierka.R;

/**
 * Created by Mateusz Skolimowski on 23.04.2017.
 */
public class ViewFromXML extends FrameLayout{

    public ViewFromXML(Context context, int layoutId) {
        super(context);
        initLayout(context,layoutId);
    }

    private void initLayout(Context context, int layoutId) {
        View view = inflate(context, layoutId,null);
        addView(view);
    }
}
