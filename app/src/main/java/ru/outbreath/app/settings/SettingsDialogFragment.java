package ru.outbreath.app.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.outbreath.app.R;

public class SettingsDialogFragment extends DialogFragment {

    private NumberPicker npInhale;
    private NumberPicker npDelayInhale;
    private NumberPicker npExhale;
    private NumberPicker npDelayExhale;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog, null);
        npInhale = view.findViewById(R.id.np_inhale);
        npDelayInhale = view.findViewById(R.id.np_delay_inhale);
        npExhale = view.findViewById(R.id.np_exhale);
        npDelayExhale = view.findViewById(R.id.np_delay_exhale);

        npInhale.setMinValue(0);
        npInhale.setMaxValue(30);
        npDelayInhale.setMinValue(0);
        npDelayInhale.setMaxValue(30);
        npExhale.setMinValue(0);
        npExhale.setMaxValue(30);
        npDelayExhale.setMinValue(0);
        npDelayExhale.setMaxValue(30);

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        npInhale.setValue(sharedPreferences.getInt("inhale", 4));
        npDelayInhale.setValue(sharedPreferences.getInt("delay_inhale", 4));
        npExhale.setValue(sharedPreferences.getInt("exhale", 4));
        npDelayExhale.setValue(sharedPreferences.getInt("delay_exhale", 4));


        // TODO: найти NumberPicker и установить значения

        return new AlertDialog.Builder(requireContext())
                .setTitle("Настройка дыхательной тренировки")
                .setView(view)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("inhale", npInhale.getValue());
                    editor.putInt("delay_inhale", npDelayInhale.getValue());
                    editor.putInt("exhale", npExhale.getValue());
                    editor.putInt("delay_exhale", npDelayExhale.getValue());
                    editor.apply();
                })
                .setNegativeButton("Отмена", null)
                .create();
    }
}