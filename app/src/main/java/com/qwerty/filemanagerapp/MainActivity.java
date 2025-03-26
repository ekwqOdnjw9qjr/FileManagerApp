package com.qwerty.filemanagerapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.qwerty.filemanagerapp.databinding.ActivityMainBinding;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private boolean hasPermission = false;
    private ActivityResultLauncher<Intent> manageStorageLauncher;

    private final ActivityResultLauncher<String> openDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleFileSelection);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupManageStorageLauncher();
        setupButtonListeners();
        checkStoragePermission();
    }

    private void setupManageStorageLauncher() {
        manageStorageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        if (Environment.isExternalStorageManager()) {
                            showToast(R.string.permission_granted);
                            hasPermission = true;
                            updateButtonState();
                        } else {
                            showPermissionDialog(
                                    R.string.permission_not_granted_title,
                                    R.string.permission_not_granted_message
                            );
                        }
                    }
                }
        );
    }

    private void setupButtonListeners() {
        binding.btnCreateNote.setOnClickListener(v -> handleCreateNoteClick());
        binding.btnSelectFile.setOnClickListener(v -> handleSelectFileClick());
        binding.btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void handleCreateNoteClick() {
        if (hasPermission) {
            startActivity(new Intent(this, CreateNoteActivity.class));
        } else {
            showToast(R.string.permission_required_toast);
            showPermissionDialog(
                    R.string.permission_required_title,
                    R.string.permission_required_message
            );
        }
    }

    private void handleSelectFileClick() {
        if (hasPermission) {
            openDocumentLauncher.launch("*/*");
        } else {
            showToast(R.string.permission_required_toast);
            showPermissionDialog(
                    R.string.permission_required_title,
                    R.string.permission_required_message
            );
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                showPermissionDialog(
                        R.string.permission_required_title,
                        R.string.permission_required_message
                );
            } else {
                hasPermission = true;
                updateButtonState();
            }
        } else {
            hasPermission = true;
            updateButtonState();
        }
    }

    private void showPermissionDialog(int titleResId, int messageResId) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(R.string.button_yes, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    manageStorageLauncher.launch(intent);
                })
                .setNegativeButton(R.string.button_no, (dialog, which) -> {
                    showToast(R.string.permission_denied);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void updateButtonState() {
        binding.btnCreateNote.setEnabled(hasPermission);
        binding.btnSelectFile.setEnabled(hasPermission);
    }

    public static String readFileContent(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
            if (inputStream != null) {
                return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleFileSelection(Uri uri) {
        if (uri == null) return;

        String content = readFileContent(this,uri);
        if (content != null) {
            Intent intent = new Intent(this, DisplayFileContentActivity.class);
            intent.putExtra("fileContent", content);
            startActivity(intent);
        }
    }

    private void showToast(int messageResId) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}