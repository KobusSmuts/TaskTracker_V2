package com.example.tasktracker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthManager {

    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // Get the current user
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Check if the user is authenticated
    public static boolean isUserAuthenticated() {
        return getCurrentUser() != null;
    }

    // Sign out the user
    public static void signOut() {
        mAuth.signOut();
    }

    // You can add more helper methods here as needed (e.g., for managing user state)
}
