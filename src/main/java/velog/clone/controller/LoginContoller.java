package velog.clone.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import velog.clone.domain.User;
import velog.clone.service.UserService;

import java.net.http.HttpRequest;
import java.util.Optional;

@Controller
@Slf4j
public class LoginContoller {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("user") User user) {
        return "login";
    }

    @PostMapping("/login")
    public String loginRegi(@ModelAttribute("user") User user, Model model, RedirectAttributes attributes, HttpServletResponse response) {

        // 이메일 존재 여부 확인
        Optional<User> existUserOptional = userService.findByEmail(user.getEmail());

        if (existUserOptional.isEmpty()) {
            model.addAttribute("error", "이메일이 존재하지 않습니다.");
            return "login";
        }

        User existUser = existUserOptional.get();


        // 비밀번호 일치 여부 확인
        if (!existUser.getPassword().equals(user.getPassword())) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "login";
        }

        Cookie idCookie = new Cookie("userId", String.valueOf(existUser.getId()));
        response.addCookie(idCookie);
        attributes.addFlashAttribute("message", "로그인 성공");
        return "redirect:/";
    }

    @GetMapping("/logout")
    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        exprieCookie(response, "userId");
        return "redirect:/";
    }


    private void exprieCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
