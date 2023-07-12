package com.antplay.binding.input.capture;

import android.view.MotionEvent;

public abstract class InputCaptureProvider {
    protected boolean isCapturing;

    public void enableCapture() {
        isCapturing = true;
    }
    public void disableCapture() {
        isCapturing = false;
    }

    public void destroy() {}

    public boolean isCapturingEnabled() {
        return isCapturing;
    }

    public boolean isCapturingActive() {
        return isCapturing;
    }

    public boolean eventHasRelativeMouseAxes(MotionEvent event) {
        return false;
    }

    public float getRelativeAxisX(MotionEvent event) {
        return 0;
    }

    public float getRelativeAxisY(MotionEvent event) {
        return 0;
    }

    public void onWindowFocusChanged(boolean focusActive) {}
}
