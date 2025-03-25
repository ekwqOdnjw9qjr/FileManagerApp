package com.qwerty.filemanagerapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.io.OutputStream;

public class CreateNoteActivity extends AppCompatActivity {

    private TextInputEditText etTextInput;
    private MaterialButton btnSaveText;
    private MaterialButton btnBack;
    private boolean hasUnsavedChanges = false;

    // Лаунчер для создания файла
    private final ActivityResultLauncher<String> createDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.CreateDocument("text/plain"), uri -> {
                if (uri != null) {
                    saveFileContent(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        // Инициализация
        etTextInput = findViewById(R.id.etTextInput);
        btnSaveText = findViewById(R.id.btnSaveText);
        btnBack = findViewById(R.id.btnBack);

        etTextInput.setMovementMethod(new ScrollingMovementMethod());

        // Отслеживаем изменения в текстовом поле с помощью лямбда-выражения
        etTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                hasUnsavedChanges = !s.toString().isEmpty();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


        // Сохранение текста в файл
        btnSaveText.setOnClickListener(v -> {
            String text = etTextInput.getText().toString();
            if (!text.isEmpty()) {
                createDocumentLauncher.launch("MyFile_" + System.currentTimeMillis() + ".txt");
            } else {
                Toast.makeText(this, "Введите текст для сохранения", Toast.LENGTH_SHORT).show();
            }
        });

        // Обработка кнопки "Назад" в интерфейсе
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void saveFileContent(Uri uri) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                outputStream.write(etTextInput.getText().toString().getBytes());
                hasUnsavedChanges = false;
                etTextInput.setText("");
                Toast.makeText(this, "Файл сохранён", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(CreateNoteActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка сохранения файла", Toast.LENGTH_SHORT).show();
        }
    }
}
