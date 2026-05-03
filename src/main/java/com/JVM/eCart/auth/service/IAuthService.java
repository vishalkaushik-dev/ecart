package com.JVM.eCart.auth.service;

import com.JVM.eCart.auth.dto.CustomerRegisterRequest;
import com.JVM.eCart.auth.dto.LoginResponse;
import com.JVM.eCart.auth.dto.ResetPasswordRequest;
import com.JVM.eCart.auth.dto.SellerRegisterRequest;

public interface IAuthService {

    String registerCustomer(CustomerRegisterRequest customerRegisterRequest);

    String activateAccount(String token);

    String resendActivationLink(String email);

    String registerSeller(SellerRegisterRequest sellerRegisterRequest);

    LoginResponse login(String email, String password);

    String logout(String token);

     String forgotPassword(String email);

     String resetPassword(ResetPasswordRequest resetPasswordRequest, String token);

     LoginResponse refreshToken(String refreshToken);
}
