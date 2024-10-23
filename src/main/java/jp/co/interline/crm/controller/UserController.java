package jp.co.interline.crm.controller;

import jp.co.interline.crm.domain.UserList;
import jp.co.interline.crm.service.UserService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    UserService service;

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

    //회원가입
    @PostMapping("/user/join")
    public ResponseEntity<?> joinUser(@RequestParam("user_id") String user_id,
                                      @RequestParam("user_name") String user_name,
                                      @RequestParam("password") String password,
                                      @RequestParam(value = "phone_number", required = false) String phone_number,
                                      @RequestParam(value = "department", required = false) String department,
                                      @RequestParam("authority") int authority,
                                      @RequestParam("register_member_id") String register_member_id) {
        if (service.findUserID(user_id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("使用できないIDです。");
        } else {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
            String formattedDate = now.format(formatter);

            UserList user = new UserList();
            user.setUser_id(user_id);
            user.setUser_name(user_name);
            user.setPassword(password);
            user.setPhone_number(phone_number);
            user.setDepartment(department);
            user.setAuthority(authority);
            user.setRegister_member_id(register_member_id);
            user.setRegistration_date(formattedDate);
            service.joinUser(user);
            return ResponseEntity.ok("ユーザー登録成功");
        }
    }

    //パスワード検証
    @PostMapping("/user/check")
    public ResponseEntity<?> checkUser(@RequestParam("user_id") String user_id,
                                       @RequestParam("password") String inputPassword){
        boolean check = service.checkUser(user_id, inputPassword);

        if(check) {
            return ResponseEntity.ok("パスワード検証成功");
        } else {
            return ResponseEntity.status(401).body("パスワード検証失敗");
        }
    }

    //社員情報修正
    @PostMapping("/user/update")
    public ResponseEntity<?> updateUser(@RequestParam("user_id") String user_id,
                                        @RequestParam("user_name") String user_name,
                                        @RequestParam("password") String password,
                                        @RequestParam(value = "phone_number", required = false) String phone_number,
                                        @RequestParam(value = "department", required = false) String department,
                                        @RequestParam("authority") int authority,
                                        @RequestParam("update_member_id") String update_member_id){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
        String formattedDate = now.format(formatter);

        UserList user = new UserList();
        user.setUser_id(user_id);
        user.setUser_name(user_name);
        user.setPassword(password);
        user.setPhone_number(phone_number);
        user.setDepartment(department);
        user.setAuthority(authority);
        user.setUpdate_member_id(update_member_id);
        user.setUpdate_date(formattedDate);
        service.updateUser(user);
        return ResponseEntity.ok("ユーザー修正成功");
    }

    //社員リスト
    @GetMapping("/user/list")
    public String userListForManager(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "categorySelect", required = false) String categorySelect,
            @RequestParam(name = "searchText", required = false) String searchText,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "orderDirection", required = false, defaultValue = "ASC") String orderDirection,
            @RequestParam(name = "countPerPage", required = false, defaultValue = "10") int countPerPage,
            Model model) {
        int pagePerGroup = 5;

        PagenationUtil navi = service.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
        ArrayList<UserList> list = service.userList(navi, categorySelect, searchText, order, orderDirection);

        model.addAttribute("navi", navi);
        model.addAttribute("list", list);
        model.addAttribute("order", order);
        model.addAttribute("orderDirection", orderDirection);
        model.addAttribute("categorySelect", categorySelect);
        model.addAttribute("searchText", searchText);
        model.addAttribute("countPerPage", countPerPage);

        return "user_list";
    }

    @GetMapping("/user/list/json")
    public ResponseEntity<Map<String, Object>> getUserListJson(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "categorySelect", required = false) String categorySelect,
            @RequestParam(name = "searchText", required = false) String searchText,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "orderDirection", required = false, defaultValue = "ASC") String orderDirection,
            @RequestParam(name = "countPerPage", required = false, defaultValue = "10") int countPerPage) {

        int pagePerGroup = 5;

        PagenationUtil navi = service.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
        ArrayList<UserList> list = service.userList(navi, categorySelect, searchText, order, orderDirection);

        Map<String, Object> response = new HashMap<>();
        response.put("navi", navi);
        response.put("list", list);
        response.put("order", order);
        response.put("orderDirection", orderDirection);
        response.put("categorySelect", categorySelect);
        response.put("searchText", searchText);
        response.put("countPerPage", countPerPage);

        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 상세 정보를 JSON 형태로 반환
    @GetMapping("/user/details/{user_id}")
    public ResponseEntity<UserList> getUserDetails(@PathVariable String user_id) {
        UserList user = service.userDetails(user_id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 특정 사용자의 수정 정보를 JSON 형태로 반환
    @GetMapping("/user/edit/{user_id}")
    public ResponseEntity<UserList> getUserEditInfo(@PathVariable String user_id) {
        UserList user = service.userDetails(user_id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //社員削除
    @PostMapping("/user/delete")
    public String userDelete(String user_id){
        service.userDelete(user_id);
        return "";
    }
}