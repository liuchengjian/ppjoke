package com.liucj.ppjoke.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class PPEditTextView extends AppCompatEditText {
    private onBackKeyEvent keyEvent;

    public PPEditTextView(Context context) {
        super(context);
    }

    public PPEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PPEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (keyEvent != null) {
                if (keyEvent.onKeyEvent()) {
                    return true;
                }
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public void setOnBackKeyEventListener(onBackKeyEvent event) {
        this.keyEvent = event;
    }

    public interface onBackKeyEvent {
        boolean onKeyEvent();
    }
}
