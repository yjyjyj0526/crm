package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.ContactDAO;
import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.domain.ContactHistory;
import jp.co.interline.crm.util.PagenationUtil;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ContactServiceImpl implements ContactService{
    @Autowired
    private ContactDAO dao;

    @Override
    public int registerContact(ContactHistory contact){
        return dao.registerContact(contact);
    }

    @Override
    public int updateContact(ContactHistory contact){
        return dao.updateContact(contact);
    }

    @Override
    public void deleteContact(int contact_id){
        dao.deleteContact(contact_id);
    }

    @Override
    public ArrayList<ContactHistory> contactList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection) {
        HashMap<String, String> map = new HashMap<>();
        map.put("categorySelect", categorySelect);
        map.put("searchText", searchText);
        map.put("order", order);
        map.put("orderDirection", orderDirection);

        RowBounds rb = new RowBounds(pagenation.getStartRecord(), pagenation.getCountPerPage());
        ArrayList<ContactHistory> list = dao.contactList(map, rb);
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
    public ContactHistory contactDetails(int contact_id){ return dao.contactDetails(contact_id);}
}
