package com.mykart.project.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private Long Id;
    private String username;
    private List<String> roles;
    private String jwtToken;
}


