package com.example.SpringOauth2Demo.dto;

public record LoginRequest(
        String email,
        String password
) {
}