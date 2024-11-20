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

public class UserAdapter extends ListAdapter<User, UserAdapter.UserViewHolder> {
    private boolean isSelectionMode = false;
    private Set<User> selectedUsers = new HashSet<>();
    private OnSelectionChangedListener selectionChangedListener;
    private OnUserClickListener onUserClickListener;

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UserAdapter() {
        super(new UserDiffCallback());
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.onUserClickListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = getItem(position);
        holder.bind(user, isSelectionMode, selectedUsers.contains(user));

        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleUserSelection(user, holder);
            } else if (onUserClickListener != null) {
                onUserClickListener.onUserClick(user);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                selectedUsers.clear();
                selectedUsers.add(user);
                notifyDataSetChanged();
                updateSelectionListener();
                return true;
            }
            return false;
        });

        holder.itemView.setBackgroundColor(
                isSelectionMode && selectedUsers.contains(user)
                        ? Color.LTGRAY
                        : Color.TRANSPARENT
        );
    }

    private void toggleUserSelection(User user, UserViewHolder holder) {
        if (selectedUsers.contains(user)) {
            selectedUsers.remove(user);
        } else {
            selectedUsers.add(user);
        }
        notifyItemChanged(holder.getAdapterPosition());
        updateSelectionListener();

        if (selectedUsers.isEmpty()) {
            isSelectionMode = false;
            notifyDataSetChanged();
        }
    }

    private void updateSelectionListener() {
        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(selectedUsers.size());
        }
    }

    public void exitSelectionMode() {
        isSelectionMode = false;
        selectedUsers.clear();
        notifyDataSetChanged();
    }

    public Set<User> getSelectedUsers() {
        return selectedUsers;
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewUserEmail;
        private final Drawable defaultBackground;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserEmail = itemView.findViewById(R.id.text_view_user_email);
            defaultBackground = textViewUserEmail.getBackground();
        }

        public void bind(User user, boolean isSelectionMode, boolean isSelected) {
            textViewUserEmail.setText(user.getUserEmail());

            if (isSelectionMode) {
                if (isSelected) {
                    textViewUserEmail.setBackgroundColor(Color.LTGRAY);
                } else {
                    textViewUserEmail.setBackground(defaultBackground);
                }
            } else {
                textViewUserEmail.setBackground(defaultBackground);
            }
        }
    }

    static class UserDiffCallback extends DiffUtil.ItemCallback<User> {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getUserEmail().equals(newItem.getUserEmail());
        }

        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.equals(newItem);
        }
    }
}
