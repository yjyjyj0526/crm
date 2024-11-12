package jp.co.interline.crm.controller.api;

import jp.co.interline.crm.domain.UserList;
import jp.co.interline.crm.service.UserService;
import jp.co.interline.crm.util.FileService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserApiController {

    @Value("${spring.servlet.multipart.location}")
    String uploadPath;

    @Autowired
    UserService service;

    private String convertHiraganaToKatakana(String hiragana) {
        StringBuilder katakana = new StringBuilder();
        for (int i = 0; i < hiragana.length(); i++) {
            char ch = hiragana.charAt(i);
            if (ch >= '\u3041' && ch <= '\u3096') {
                katakana.append((char) (ch + 0x60));
            } else {
                katakana.append(ch);
            }
        }
        return katakana.toString();
    }

    private String determineCategory(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return null;
        }

        String hiraganaRegex = "[\u3040-\u309F]+";
        String katakanaRegex = "[\u30A0-\u30FF]+";
        String kanjiRegex = "[\u4E00-\u9FFF]+";

        if (searchText.matches(hiraganaRegex)) {
            return "user_name_phonetic";
        } else if (searchText.matches(katakanaRegex)) {
            return "user_name_phonetic";
        } else if (searchText.matches(kanjiRegex)) {
            return "user_name";
        } else {
            return "user_name";
        }
    }

    //ファイルセーブ
    private String saveFile(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return FileService.saveFile(file, uploadPath);
        }
        return null;
    }

    //ファイル削除
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

    // Base64
    private String encodeFileToBase64(String filePath) throws IOException {
        if (filePath != null && !filePath.isEmpty()) {
            Path path = Paths.get(uploadPath, filePath);
            if (Files.exists(path)) {
                byte[] fileBytes = Files.readAllBytes(path);
                return Base64.getEncoder().encodeToString(fileBytes);
            }
        }
        return null;
    }

    //社員登録
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> joinUser(
            @RequestParam(value = "profile_image", required = false) MultipartFile profile_image,
            @RequestParam("user_id") String userId,
            @RequestParam("user_name") String userName,
            @RequestParam("password") String password,
            @RequestParam(value = "phone_number", required = false) String phoneNumber,
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "joining_date", required = false) String joiningDate,
            @RequestParam(value = "date_of_birth", required = false)String dateOfBirth,
            @RequestParam("authority") int authority,
            @RequestParam("register_member_id") String registerMemberId,
            @RequestParam("user_name_phonetic") String user_name_phonetic) {

        Map<String, Object> response = new HashMap<>();

        try {
            if (service.findUserID(userId)) {
                response.put("success", false);
                response.put("message", "使えないIDです。");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            } else {
                String image = (profile_image != null && !profile_image.isEmpty()) ? saveFile(profile_image) : null;
                LocalDateTime now = LocalDateTime.now();

                UserList user = new UserList();
                user.setUser_id(userId);
                user.setUser_name(userName);
                user.setPassword(password);
                user.setPhone_number(phoneNumber);
                user.setEmail(email);
                user.setJoining_date(joiningDate);
                user.setDate_of_birth(dateOfBirth);
                user.setDepartment(department);
                user.setAuthority(authority);
                user.setRegister_member_id(registerMemberId);
                user.setRegistration_date(String.valueOf(now));
                user.setProfile_image_path(image);
                user.setUser_name_phonetic(user_name_phonetic);
                service.joinUser(user);

                response.put("success", true);
                response.put("message", "ユーザー登録成功");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "ユーザー登録失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //　社員情報修正
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(
            @RequestParam("user_id") String userId,
            @RequestParam("user_name") String userName,
            @RequestParam("phone_number") String phoneNumber,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "joining_date", required = false) String joiningDate,
            @RequestParam(value = "date_of_birth", required = false)String dateOfBirth,
            @RequestParam("department") String department,
            @RequestParam("authority") int authority,
            @RequestParam("update_member_id") String updateMemberId,
            @RequestParam(value = "profile_image", required = false) MultipartFile profileImage) {

        Map<String, Object> response = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();

        UserList user = service.userDetails(userId); // 기존 사용자 정보를 가져옵니다.
        if (user == null) {
            response.put("success", false);
            response.put("message", "ユーザーが見つかりません。");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // 새로운 이미지를 업로드하는 경우
        if (profileImage != null && !profileImage.isEmpty()) {
            // 기존 이미지가 있으면 삭제
            deleteFile(user.getProfile_image_path());

            // 새 이미지 저장
            String newImagePath = saveFile(profileImage);
            user.setProfile_image_path(newImagePath); // 이미지 경로 업데이트
        }

        user.setUser_name(userName);
        user.setPhone_number(phoneNumber);
        user.setEmail(email);
        user.setJoining_date(joiningDate);
        user.setDate_of_birth(dateOfBirth);
        user.setDepartment(department);
        user.setAuthority(authority);
        user.setUpdate_member_id(updateMemberId);
        user.setUpdate_date(String.valueOf(now));

        service.updateUser(user);

        response.put("success", true);
        response.put("message", "ユーザー修正成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/json")
    public ResponseEntity<Map<String, Object>> getUserListJson(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(name = "searchText", required = false) String searchText,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "orderDirection", required = false, defaultValue = "ASC") String orderDirection,
            @RequestParam(name = "countPerPage", required = false, defaultValue = "10") int countPerPage) {

        int pagePerGroup = 5;

        String categorySelect = determineCategory(searchText);

        if ("user_name_phonetic".equals(categorySelect) && searchText != null) {
            searchText = convertHiraganaToKatakana(searchText);
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

        if (navi != null) {
            response.put("totalPages", navi.getTotalPageCount());
        }

        return ResponseEntity.ok(response);
    }

    // 특정 사용자의 상세 정보를 JSON 형태로 반환
    @GetMapping(value = "/details/{user_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUserDetails(@PathVariable String user_id) {
        UserList user = service.userDetails(user_id);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            try {
                String base64Image = encodeFileToBase64(user.getProfile_image_path());
                response.put("profile_image_base64", base64Image);
            } catch (IOException e) {
                response.put("profile_image_base64", null);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // 특정 사용자의 수정 정보를 JSON 형태로 반환
    @GetMapping("/edit/{user_id}")
    public ResponseEntity<Map<String, Object>> getUserEditInfo(@PathVariable String user_id) {
        UserList user = service.userDetails(user_id);
        if (user != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("data", user);
            try {
                String base64Image = encodeFileToBase64(user.getProfile_image_path());
                response.put("profile_image_base64", base64Image);
            } catch (IOException e) {
                response.put("profile_image_base64", null);
            }
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    //社員削除
    @PostMapping("/delete")
    public String userDelete(String user_id){
        service.userDelete(user_id);
        return "";
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam String user_id) {
        UserList user = new UserList();
        user.setUser_id(user_id);

        service.resetPassword(user);
        return "Password reset link has been sent to your registered email.";
    }
}