package com.example.satto.auth;

import com.example.satto.domain.users.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  private String profileImg;
  private String name;
  private String nickname;
  private String department;
  private int studentId;
  private String email;
  private String password;
  private Byte isPublic;
  private Role role;
}
