package com.example.tasktracker;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private int selectedPosition = RecyclerView.NO_POSITION; // To track selected position

    protected TaskAdapter(@NonNull DiffUtil.ItemCallback<Task> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = getItem(position);
        holder.textViewTaskName.setText(currentTask.getName());
        holder.textViewTaskStatus.setText(currentTask.getStatus());

        // Highlight selected item
        holder.itemView.setBackgroundColor(selectedPosition == position ? 0xFFCCCCCC : 0xFFFFFFFF); // Change colors as needed

        holder.itemView.setOnClickListener(v -> {
            notifyItemChanged(selectedPosition); // Reset previously selected item
            selectedPosition = holder.getAdapterPosition(); // Update selected position
            notifyItemChanged(selectedPosition); // Notify to highlight selected item
        });
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTaskName;
        private final TextView textViewTaskStatus;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTaskName = itemView.findViewById(R.id.text_view_task_name);
            textViewTaskStatus = itemView.findViewById(R.id.text_view_task_status);
        }
    }

    public Task getTaskAtPosition(int position) {
        return getItem(position);
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public static class TaskDiff extends DiffUtil.ItemCallback<Task> {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem);
        }
    }
}
