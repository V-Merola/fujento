package com.fujentopj.fujento.module.users.adapter.in.rest.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RegisterUserRequestDTO {
    @NotBlank
    private  String email;
    @NotBlank
    private  String nickname;
    @NotBlank
    private  String password;

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "RegisterUserRequestDTO{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegisterUserRequestDTO)) return false;

        RegisterUserRequestDTO that = (RegisterUserRequestDTO) o;

        if (!email.equals(that.email)) return false;
        if (!nickname.equals(that.nickname)) return false;
        return password.equals(that.password);
    }
    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + nickname.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }

}
