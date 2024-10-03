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
public class UserServiceImpl implements UserService{

    @Autowired
    private UserListDAO dao;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public int joinUser(UserList user){

        String pw = passwordEncoder.encode(user.getPassword());

        user.setPassword(pw);
        return dao.joinUser(user);
    }

    @Override
    public boolean findUserID(String user_id) {
        String existingUserID = dao.findUserID(user_id);
        return existingUserID != null && existingUserID.equals(user_id);
    }
    //社員情報修正
    @Override
    public int updateUser(UserList user){

        String pw = passwordEncoder.encode(user.getPassword());

        user.setPassword(pw);
        return dao.updateUser(user);
    }
    //パスワード検証
    @Override
    public boolean checkUser(String user_id, String inputPassword){
        String existingPassword = dao.checkUser(user_id);

        return passwordEncoder.matches(inputPassword, existingPassword);
    }
    @Override
    public ArrayList<UserList> userList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);
        map.put("order", order);
        map.put("orderDirection", orderDirection);

        RowBounds rb = new RowBounds(pagenation.getStartRecord(), pagenation.getCountPerPage());
        ArrayList<UserList> list = dao.userList(map, rb);
        return list;
    }
    @Override
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);

        int total = dao.getTotal(map);

        PagenationUtil navi = new PagenationUtil(pagePerGroup, countPerPage, page, total);

        return navi;
    }
    @Override
    public UserList userDetails(String user_id) {
        return dao.userDetails(user_id);
    }
    @Override
    public void userDelete(String user_id){
        dao.userDelete(user_id);
    }
}
