package com.example.satto.auth;

import com.example.satto.global.common.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register") // 회원가입 후 토큰 발급
  public BaseResponse<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return BaseResponse.onSuccess(service.register(request));
  }

  @PostMapping("/authenticate")  // 로그인시 토큰 재 발급
  public BaseResponse<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return BaseResponse.onSuccess(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
