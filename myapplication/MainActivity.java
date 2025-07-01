package com.example.myapplication; // Sesuai dengan lokasi di gambar

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
// Impor kelas dari paket yang sama
import com.example.myapplication.TaskAdapter;
import com.example.myapplication.DatabaseHelper;
import com.example.myapplication.Task;

import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskInteractionListener {

    private RecyclerView recyclerViewTasks;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(this);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        fabAddTask = findViewById(R.id.fab_add_task);

        loadTasks();

        fabAddTask.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh tasks when returning to MainActivity
        loadTasks();
    }

    private void loadTasks() {
        taskList = dbHelper.getAllTasks();
        if (taskAdapter == null) {
            taskAdapter = new TaskAdapter(taskList, this);
            recyclerViewTasks.setAdapter(taskAdapter);
        } else {
            taskAdapter.updateTasks(taskList);
        }
    }

    @Override
    public void onTaskCompletedChanged(Task task, boolean isChecked) {
        task.setCompleted(isChecked);
        int rowsAffected = dbHelper.updateTask(task);
        if (rowsAffected > 0) {
            // Tidak perlu memuat ulang semua, cukup perbarui tampilan item tunggal
            taskAdapter.notifyItemChanged(taskList.indexOf(task));
            Toast.makeText(this, "Status tugas diperbarui!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gagal memperbarui status tugas.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTaskDeleted(Task task) {
        // Tampilkan dialog konfirmasi sebelum menghapus
        new AlertDialog.Builder(this)
                .setTitle("Hapus Tugas")
                .setMessage("Apakah Anda yakin ingin menghapus tugas '" + task.getTitle() + "'?")
                .setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbHelper.deleteTask(task);
                        taskList.remove(task); // Hapus dari daftar saat ini
                        taskAdapter.notifyDataSetChanged(); // Beri tahu adapter untuk menyegarkan tampilan
                        Toast.makeText(MainActivity.this, "Tugas berhasil dihapus!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    @Override
    public void onTaskClicked(Task task) {
        // Buka AddTaskActivity untuk mengedit tugas
        Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }
}
