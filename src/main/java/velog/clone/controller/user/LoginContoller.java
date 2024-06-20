package velog.clone.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import velog.clone.Const.SessionConst;
import velog.clone.domain.User;
import velog.clone.repository.BlogRepository;
import velog.clone.service.UserService;

import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginContoller {
    @Autowired
    private UserService userService;

    private final BlogRepository blogRepository;

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("user") User user) {
        return "login";
    }

    @PostMapping("/login")
    public String loginRegi(@ModelAttribute("user") User user, Model model, RedirectAttributes attributes, HttpServletRequest request) {

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

        //세션
        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, existUser);

        attributes.addFlashAttribute("message", "로그인 성공");
        return "redirect:/";
    }


    @GetMapping("/logout")
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/";
    }
}
