package jp.co.interline.crm.dao;

import jp.co.interline.crm.domain.ClientCompany;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface CompanyDAO {
    int registerCompany(ClientCompany company);
    int updateCompany(ClientCompany company);
    void deleteCompany(int company_id);
    ArrayList<ClientCompany> companyList (HashMap<String, String> map, RowBounds rb);
    int getTotal (HashMap<String, String> map);
    ClientCompany companyDetails(int company_id);
}
