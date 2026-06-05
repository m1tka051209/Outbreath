package ru.outbreath.app.loader;

import static java.lang.Math.PI;
import static java.lang.Math.sin;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import ru.outbreath.app.R;

public class WaveView extends View {
    private ValueAnimator waveAnim;
    private Paint paintPrimary;
    private Path pathPrimary;
    private Paint paintSecondary;
    private Path pathSecondary;
    private float amplitude = 100;
    private float phase;
    private float waterLevel;
    private float secondaryOffset = 0;
    private ValueAnimator waterLevelAnim;
    private ValueAnimator amplitudeAnim;
    private boolean isPaused = false;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public void setWaterLevel(float waterLevel) {
        this.waterLevel = waterLevel;
        this.secondaryOffset = 0;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        pathSecondary.reset();
        float yOffset = waterLevel + secondaryOffset;
        pathSecondary.moveTo(0, (float) (yOffset + amplitude * 0.3f * sin(0 + phase)));
        for (int x = 0; x < getWidth(); x++) {
            float fade = (float) Math.sin(x * Math.PI / getWidth());
            double y = yOffset + fade * amplitude * 0.3f * sin(x * 0.001 + phase);
            pathSecondary.lineTo(x, (float) y);
        }
        pathSecondary.lineTo(getWidth(), getHeight());
        pathSecondary.lineTo(0, getHeight());
        pathSecondary.close();
        canvas.drawPath(pathSecondary, paintSecondary);

        pathPrimary.reset();
        pathPrimary.moveTo(0, (float) (waterLevel + amplitude * sin(0 + phase)));
        for (int x = 0; x < getWidth(); x++) {
            float fade = (float) Math.sin(x * Math.PI / getWidth());
            double y = waterLevel + fade * amplitude * sin(x * 0.001 + phase);
            pathPrimary.lineTo(x, (float) y);
        }
        pathPrimary.lineTo(getWidth(), getHeight());
        pathPrimary.lineTo(0, getHeight());
        pathPrimary.close();
        canvas.drawPath(pathPrimary, paintPrimary);
    }

    private void init() {
        paintPrimary = new Paint();
        pathPrimary = new Path();
        paintPrimary.setAntiAlias(true);
        int primaryWaveColor = ContextCompat.getColor(getContext(), R.color.wave_primary);
        paintPrimary.setColor(primaryWaveColor);
        paintPrimary.setStyle(Paint.Style.FILL);

        paintSecondary = new Paint();
        pathSecondary = new Path();
        paintSecondary.setAntiAlias(true);
        int secondaryWaveColor = ContextCompat.getColor(getContext(), R.color.wave_secondary);
        paintSecondary.setColor(secondaryWaveColor);
        paintSecondary.setStyle(Paint.Style.FILL);
        startWaveAnimation();
    }

    private void startWaveAnimation() {
        waveAnim = ValueAnimator.ofFloat(0f, (float) (2 * PI));
        waveAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveAnim.setDuration(5000);
        waveAnim.addUpdateListener(valueAnimator -> {
            phase = (float) valueAnimator.getAnimatedValue();
            invalidate();
        });
        waveAnim.start();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        waterLevel = h / 2f;
    }

    public void animateAmplitude(float target, int duration) {
        if (isPaused) {
        } else {
            amplitudeAnim = ValueAnimator.ofFloat(amplitude, target);
            amplitudeAnim.setDuration(duration);
            amplitudeAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            amplitudeAnim.addUpdateListener(animListener -> {
                amplitude = (float) animListener.getAnimatedValue();
                invalidate();
            });
            amplitudeAnim.start();
        }
    }

    public void animateWaterLevel(float target, int duration) {
        if (isPaused) {
        } else {
            waterLevelAnim = ValueAnimator.ofFloat(waterLevel, target);
            waterLevelAnim.setDuration(duration);
            waterLevelAnim.setInterpolator(new AccelerateDecelerateInterpolator());
            waterLevelAnim.addUpdateListener(animListener -> {
                waterLevel = (float) animListener.getAnimatedValue();
                invalidate();
            });
            waterLevelAnim.start();

            ValueAnimator animOffset = ValueAnimator.ofFloat(secondaryOffset, 0);
            animOffset.setDuration(duration);
            animOffset.setInterpolator(new AccelerateDecelerateInterpolator());
            animOffset.addUpdateListener(animListener -> {
                secondaryOffset = (float) animListener.getAnimatedValue();
                invalidate();
            });
            animOffset.start();
        }
    }

    public void pauseAll() {
        if (isPaused) return;
        if (waveAnim != null && waveAnim.isRunning()) waveAnim.pause();
        if (waterLevelAnim != null && waterLevelAnim.isRunning()) waterLevelAnim.pause();
        if (amplitudeAnim != null && amplitudeAnim.isRunning()) amplitudeAnim.pause();
        isPaused = true;
    }

    public void resumeAll() {
        if (!isPaused) return;
        if (waveAnim != null && waveAnim.isPaused()) waveAnim.resume();
        if (waterLevelAnim != null && waterLevelAnim.isPaused()) waterLevelAnim.resume();
        if (amplitudeAnim != null && amplitudeAnim.isPaused()) amplitudeAnim.resume();
        isPaused = false;
    }
}
