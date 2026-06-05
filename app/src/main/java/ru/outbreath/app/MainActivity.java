package ru.outbreath.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ru.outbreath.app.databinding.ActivityMainBinding;
import ru.outbreath.app.settings.SettingsDialogFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        super.onCreate(savedInstanceState);

        setContentView(binding.getRoot());

        binding.btnToProfile.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileActivity.class)));

        binding.btnStart.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, TrainingActivity.class)));

        binding.btnSettings.setOnClickListener(v -> new SettingsDialogFragment().show(getSupportFragmentManager(), "settings"));


    }
}