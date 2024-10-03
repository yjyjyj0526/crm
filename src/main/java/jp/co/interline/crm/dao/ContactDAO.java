package jp.co.interline.crm.dao;

import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.domain.ContactHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface ContactDAO {
    int registerContact(ContactHistory contact);
    int updateContact(ContactHistory contact);
    void deleteContact(int contact_id);
    ArrayList<ContactHistory> contactList (HashMap<String, String> map, RowBounds rb);
    int getTotal (HashMap<String, String> map);
    ContactHistory contactDetails(int contact_id);
}
