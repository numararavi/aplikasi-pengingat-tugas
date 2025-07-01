package com.example.myapplication; // Sesuai dengan lokasi di gambar

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Impor kelas dari paket yang sama
import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDescription, etDueDate;
    private Button btnSaveTask;
    private TextView tvHeader;
    private DatabaseHelper dbHelper;
    private Calendar calendar;

    private int taskId = -1; // -1 indicates new task, otherwise it's an existing task ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        tvHeader = findViewById(R.id.tv_add_task_title_header);
        etTitle = findViewById(R.id.et_task_title);
        etDescription = findViewById(R.id.et_task_description);
        etDueDate = findViewById(R.id.et_due_date);
        btnSaveTask = findViewById(R.id.btn_save_task);

        // Check if we are editing an existing task
        if (getIntent().hasExtra("task_id")) {
            taskId = getIntent().getIntExtra("task_id", -1);
            if (taskId != -1) {
                tvHeader.setText("Edit Tugas");
                btnSaveTask.setText("Update Tugas");
                loadTaskData(taskId);
            }
        }

        etDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateDueDateLabel();
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void updateDueDateLabel() {
        String myFormat = "dd/MM/yyyy"; // Date format
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDueDate.setText(sdf.format(calendar.getTime()));
    }

    private void loadTaskData(int id) {
        Task task = dbHelper.getTask(id);
        if (task != null) {
            etTitle.setText(task.getTitle());
            etDescription.setText(task.getDescription());
            etDueDate.setText(task.getDueDate());
            // You might want to parse the due date back into the calendar for the date picker
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                calendar.setTime(sdf.parse(task.getDueDate()));
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String dueDate = etDueDate.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Judul tugas wajib diisi!");
            return;
        }
        if (TextUtils.isEmpty(dueDate)) {
            etDueDate.setError("Tanggal jatuh tempo wajib diisi!");
            return;
        }

        if (taskId == -1) {
            // Add new task
            Task newTask = new Task(title, description, dueDate, false);
            long id = dbHelper.addTask(newTask);
            if (id > 0) {
                Toast.makeText(this, "Tugas berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                finish(); // Close activity
            } else {
                Toast.makeText(this, "Gagal menambahkan tugas.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update existing task
            Task existingTask = dbHelper.getTask(taskId); // Get existing status
            if (existingTask != null) {
                existingTask.setTitle(title);
                existingTask.setDescription(description);
                existingTask.setDueDate(dueDate);
                int rowsAffected = dbHelper.updateTask(existingTask);
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Tugas berhasil diupdate!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Gagal mengupdate tugas.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Tugas tidak ditemukan.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
