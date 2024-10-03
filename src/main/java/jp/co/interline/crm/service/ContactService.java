package jp.co.interline.crm.service;

import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.domain.ContactHistory;
import jp.co.interline.crm.util.PagenationUtil;

import java.util.ArrayList;

public interface ContactService {
    public int registerContact(ContactHistory contact);
    public int updateContact(ContactHistory contact);
    public void deleteContact(int contact_id);
    public ArrayList<ContactHistory> contactList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection);
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText);
    ContactHistory contactDetails(int contact_id);
}
