package com.example.mateuszskolimowski.inzynierka.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.example.mateuszskolimowski.inzynierka.R;


/**
 * Created by Mateusz Skolimowski on 12.01.2017.
 */

public class Animations {

    private static void animation(View v, Context ctx, int animationId, Animation.AnimationListener animationListener){
        Animation a = AnimationUtils.loadAnimation(ctx, animationId);
        if(animationListener != null)
            a.setAnimationListener(animationListener);
        if (a != null) {
            a.reset();
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void rotateRight(View v,Context ctx) {
        animation(v,ctx,R.animator.rotate_right,null);
    }

    public static void rotateRightFrom180(View v,Context ctx) {
        animation(v,ctx,R.animator.rotate_right_from_180,null);
    }

    public static void expand(final View v, int speed) {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? RelativeLayout.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density)*speed);
        Utils.debugLog("duration : " + a.getDuration());
        a.setFillAfter(true);
        v.startAnimation(a);
    }

    public static void collapse(final View v, int speed) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density)*speed);
        v.startAnimation(a);
    }
}
