package jp.co.interline.crm.controller.api;

import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.service.ManagerService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manager")
public class ManagerApiController {
    @Autowired
    ManagerService service;
    //顧客登録
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerManager(@RequestParam("manager_name") String manager_name,
                                                               @RequestParam("company_id") int company_id,
                                                               @RequestParam(value = "department", required = false) String department,
                                                               @RequestParam(value = "position", required = false) String position,
                                                               @RequestParam(value = "manager_age", required = false) String manager_age,
                                                               @RequestParam(value = "manager_mail", required = false) String manager_mail,
                                                               @RequestParam(value = "phone_number_1", required = false) String phone_number_1,
                                                               @RequestParam(value = "phone_number_2", required = false) String phone_number_2,
                                                               @RequestParam("register_member_id") String register_member_id){
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
            String formattedDate = now.format(formatter);

            ClientManager manager = new ClientManager();
            manager.setManager_name(manager_name);
            manager.setCompany_id(company_id);
            manager.setDepartment(department);
            manager.setPosition(position);
            manager.setManager_age(manager_age);
            manager.setManager_mail(manager_mail);
            manager.setPhone_number_1(phone_number_1);
            manager.setPhone_number_2(phone_number_2);
            manager.setRegister_member_id(register_member_id);
            manager.setRegistration_date(formattedDate);
            service.registerManager(manager);

            response.put("success", true);
            response.put("message", "顧客登録成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "顧客登録失敗" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    //顧客修正
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateManager(@RequestParam("manager_id") String manager_id,
                                           @RequestParam("manager_name") String manager_name,
                                           @RequestParam("company_id") int company_id,
                                           @RequestParam(value = "department", required = false) String department,
                                           @RequestParam(value = "position", required = false) String position,
                                           @RequestParam(value = "manager_age", required = false) String manager_age,
                                           @RequestParam(value = "manager_mail", required = false) String manager_mail,
                                           @RequestParam(value = "phone_number_1", required = false) String phone_number_1,
                                           @RequestParam(value = "phone_number_2", required = false) String phone_number_2,
                                           @RequestParam("update_member_id") String update_member_id){

        Map<String, Object> response = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
        String formattedDate = now.format(formatter);

        ClientManager manager = new ClientManager();
        manager.setManager_id(manager_id);
        manager.setManager_name(manager_name);
        manager.setCompany_id(company_id);
        manager.setDepartment(department);
        manager.setPosition(position);
        manager.setManager_age(manager_age);
        manager.setManager_mail(manager_mail);
        manager.setPhone_number_1(phone_number_1);
        manager.setPhone_number_2(phone_number_2);
        manager.setUpdate_member_id(update_member_id);
        manager.setUpdate_date(formattedDate);
        service.updateManager(manager);

        response.put("success", true);
        response.put("message", "顧客上方修正成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/json")
    public ResponseEntity<Map<String, Object>> managerList(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(name="categorySelect", required = false) String categorySelect,
                                         @RequestParam(name="searchText", required = false) String searchText,
                                         @RequestParam(name="order", required = false) String order,
                                         @RequestParam(name="orderDirection", required = false, defaultValue = "ASC") String orderDirection,
                                         @RequestParam(name="countPerPage", required = false, defaultValue = "10") int countPerPage,
                                         Model model) {
        int pagePerGroup = 5;

        if (searchText == null || searchText.isEmpty()) {
            searchText = null;
        }
        PagenationUtil navi = service.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
        ArrayList<ClientManager> list = service.managerList(navi, categorySelect, searchText, order, orderDirection);

        Map<String, Object> response = new HashMap<>();

        response.put("navi", navi);
        response.put("list", list);
        response.put("order", order);
        response.put("orderDirection", orderDirection);
        response.put("categorySelect", categorySelect);
        response.put("searchText", searchText);
        response.put("countPerPage", countPerPage);

        if (navi != null) {
            response.put("totalPages", navi.getTotalPageCount());
        }

        return ResponseEntity.ok(response);
    }

    //顧客社詳細
    @GetMapping(value = "/details/{manager_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> companyDetails(@PathVariable int manager_id) {
        ClientManager manager = service.managerDetails(manager_id);
        if (manager != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("manager", manager);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/edit/{manager_id}")
    public ResponseEntity<ClientManager> getUserEditInfo(@PathVariable int manager_id) {
        ClientManager manager = service.managerDetails(manager_id);
        if (manager != null) {
            return ResponseEntity.ok(manager);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
