package com.example.restaurant.dto;
public class TaskRequest {
    private Worker worker; // Objeto anidado
    private String type;
    private String title;
    private String description;
    private String dueTime;
    private String priority;

    // Getters y Setters (necesarios para Spring)
    public static class Worker {
        private String name;
        private String role;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }

    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueTime() {
        return dueTime;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
