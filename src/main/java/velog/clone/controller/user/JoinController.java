package velog.clone.controller.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import velog.clone.domain.User;
import velog.clone.service.UserService;




@Slf4j
@Controller
public class JoinController {
    @Autowired
    private UserService userService;

    @GetMapping("/join")
    public String showJoinForm(@ModelAttribute("user") User user) {
        return "join";
    }

    @PostMapping("/join")
    public String registerUser(@ModelAttribute("user") User user,
                               @RequestParam("passwordConfirm") String passwordConfirm,
                               Model model) {

        // email 중복 불가능
        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "이미 사용 중인 이메일 입니다.");
            return "join";
        }

        //비밀번호 재확인
        if (!user.getPassword().equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "join";
        }


        userService.createUser(user);

        return "redirect:/login";
    }
}
