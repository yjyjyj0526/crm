package jp.co.interline.crm.dao;

import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.domain.ContactHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface ContactDAO {
    List<ContactHistory> getContactHistoriesByOpportunityId(int opportunityId);
    int getMaxOrder(int opportunity_id, int step);
    int registerContact(ContactHistory contact);
}
