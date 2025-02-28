package practice.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import practice.jwt.service.UserService;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    /*
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    */

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam(name="email") String email,
            @RequestParam(name="password") String password,
            Model model
    ) {
        try {
            userService.registerUser(email, password);
            return "redirect:/login";
        } catch(IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }
}