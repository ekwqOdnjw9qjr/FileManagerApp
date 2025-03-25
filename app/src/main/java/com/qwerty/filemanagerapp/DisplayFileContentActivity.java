package com.qwerty.filemanagerapp;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class DisplayFileContentActivity extends AppCompatActivity {

    private TextView tvFileContent;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_file_content);

        tvFileContent = findViewById(R.id.tvFileContent);
        btnBack = findViewById(R.id.btnBack);

        tvFileContent.setMovementMethod(new ScrollingMovementMethod());

        // Получаем содержимое файла из Intent
        String fileContent = getIntent().getStringExtra("fileContent");
        if (fileContent != null) {
            tvFileContent.setText(fileContent);
        } else {
            tvFileContent.setText("Не удалось загрузить содержимое файла");
        }

        // Обработка кнопки "Назад"
        btnBack.setOnClickListener(v -> onBackPressed());
    }
}
