package jp.co.interline.crm.service;

import jp.co.interline.crm.domain.UserList;
import jp.co.interline.crm.util.PagenationUtil;

import java.util.ArrayList;

public interface UserService {
    public int joinUser(UserList user);
    public boolean findUserID(String user_id);
    public int updateUser(UserList user);
    public boolean checkUser(String user_id, String inputPassword);
    public ArrayList<UserList> userList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection);
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText);
    UserList userDetails(String user_id);
    public void userDelete(String user_id);
}
