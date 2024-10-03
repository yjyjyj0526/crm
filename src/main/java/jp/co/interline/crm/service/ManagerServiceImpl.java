package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.CompanyDAO;
import jp.co.interline.crm.dao.ManagerDAO;
import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.util.PagenationUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ManagerServiceImpl implements ManagerService{
    @Autowired
    private ManagerDAO dao;

    @Override
    public int registerManager(ClientManager manager){
        return dao.registerManager(manager);
    }

    @Override
    public int updateManager(ClientManager manager){
        return dao.updateManager(manager);
    }

    @Override
    public void deleteManager(int manager_id){
        dao.deleteManager(manager_id);
    }

    @Override
    public ArrayList<ClientManager> managerList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);
        map.put("order", order);
        map.put("orderDirection", orderDirection);

        RowBounds rb = new RowBounds(pagenation.getStartRecord(), pagenation.getCountPerPage());
        ArrayList<ClientManager> list = dao.managerList(map, rb);
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
    public ClientManager managerDetails(int manager_id){ return dao.managerDetails(manager_id);}
}
