package com.github.baby.owspace.view.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class SelectTextView extends TextView {
    public SelectTextView(Context context) {
        super(context);
    }

    public SelectTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int startSelection = getSelectionStart();
        int endSelection = getSelectionEnd();
        if (startSelection < 0 || endSelection < 0){
            Selection.setSelection((Spannable) getText(), getText().length());
        } else if (startSelection != endSelection) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                final CharSequence text = getText();
                setText(null);
                setText(text);
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
