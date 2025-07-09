package cn.master.luna.controller;

import cn.master.luna.handler.ResultHandler;
import cn.master.luna.service.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * @author Created by 11's papa on 2025/7/2
 */
@Tag(name = "登录")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserLoginService userLoginService;

    public AuthController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PreAuthorize("hasAuthority('project_admin')")
    @GetMapping("/info")
    public String hello(Authentication authentication) {
        return "Hello, " + authentication.getName() + "!";
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public ResultHandler login(@RequestBody LoginRequest loginRequest) {
        return userLoginService.login(loginRequest);
    }

    public record LoginRequest(String username, String password) {
    }
}
