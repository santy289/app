package com.rootnetapp.rootnetintranet.ui.createworkflow.adapters;

import android.view.MotionEvent;
import android.view.View;

/**
 * This class is used to prevent the spinners, buttons or any other view from losing focus to a
 * TextInputEditText. Detects whether the touch corresponds to a click rather than any movement
 * event.
 */
public class OnTouchClickListener implements View.OnTouchListener {

    private final int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;
    private final View viewToFocus;

    public OnTouchClickListener(View viewToFocus) {
        this.viewToFocus = viewToFocus;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //we need to detect whether the user has clicked or dragged before calling performClick()
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (isClick(startX, endX, startY, endY)) {
                    //the user has clicked
                    v.performClick();
                    viewToFocus.requestFocus();
                }
                break;
        }
        return true;
    }

    private boolean isClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
    }
}