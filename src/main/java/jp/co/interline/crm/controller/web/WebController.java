package jp.co.interline.crm.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {

    //로그인폼 이동
    @GetMapping("/loginform")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String exception,
                            Model model){
        model.addAttribute("error", error);
        model.addAttribute("exception", exception);
        return "login_form";
    }

    @GetMapping({"","/"})
    public String main(){
        return "initial";
    }

    //社員リスト
    @GetMapping("/user/list")
    public String userListForManager(){

        return "/user/user_list";
    }

    @GetMapping("/company/list")
    public String companyList(){
        return "/company/company_list";
    }

    @GetMapping("/manager/list")
    public String managerList(){
        return "/manager/manager_list";
    }

    @GetMapping("/sales/list")
    public String salesList(){
        return "/sales/sales_list";
    }
}
