package practice.jwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import practice.jwt.entity.User;
import practice.jwt.repository.UserRepository;
import practice.jwt.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")   // ROLE_ADMIN을 가진 사용자만 접근 가능, SecurityContextHolder에서 현재 로그인한 사용자의 Role을 확인
public class AdminController {
    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping
    public String admin() {
        return "admin";
    }

    @GetMapping("/users")
    public String userList(Model model) {
        // 사용자의 목록을 리스트로 출력
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-list";
    }

    @PostMapping("users/{id}/role")
    public String changeUserRole(@PathVariable Long id) {
        // 특정 사용자의 권한을 변경
        userService.toggleUserRole(id);
        return "redirect:/admin/users";
    }
}
