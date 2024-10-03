package jp.co.interline.crm.dao;

import jp.co.interline.crm.domain.ClientManager;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface ManagerDAO {
    int registerManager(ClientManager manager);
    int updateManager(ClientManager manager);
    void deleteManager(int manager_id);
    ArrayList<ClientManager> managerList (HashMap<String, String> map, RowBounds rb);
    int getTotal (HashMap<String, String> map);
    ClientManager managerDetails(int manager_id);
}
