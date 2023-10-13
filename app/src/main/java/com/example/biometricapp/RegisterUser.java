package com.example.biometricapp;

public interface RegisterUser {
    void login(String numberPhone);
    void logout();
    boolean isAuthenticated();

}
