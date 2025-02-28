package practice.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import practice.jwt.service.UserService;

import java.security.Security;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/change-password")
    public String changePasswordForm() {
        return "change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            HttpServletRequest request,
            Model model
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();    // 현재 이메일 정보 가져옴

        if(userService.changePassword(email, currentPassword, newPassword)) {
            // 비밀번호 변경 성공 -> 로그아웃 처리
            request.getSession().invalidate();  // 세션 초기화
            SecurityContextHolder.clearContext();   // spring security 컨텍스트 초기화
            return "redirect:/login";
        } else {
            model.addAttribute("errorMessage", "현재 비밀번호가 일치하지 않습니다");
            return "change-password";
        }
    }

}
