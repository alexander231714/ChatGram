package com.loschidos.chatgram.models;

public class User {

    private String id, email, username, telefono;
    //va a contener la fecha en la que se creo el usuario
    private long timestamp;

    public User (){

    }

    public User(String id, String email, String username, String telefono, long timestamp) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.telefono = telefono;
        this.timestamp=timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
