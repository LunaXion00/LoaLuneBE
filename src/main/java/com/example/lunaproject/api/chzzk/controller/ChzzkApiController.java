package com.example.lunaproject.api.chzzk.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chzzk/auth")
public class ChzzkApiController {
    private static final String AUTH_URL = "https://chzzk.naver.com/account-interlock";

}
