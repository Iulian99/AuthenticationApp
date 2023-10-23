package com.example.biometricapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager {
    private static FirebaseAuthManager instance= null;//ensure that we have a single instance of FirebaseAuthManager class;
    private FirebaseAuth firebaseAuth;//authentification operations in Firebase

    private FirebaseAuthManager(){
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuthManager getInstance(){
        if(instance == null){
            instance = new FirebaseAuthManager();
        }
        return instance;
    }

    public FirebaseUser getCurrentUser(){//verify if the user is login
        return firebaseAuth.getCurrentUser();
    }

}
