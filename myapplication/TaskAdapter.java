package com.example.myapplication; // Sesuaikan dengan lokasi di gambar

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Impor R (resource) dari paket utama aplikasi
import com.example.myapplication.R;
// Impor kelas Task dari paket yang sama
import com.example.myapplication.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;
    private OnTaskInteractionListener listener;

    public interface OnTaskInteractionListener {
        void onTaskCompletedChanged(Task task, boolean isChecked);
        void onTaskDeleted(Task task);
        void onTaskClicked(Task task);
    }

    public TaskAdapter(List<Task> taskList, OnTaskInteractionListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvDueDate.setText("Jatuh Tempo: " + task.getDueDate());
        holder.checkBoxCompleted.setChecked(task.isCompleted());

        // Terapkan efek coret jika tugas selesai
        if (task.isCompleted()) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDescription.setPaintFlags(holder.tvDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvDueDate.setPaintFlags(holder.tvDueDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvDescription.setPaintFlags(holder.tvDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvDueDate.setPaintFlags(holder.tvDueDate.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskCompletedChanged(task, isChecked);
            }
        });

        holder.ivDeleteTask.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskDeleted(task);
            }
        });

        holder.taskItemLayout.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClicked(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.taskList = newTasks;
        notifyDataSetChanged();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvDueDate;
        CheckBox checkBoxCompleted;
        ImageView ivDeleteTask;
        LinearLayout taskItemLayout;

        TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvDescription = itemView.findViewById(R.id.tv_task_description);
            tvDueDate = itemView.findViewById(R.id.tv_task_due_date);
            checkBoxCompleted = itemView.findViewById(R.id.checkBoxCompleted);
            ivDeleteTask = itemView.findViewById(R.id.iv_delete_task);
            taskItemLayout = itemView.findViewById(R.id.task_item_layout);
        }
    }
}
