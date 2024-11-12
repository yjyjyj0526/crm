package jp.co.interline.crm.dao;

import jp.co.interline.crm.domain.UserList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface UserListDAO {
    String managerDetails(String user_id);
    void insertManager(UserList userlist);
    int joinUser(UserList user);
    String findUserID(String user_id);
    int updateUser(UserList user);
    String checkUser(String user_id);
    ArrayList<UserList> userList (HashMap<String, String> map, RowBounds rb);
    int getTotal (HashMap<String, String> map);
    UserList userDetails(String user_id);
    void userDelete(String user_id);
    int resetPassword(UserList user);
}
