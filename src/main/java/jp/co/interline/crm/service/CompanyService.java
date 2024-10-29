package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.CompanyDAO;
import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.util.PagenationUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class CompanyService {

    @Autowired
    private CompanyDAO dao;

    // 회사 등록
    public int registerCompany(ClientCompany company) {
        return dao.registerCompany(company);
    }

    // 회사 정보 수정
    public int updateCompany(ClientCompany company) {
        return dao.updateCompany(company);
    }

    // 회사 삭제
    public void deleteCompany(int company_id) {
        dao.deleteCompany(company_id);
    }

    // 회사 목록 가져오기
    public ArrayList<ClientCompany> companyList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);
        map.put("order", order);
        map.put("orderDirection", orderDirection);

        RowBounds rb = new RowBounds(pagenation.getStartRecord(), pagenation.getCountPerPage());
        ArrayList<ClientCompany> list = dao.companyList(map, rb);
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

    // 회사 세부 정보 가져오기
    public ClientCompany companyDetails(int company_id) {
        return dao.companyDetails(company_id);
    }
}