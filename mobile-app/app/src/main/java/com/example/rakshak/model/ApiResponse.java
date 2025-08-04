package com.example.rakshak.model;

public class ApiResponse {
    private boolean success;
    private String message;
    private User data;  // This can be generic Object if needed
    private int statusCode;

    // Default constructor (required for Gson)
    public ApiResponse() {}

    // Constructor for success cases
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Constructor with data
    public ApiResponse(boolean success, String message, User data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getData() {
        return data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    // Optional: Builder pattern for complex responses
    public static class Builder {
        private boolean success;
        private String message;
        private User data;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(User data) {
            this.data = data;
            return this;
        }

        public ApiResponse build() {
            return new ApiResponse(success, message, data);
        }
    }
}