package com.example.tasktracker;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.Set;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {
    private boolean isSelectionMode = false;
    private Set<Task> selectedTasks = new HashSet<>();
    private OnSelectionChangedListener selectionChangedListener;
    private OnTaskClickListener onTaskClickListener;


    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public TaskAdapter() {
        super(new TaskDiffCallback());
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_layout, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = getItem(position);
        holder.bind(task, isSelectionMode, selectedTasks.contains(task));

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleTaskSelection(task, holder);
            } else if (onTaskClickListener != null) {
                onTaskClickListener.onTaskClick(task);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                selectedTasks.clear();
                selectedTasks.add(task);
                notifyDataSetChanged();
                updateSelectionListener();
                return true;
            }
            return false;
        });
    }

    private void toggleTaskSelection(Task task, TaskViewHolder holder) {
        if (selectedTasks.contains(task)) {
            selectedTasks.remove(task);
        } else {
            selectedTasks.add(task);
        }
        notifyItemChanged(holder.getAdapterPosition());
        updateSelectionListener();

        if (selectedTasks.isEmpty()) {
            isSelectionMode = false;
            notifyDataSetChanged();
        }
    }

    private void updateSelectionListener() {
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(selectedTasks.size());
        }
    }

    public void exitSelectionMode() {
        isSelectionMode = false;
        selectedTasks.clear();
        notifyDataSetChanged();
    }

    public Set<Task> getSelectedTasks() {
        return selectedTasks;
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTaskName;
        private final Drawable defaultBackground;
//        private final View selectionOverlay;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTaskName = itemView.findViewById(R.id.text_view_task_name);
            defaultBackground = textViewTaskName.getBackground();
        }

        public void bind(Task task, boolean isSelectionMode, boolean isSelected) {
            textViewTaskName.setText(task.getName());

            if (isSelectionMode) {
                if(isSelected) {
                    textViewTaskName.setBackgroundColor(Color.LTGRAY);
                } else {
                    textViewTaskName.setBackground(defaultBackground);
                }
            } else {
                textViewTaskName.setBackground(defaultBackground);
            }
        }
    }

    static class TaskDiffCallback extends DiffUtil.ItemCallback<Task> {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.getUID().equals(newItem.getUID());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem);
        }
    }
}