package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.ManagerDAO;
import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.util.PagenationUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ManagerService {

    @Autowired
    private ManagerDAO dao;

    // 관리자 등록
    public int registerManager(ClientManager manager) {
        return dao.registerManager(manager);
    }

    // 관리자 정보 수정
    public int updateManager(ClientManager manager) {
        return dao.updateManager(manager);
    }

    // 관리자 삭제
    public void deleteManager(int manager_id) {
        dao.deleteManager(manager_id);
    }

    // 관리자 목록 가져오기
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

    // 페이지 네비게이터 가져오기
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);

        int total = dao.getTotal(map);

        PagenationUtil navi = new PagenationUtil(pagePerGroup, countPerPage, page, total);

        return navi;
    }

    // 관리자 세부 정보 가져오기
    public ClientManager managerDetails(int manager_id) {
        return dao.managerDetails(manager_id);
    }
}