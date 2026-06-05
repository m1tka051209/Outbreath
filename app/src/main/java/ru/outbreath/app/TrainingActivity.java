package ru.outbreath.app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import ru.outbreath.app.databinding.ActivityTrainingBinding;
import ru.outbreath.app.loader.Timer;
import ru.outbreath.app.loader.WaveView;

public class TrainingActivity extends AppCompatActivity {

    private ActivityTrainingBinding binding;
    private Timer timer;
    private WaveView waveView;
    private TextView tvPhase;
    private int inhale;
    private int exhale;
    private int delayI;
    private int delayE;
    private long trainingStartTime;
    private long totalTrainingDuration;
    private long totalPausedDuration = 0;
    private long pauseStartTime = 0;
    private int[] phaseDurations;
    private final String[] phaseNames = {"Вдох", "Задержка-вдох", "Выдох", "Задержка-выдох"};
    private int currentPhaseIndex = 0;
    private float minWaterLevel;
    private float maxWaterLevel;
    private final float minAmplitude = 50f;
    private final float maxAmplitude = 200f;
    private boolean isDialogShowing = false;
    private boolean isResumedByButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrainingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.main.setOnClickListener(v -> showPauseDialog());
        waveView = binding.waveView;
        tvPhase = binding.tvPhase;

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        inhale = prefs.getInt("inhale", 4) * 1000;
        exhale = prefs.getInt("exhale", 4) * 1000;
        delayI = prefs.getInt("delay_inhale", 4) * 1000;
        delayE = prefs.getInt("delay_exhale", 4) * 1000;

        phaseDurations = new int[]{inhale, delayI, exhale, delayE};
        binding.pbTraining.setMax(100);

        waveView.setAlpha(0f);
        waveView.post(() -> {
            int height = waveView.getHeight();
            minWaterLevel = height;
            maxWaterLevel = 0f;

            waveView.setWaterLevel(minWaterLevel);


            Timer.OnPhaseListener listener = () -> {
                currentPhaseIndex++;
                if (currentPhaseIndex < phaseDurations.length) {
                    startPhase(currentPhaseIndex);
                    timer.startPhaseSmooth(phaseDurations[currentPhaseIndex]);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(TrainingActivity.this, "Тренировка завершена", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            };


            timer = new Timer(listener);


            timer.setOnTickListener(ms -> {
                long now = System.currentTimeMillis();
                long elapsed = now - trainingStartTime - totalPausedDuration;
                int progressPercent = (int) (elapsed * 100 / totalTrainingDuration);
                if (progressPercent > 100) progressPercent = 100;
                binding.pbTraining.setProgress(progressPercent);

                long seconds = ms / 1000 + 1;
                runOnUiThread(() -> binding.tvSeconds.setText(String.valueOf(seconds)));
            });

            waveView.animate().alpha(1f).setDuration(400).start();

            trainingStartTime = System.currentTimeMillis();
            totalTrainingDuration = inhale + delayI + exhale + delayE;
            startPhase(0);
            timer.startPhaseSmooth(phaseDurations[0]);
        });
    }

    public void startPhase(int index) {
        switch (phaseNames[index]) {
            case "Вдох":
                waveView.animateAmplitude(maxAmplitude, phaseDurations[0]);
                waveView.animateWaterLevel(maxWaterLevel, phaseDurations[0]);
                tvPhase.setText("Вдох");
                break;
            case "Задержка-вдох":
                waveView.animateAmplitude(maxAmplitude, phaseDurations[1]);
                waveView.animateWaterLevel(maxWaterLevel, phaseDurations[1]);
                tvPhase.setText("Задержка");
                break;
            case "Выдох":
                waveView.animateAmplitude(minAmplitude, phaseDurations[2]);
                waveView.animateWaterLevel(minWaterLevel, phaseDurations[2]);
                tvPhase.setText("Выдох");
                break;
            case "Задержка-выдох":
                waveView.animateAmplitude(minAmplitude, phaseDurations[3]);
                waveView.animateWaterLevel(minWaterLevel, phaseDurations[3]);
                tvPhase.setText("Задержка");
                break;
        }
    }

    private void showPauseDialog() {
        if (isDialogShowing) return;
        isDialogShowing = true;
        pauseStartTime = System.currentTimeMillis();
        waveView.pauseAll();
        timer.pauseTimer();
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_pause_menu, null);
        dialog.setContentView(view);

        view.findViewById(R.id.btn_continue).setOnClickListener(v -> {
            totalPausedDuration += System.currentTimeMillis() - pauseStartTime;
            isResumedByButton = true;
            waveView.resumeAll();
            timer.resumeTimer();
            dialog.dismiss();
        });

        view.findViewById(R.id.btn_end).setOnClickListener(v -> {
            if (timer != null) {
                timer.stop();
                Toast.makeText(this, "Тренировка прервана", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
            finish();
        });

        dialog.setOnDismissListener(d -> {
            if (!isResumedByButton) {
                totalPausedDuration += System.currentTimeMillis() - pauseStartTime;
            }
            isResumedByButton = false;
            isDialogShowing = false;
            waveView.resumeAll();
            timer.resumeTimer();
        });

        dialog.show();
    }
}
