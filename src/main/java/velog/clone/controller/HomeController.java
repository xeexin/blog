package velog.clone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import velog.clone.domain.User;
import velog.clone.repository.UserRepository;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserRepository userRepository;


    //    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/")
    public String HomeLogin(@CookieValue(name = "userId", required = false) String userId, Model model) {
        if (userId == null) {
            return "index";
        }

        try {
            Long userIdLong = Long.parseLong(userId);
            Optional<User> loginMember = userRepository.findById(userIdLong);
            if (!loginMember.isPresent()) {
                return "index";
            }
            model.addAttribute("user", loginMember.get());
            return "loginHome";
        } catch (NumberFormatException e) {
            // userId가 Long 타입으로 변환되지 않으면 index 페이지로 리디렉션
            return "index";
        }
    }
}
