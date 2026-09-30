package com.example.SpringOauth2Demo.dto;

public record RegisterRequest(
        String name,
        String email,
        String password,
        String phone
) {
}