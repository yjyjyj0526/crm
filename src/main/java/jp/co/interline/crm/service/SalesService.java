package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.ContactDAO;
import jp.co.interline.crm.dao.ManagerDAO;
import jp.co.interline.crm.dao.SalesDAO;
import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.domain.ContactHistory;
import jp.co.interline.crm.domain.SalesOpportunity;
import jp.co.interline.crm.util.PagenationUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SalesService {
    @Autowired
    private SalesDAO salesDAO;

    @Autowired
    private ContactDAO contactDAO;

    // 관리자 등록
    public int registerSales(SalesOpportunity sales) {
        return salesDAO.registerSales(sales);
    }

    // 관리자 정보 수정
    public int updateSales(SalesOpportunity manager) {
        return salesDAO.updateSales(manager);
    }

    // 관리자 삭제
    public void deleteManager(int opportunity_id) {
        salesDAO.deleteSales(opportunity_id);
    }

    // 관리자 목록 가져오기
    public ArrayList<SalesOpportunity> salesList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);
        map.put("order", order);
        map.put("orderDirection", orderDirection);

        RowBounds rb = new RowBounds(pagenation.getStartRecord(), pagenation.getCountPerPage());
        ArrayList<SalesOpportunity> list = salesDAO.salesList(map, rb);
        return list;
    }

    // 페이지 네비게이터 가져오기
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);

        int total = salesDAO.getTotal(map);

        PagenationUtil navi = new PagenationUtil(pagePerGroup, countPerPage, page, total);

        return navi;
    }

    public SalesOpportunity getSalesOpportunityById(int opportunityId) {
        return salesDAO.salesDetails(opportunityId);
    }

    public List<ContactHistory> getContactHistoriesByOpportunityId(int opportunityId) {
        return contactDAO.getContactHistoriesByOpportunityId(opportunityId);
    }
}
