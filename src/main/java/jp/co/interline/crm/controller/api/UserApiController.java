package jp.co.interline.crm.controller.api;

import jp.co.interline.crm.domain.UserList;
import jp.co.interline.crm.service.UserService;
import jp.co.interline.crm.util.FileService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserApiController {

    @Value("${spring.servlet.multipart.location}")
    String uploadPath;

    @Autowired
    UserService service;

    //파일 저장
    private String saveFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return FileService.saveFile(file, uploadPath);
        }
        return null;
    }

    //파일 삭제
    private void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            Path filePath = Paths.get(uploadPath, fileName);
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                }
            }
        }
    }

    //회원가입
    @PostMapping("/user/join")
    public ResponseEntity<Map<String, Object>> joinUser(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("user_id") String userId,
            @RequestParam("user_name") String userName,
            @RequestParam("password") String password,
            @RequestParam(value = "phone_number", required = false) String phoneNumber,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam("authority") int authority,
            @RequestParam("register_member_id") String registerMemberId) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (service.findUserID(userId)) {
                response.put("message", "사용할 수 없는 ID입니다.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else {
                String solutionPic = (file != null && !file.isEmpty()) ? saveFile(file) : null;
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
                String formattedDate = now.format(formatter);

                UserList user = new UserList();
                user.setUser_id(userId);
                user.setUser_name(userName);
                user.setPassword(password);
                user.setPhone_number(phoneNumber);
                user.setDepartment(department);
                user.setAuthority(authority);
                user.setRegister_member_id(registerMemberId);
                user.setRegistration_date(formattedDate);
                user.setProfile_image_path(solutionPic);  // 파일 경로 설정
                service.joinUser(user);

                response.put("message", "ユーザー登録成功");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("message", "ユーザー登録 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    @GetMapping("/user/list/json")
    public ResponseEntity<Map<String, Object>> getUserListJson(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "categorySelect", required = false) String categorySelect,
            @RequestParam(name = "searchText", required = false) String searchText,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "orderDirection", required = false, defaultValue = "ASC") String orderDirection,
            @RequestParam(name = "countPerPage", required = false, defaultValue = "10") int countPerPage) {

        int pagePerGroup = 5;

        if (searchText == null || searchText.isEmpty()) {
            searchText = null; // 검색어가 없으면 검색 조건을 null로 설정
        }

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