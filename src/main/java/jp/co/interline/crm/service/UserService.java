package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.UserListDAO;
import jp.co.interline.crm.domain.UserList;
import jp.co.interline.crm.util.PagenationUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class UserService {

    @Autowired
    private UserListDAO dao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 회원 가입
    public int joinUser(UserList user) {
        String pw = passwordEncoder.encode(user.getPassword());
        user.setPassword(pw);
        return dao.joinUser(user);
    }

    // 사용자 ID 찾기
    public boolean findUserID(String user_id) {
        String existingUserID = dao.findUserID(user_id);
        return existingUserID != null && existingUserID.equals(user_id);
    }

    // 사용자 정보 수정
    public int updateUser(UserList user) {
        String pw = passwordEncoder.encode(user.getPassword());
        user.setPassword(pw);
        return dao.updateUser(user);
    }

    // 사용자 확인
    public boolean checkUser(String user_id, String inputPassword) {
        String existingPassword = dao.checkUser(user_id);
        return passwordEncoder.matches(inputPassword, existingPassword);
    }

    // 사용자 목록 가져오기
    public ArrayList<UserList> userList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);
        map.put("order", order);
        map.put("orderDirection", orderDirection);

        RowBounds rb = new RowBounds(pagenation.getStartRecord(), pagenation.getCountPerPage());
        return dao.userList(map, rb);
    }

    // 페이지 네비게이터 가져오기
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);

        int total = dao.getTotal(map);
        return new PagenationUtil(pagePerGroup, countPerPage, page, total);
    }

    // 사용자 세부 정보 가져오기
    public UserList userDetails(String user_id) {
        return dao.userDetails(user_id);
    }

    // 사용자 삭제
    public void userDelete(String user_id) {
        dao.userDelete(user_id);
    }
}