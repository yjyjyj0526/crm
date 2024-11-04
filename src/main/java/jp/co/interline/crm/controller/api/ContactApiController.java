//package jp.co.interline.crm.controller.api;
//
//import jp.co.interline.crm.domain.ContactHistory;
//import jp.co.interline.crm.service.ContactService;
//import jp.co.interline.crm.util.PagenationUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//
//@Controller
//public class ContactApiController {
//    @Autowired
//    ContactService service;
//    //컨텍트 날짜 -> 날짜 형식 변경 추가 할 것
//    @PostMapping("/contact/register")
//    public ResponseEntity<?> registerContact(@RequestParam("manager_id") int manager_id,
//                                             @RequestParam("user_id_1") String user_id_1,
//                                             @RequestParam(value = "user_id_2", required = false) String user_id_2,
//                                             @RequestParam(value = "user_id_3", required = false) String user_id_3,
//                                             @RequestParam("contact_method") String contact_method,
//                                             @RequestParam("contact_date") String contact_date,
//                                             @RequestParam("contact_details") String contact_details,
//                                             @RequestParam("register_member_id") String register_member_id){
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
//        String formattedDate = now.format(formatter);
//
//        ContactHistory contact = new ContactHistory();
//        contact.setManager_id(manager_id);
//        contact.setUser_id_1(user_id_1);
//        contact.setUser_id_2(user_id_2);
//        contact.setUser_id_3(user_id_3);
//        contact.setContact_method(contact_method);
//        contact.setContact_date(contact_date);
//        contact.setContact_details(contact_details);
//        contact.setRegister_member_id(register_member_id);
//        contact.setRegistration_date(formattedDate);
//        service.registerContact(contact);
//
//        return ResponseEntity.ok("");
//    }
//    //コンタクト履歴修正
//    @PostMapping("/contact/update")
//    public ResponseEntity<?> updateContact(@RequestParam("contact_id") int contact_id,
//                                           @RequestParam("manager_id") int manager_id,
//                                           @RequestParam("user_id_1") String user_id_1,
//                                           @RequestParam(value = "user_id_2", required = false) String user_id_2,
//                                           @RequestParam(value = "user_id_3", required = false) String user_id_3,
//                                           @RequestParam("contact_method") String contact_method,
//                                           @RequestParam("contact_date") String contact_date,
//                                           @RequestParam("contact_details") String contact_details,
//                                           @RequestParam("update_member_id") String update_member_id){
//        LocalDateTime now = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
//        String formattedDate = now.format(formatter);
//
//        ContactHistory contact = new ContactHistory();
//        contact.setContact_id(contact_id);
//        contact.setManager_id(manager_id);
//        contact.setUser_id_1(user_id_1);
//        contact.setUser_id_2(user_id_2);
//        contact.setUser_id_3(user_id_3);
//        contact.setContact_method(contact_method);
//        contact.setContact_date(contact_date);
//        contact.setContact_details(contact_details);
//        contact.setUpdate_member_id(update_member_id);
//        contact.setUpdate_date(formattedDate);
//        service.updateContact(contact);
//
//        return ResponseEntity.ok("");
//    }
//    //削除
//    @PostMapping("/contact/delete")
//    public String deleteContact(int contact_id){
//        service.deleteContact(contact_id);
//        return "";
//    }
//    //リスト
//    @GetMapping("/contact/list")
//    public ResponseEntity<?> contactList(@RequestParam(defaultValue = "1") int page,
//                                         @RequestParam(name="categorySelect", required = false) String categorySelect,
//                                         @RequestParam(name="searchText", required = false) String searchText,
//                                         @RequestParam(name="order", required = false) String order,
//                                         @RequestParam(name="orderDirection", required = false, defaultValue = "ASC") String orderDirection,
//                                         @RequestParam(name="countPerPage", required = false, defaultValue = "10") int countPerPage,
//                                         Model model) {
//        int pagePerGroup = 5;
//
//        PagenationUtil navi = service.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
//        ArrayList<ContactHistory> list = service.contactList(navi, categorySelect, searchText, order, orderDirection);
//
//        model.addAttribute("navi", navi);
//        model.addAttribute("list", list);
//        model.addAttribute("order", order);
//        model.addAttribute("orderDirection", orderDirection);
//        model.addAttribute("categorySelect", categorySelect);
//        model.addAttribute("searchText", searchText);
//        model.addAttribute("countPerPage", countPerPage);
//
//        return ResponseEntity.ok(list);
//    }
//    //詳細
//    @GetMapping("/contact/detail/{contact_id}")
//    public String contactDetails(@PathVariable int contact_id, Model model) {
//        ContactHistory contact = service.contactDetails(contact_id);
//        model.addAttribute("contact", contact);
//        return "";
//    }
//}
