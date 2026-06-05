package ru.outbreath.app.loader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.CountDownTimer;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Timer {
    private int phaseDuration;
    private OnPhaseListener listener;
    private ValueAnimator animator;
    private OnTickListener tickListener;
    private boolean isTimerPaused = false;

    public Timer(OnPhaseListener listener) {
        this.listener = listener;
    }

    public int getPhaseDuration() {
        return phaseDuration;
    }

    public void setPhaseDuration(int phaseDuration) {
        this.phaseDuration = phaseDuration;
    }

    public interface OnPhaseListener {
        void onPhaseEnd();
    }

    public interface OnTickListener {
        void onTick(long msUntilFinished);
    }

    public boolean isTimerPaused() {
        return isTimerPaused;
    }

    public void setOnTickListener(OnTickListener listener) {
        this.tickListener = listener;
    }

    public void startPhaseSmooth(int phaseDuration) {
        animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(phaseDuration);
        animator.addUpdateListener(animation -> {
            if (tickListener != null) {
                long remaining = phaseDuration - animation.getCurrentPlayTime();
                tickListener.onTick(remaining);
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                listener.onPhaseEnd();
            }
        });
        animator.start();

//        CountDownTimer countDownTimer = new CountDownTimer() {
//            @Override
//            public void onFinish() {
//
//            }
//
//            @Override
//            public void onTick(long l) {
//
//            }
//        };
    }

    public void stop() {
        animator.cancel();
    }

    public void pauseTimer() {
        if (isTimerPaused) return;
        if (animator != null && animator.isRunning()) {
            animator.pause();
            isTimerPaused = true;
        }
    }

    public void resumeTimer() {
        if (!isTimerPaused) return;
        if (animator != null && animator.isPaused()) {
            animator.resume();
            isTimerPaused = false;
        }
    }

}
