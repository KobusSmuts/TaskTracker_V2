package com.example.tasktracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Adapter for displaying a list of tasks in the RecyclerView.
 */
public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    private OnTaskClickListener onTaskClickListener; // Listener for task clicks

    public TaskAdapter() {
        super(new TaskDiffCallback());
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_task_view, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);
        holder.bind(task);
    }

    /**
     * Sets a listener to handle task clicks.
     */
    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    /**
     * ViewHolder for task items.
     */
    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTaskName;
        private Spinner spnViewTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTaskName = itemView.findViewById(R.id.text_view_task_name);
            spnViewTaskStatus = itemView.findViewById(R.id.spinner_task_status);

            // Handle item click
            itemView.setOnClickListener(v -> {
                if (onTaskClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onTaskClickListener.onTaskClick(getItem(position));
                    }
                }
            });
        }

        public void bind(Task task) {
            textViewTaskName.setText(task.getName());
            spnViewTaskStatus.setText(task.getStatus());
        }
    }

    /**
     * Callback for calculating the diff between two tasks.
     */
    static class TaskDiffCallback extends DiffUtil.ItemCallback<Task> {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getUID().equals(newItem.getUID()); // Compare by uID
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem); // Compare content equality
        }
    }

    /**
     * Interface for task click events.
     */
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }
}
