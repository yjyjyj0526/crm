package jp.co.interline.crm.controller.web;

import jp.co.interline.crm.domain.UserList;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class UserWebController {

    //로그인폼 이동
    @GetMapping("/loginform")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String exception,
                            Model model){
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "login_form";
    }

    //회원가입폼 이동
    @GetMapping("/user/joinform")
    public String joinForm(){
        return "join_form";
    }

    //社員リスト
    @GetMapping("/user/list")
    public String userListForManager(){

        return "user_list";
    }
}
