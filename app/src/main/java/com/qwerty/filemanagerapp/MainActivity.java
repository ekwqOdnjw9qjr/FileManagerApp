package com.qwerty.filemanagerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnCreateNote;
    private MaterialButton btnSelectFile;
    private MaterialButton btnBack;

    // Лаунчер для выбора файла
    private final ActivityResultLauncher<String> openDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String content = readFileContent(uri);
                    if (content != null) {
                        // Переходим на страницу отображения содержимого файла
                        Intent intent = new Intent(MainActivity.this, DisplayFileContentActivity.class);
                        intent.putExtra("fileContent", content); // Передаем содержимое файла
                        startActivity(intent);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCreateNote = findViewById(R.id.btnCreateNote);
        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnBack = findViewById(R.id.btnBack);

        // Переход на страницу создания записи
        btnCreateNote.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            startActivity(intent);
        });

        // Кнопка для выбора файла для чтения
        btnSelectFile.setOnClickListener(v -> openDocumentLauncher.launch("text/plain"));

        // Обработка кнопки "Назад" в интерфейсе
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private String readFileContent(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();
                inputStream.close();
                return content.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка чтения файла", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
