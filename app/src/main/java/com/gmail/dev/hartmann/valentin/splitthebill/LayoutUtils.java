package com.gmail.dev.hartmann.valentin.splitthebill;

import android.os.Build;
import android.view.ViewTreeObserver;
import android.widget.EditText;

public class LayoutUtils {
    // sets the width of a 7 digit EditText to that of 7 zeros by using an EditText with a width of
    // 2 zeros
    public static void setLongNumberEntryWidth(final EditText target, final EditText source) {
        target.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        target.setWidth((int) 3.5 * source.getWidth());
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            target.getViewTreeObserver()
                                    .removeGlobalOnLayoutListener(this);
                        } else {
                            target.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }
                    }
                }
        );
    }
}
