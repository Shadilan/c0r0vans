package com.coe.c0r0vans.UIElements.MessageLayout;

import android.content.Context;
import android.util.AttributeSet;

import utility.GATracker;

/**
 * EssageLine for Main View
 */

public class EssageLineView extends EssageLine {
    public EssageLineView(Context context) {
        super(context);
    }

    public EssageLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EssageLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void clickClose() {
        try {
            hide();
        } catch (Exception e) {
            GATracker.trackException("RemoveMessage", "AfterInit");
        }
    }
}
