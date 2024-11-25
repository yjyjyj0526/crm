package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.UserListDAO;
import jp.co.interline.crm.domain.UserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminService {

    @Autowired
    UserListDAO dao;

    @Autowired
    PasswordEncoder passwordEncoder;

    // 매니저 생성
    public void createManager() {
        String managerUserID = "manager";
        String managerPassword = "1234"; // 초기 비밀번호

        if (dao.managerDetails(managerUserID) == null) {
            String encodedPassword = passwordEncoder.encode(managerPassword);
            LocalDateTime now = LocalDateTime.now();

            UserList managerUser = new UserList();
            managerUser.setUser_id(managerUserID);
            managerUser.setUser_name(managerUserID);
            managerUser.setPassword(encodedPassword);
            managerUser.setAuthority(1);
            managerUser.setRegistration_date(String.valueOf(now));
            managerUser.setRegister_member_id(managerUserID);

            dao.insertManager(managerUser);
        }
    }
}