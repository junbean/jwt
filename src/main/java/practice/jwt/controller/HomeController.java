package practice.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");

        System.out.println("email = " + email);
        System.out.println("role = " + role);

        model.addAttribute("userEmail", email);
        model.addAttribute("userRole", role);
        return "dashboard";
    }

    /* AdminController로 이관됨
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
    */
}

/*
@GetMapping("/dashboard")
public String dashboard(HttpServletRequest request, Model model) {
    System.out.println(request.getAttribute("userEmail"));
    System.out.println(request.getAttribute("userRole"));
    model.addAttribute("userEmail", request.getAttribute("userEmail"));
    model.addAttribute("userRole", request.getAttribute("userRole"));
    return "dashboard";
}
*/
