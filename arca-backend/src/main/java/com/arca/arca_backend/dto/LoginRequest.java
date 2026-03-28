package com.arca.arca_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    private String email;
    private String password;
    private String masterPassword;  // Master password for vault encryption
    private String supabaseUserId;  // Optional: link to Supabase auth user
}
