package jp.co.interline.crm.dao;

import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.domain.SalesOpportunity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface SalesDAO {
    int registerSales(SalesOpportunity sales);
    int updateSales(SalesOpportunity manager);
    void deleteSales(int manager_id);
    ArrayList<SalesOpportunity> salesList(HashMap<String, String> map, RowBounds rb);
    int getTotal (HashMap<String, String> map);
    SalesOpportunity salesDetails(int manager_id);
}
