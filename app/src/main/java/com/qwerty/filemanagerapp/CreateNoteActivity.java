package com.qwerty.filemanagerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.qwerty.filemanagerapp.databinding.ActivityCreateNoteBinding;

import java.io.OutputStream;

public class CreateNoteActivity extends AppCompatActivity {

    private ActivityCreateNoteBinding binding;

    private final ActivityResultLauncher<String> createDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"), uri -> {
                if (uri != null) {
                    saveFileContent(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etTextInput.setMovementMethod(new ScrollingMovementMethod());
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        binding.btnSaveText.setOnClickListener(v -> {
            if (!binding.etTextInput.getText().toString().isEmpty()) {
                createDocumentLauncher.launch("MyFile_" + System.currentTimeMillis() + ".txt");
            } else {
                Toast.makeText(this, R.string.input_text, Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void saveFileContent(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                outputStream.write(binding.etTextInput.getText().toString().getBytes());
                Toast.makeText(this, R.string.file_save, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                finish();
            }
        } catch (Exception e) {
            Toast.makeText(this, R.string.file_save_error, Toast.LENGTH_SHORT).show();
        }
    }
}
