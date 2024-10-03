package jp.co.interline.crm.service;

import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.util.PagenationUtil;

import java.util.ArrayList;

public interface CompanyService {
    public int registerCompany(ClientCompany company);
    public int updateCompany(ClientCompany company);
    public void deleteCompany(int company_id);
    public ArrayList<ClientCompany> companyList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection);
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText);
    ClientCompany companyDetails(int company_id);
}
