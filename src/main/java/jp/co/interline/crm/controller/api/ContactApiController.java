package jp.co.interline.crm.controller.api;

import jp.co.interline.crm.domain.ContactHistory;
import jp.co.interline.crm.service.ContactService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ContactApiController {
    @Autowired
    ContactService service;

    @PostMapping("/contact/register")
    public ResponseEntity<Map<String, Object>> registerContact(@RequestParam("opportunity_id") int opportunity_id,
                                             @RequestParam("manager_id") int manager_id,
                                             @RequestParam("user_id_1") String user_id_1,
                                             @RequestParam(value = "user_id_2", required = false) String user_id_2,
                                             @RequestParam(value = "user_id_3", required = false) String user_id_3,
                                             @RequestParam("contact_method") String contact_method,
                                             @RequestParam("contact_date") String contact_date,
                                             @RequestParam("contact_details") String contact_details,
                                             @RequestParam("step") int step,
                                             @RequestParam("register_member_id") String register_member_id){
        Map<String, Object> response = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        ContactHistory contact = new ContactHistory();
        contact.setOpportunity_id(opportunity_id);
        contact.setManager_id(manager_id);
        contact.setUser_id_1(user_id_1);
        contact.setUser_id_2(user_id_2);
        contact.setUser_id_3(user_id_3);
        contact.setContact_method(contact_method);
        contact.setContact_date(contact_date);
        contact.setContact_details(contact_details);
        contact.setRegister_member_id(register_member_id);
        contact.setRegistration_date(String.valueOf(now));
        contact.setStep(step);
        service.registerContact(contact);

        response.put("success", true);
        response.put("message", "顧客登録成功");
        return ResponseEntity.ok(response);
    }

}
