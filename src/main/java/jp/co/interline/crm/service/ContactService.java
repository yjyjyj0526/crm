//package jp.co.interline.crm.service;
//
//import jp.co.interline.crm.dao.ContactDAO;
//import jp.co.interline.crm.domain.ContactHistory;
//import jp.co.interline.crm.util.PagenationUtil;
//import org.apache.ibatis.session.RowBounds;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//@Service
//public class ContactService {
//
//    @Autowired
//    private ContactDAO dao;
//
//    // 연락 기록 등록
//    public int registerContact(ContactHistory contact) {
//        return dao.registerContact(contact);
//    }
//
//    // 연락 기록 수정
//    public int updateContact(ContactHistory contact) {
//        return dao.updateContact(contact);
//    }
//
//    // 연락 기록 삭제
//    public void deleteContact(int contact_id) {
//        dao.deleteContact(contact_id);
//    }
//
//    // 연락 기록 리스트 가져오기
//    public ArrayList<ContactHistory> contactList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("categorySelect", categorySelect);
//        map.put("searchText", searchText);
//        map.put("order", order);
//        map.put("orderDirection", orderDirection);
//
//        RowBounds rb = new RowBounds(pagenation.getStartRecord(), pagenation.getCountPerPage());
//        ArrayList<ContactHistory> list = dao.contactList(map, rb);
//        return list;
//    }
//
//    // 페이지 네비게이터 가져오기
//    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText) {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("categorySelect", categorySelect);
//        map.put("searchText", searchText);
//
//        int total = dao.getTotal(map);
//
//        PagenationUtil navi = new PagenationUtil(pagePerGroup, countPerPage, page, total);
//
//        return navi;
//    }
//
//    // 연락 기록 상세 정보 가져오기
//    public ContactHistory contactDetails(int contact_id) {
//        return dao.contactDetails(contact_id);
//    }
//}