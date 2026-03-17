package com.nulhart.controller;

import com.nulhart.services.AnimeService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/mal/callback")
@AllArgsConstructor
public class MalAuthController {
    public AnimeService animeService;
    @GetMapping
    public void callback(@RequestParam String code, HttpSession session){
       String verifier = (String) session.getAttribute("mal_verifier");
        animeService.exchangeCodeForToken(code,verifier,session);
    }
}
