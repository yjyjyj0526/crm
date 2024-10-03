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
public class CompanyServiceImpl implements CompanyService{
    @Autowired
    private CompanyDAO dao;

    @Override
    public int registerCompany(ClientCompany company){
        return dao.registerCompany(company);
    }

    @Override
    public int updateCompany(ClientCompany company){
        return dao.updateCompany(company);
    }

    @Override
    public void deleteCompany(int company_id){
        dao.deleteCompany(company_id);
    }

    @Override
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
    public ClientCompany companyDetails(int company_id){ return dao.companyDetails(company_id);}
}
