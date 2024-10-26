package jp.co.interline.crm.controller.api;

import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.service.ManagerService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Controller
public class ManagerApiController {
    @Autowired
    ManagerService service;
    //顧客登録
    @PostMapping("/manager/register")
    public ResponseEntity<?> registerManager(@RequestParam("manager_name") String manager_name,
                                             @RequestParam("company_id") int company_id,
                                             @RequestParam(value = "department", required = false) String department,
                                             @RequestParam(value = "position", required = false) String position,
                                             @RequestParam(value = "manager_age", required = false) String manager_age,
                                             @RequestParam(value = "manager_mail", required = false) String manager_mail,
                                             @RequestParam(value = "phone_number_1", required = false) String phone_number_1,
                                             @RequestParam(value = "phone_number_2", required = false) String phone_number_2,
                                             @RequestParam("register_member_id") String register_member_id){
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

        return ResponseEntity.ok("登録成功");
    }
    //顧客修正
    @PostMapping("/manager/update")
    public ResponseEntity<?> updateManager(@RequestParam("manager_id") String manager_id,
                                           @RequestParam("manager_name") String manager_name,
                                           @RequestParam("company_id") int company_id,
                                           @RequestParam(value = "department", required = false) String department,
                                           @RequestParam(value = "position", required = false) String position,
                                           @RequestParam(value = "manager_age", required = false) String manager_age,
                                           @RequestParam(value = "manager_mail", required = false) String manager_mail,
                                           @RequestParam(value = "phone_number_1", required = false) String phone_number_1,
                                           @RequestParam(value = "phone_number_2", required = false) String phone_number_2,
                                           @RequestParam("update_member_id") String update_member_id){
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

        return ResponseEntity.ok("修正成功");
    }
    //削除
    @PostMapping("/manager/delete")
    public String deleteManager(int manager_id){
        service.deleteManager(manager_id);
        return "";
    }
    //顧客リスト
    @GetMapping("/manager/list")
    public ResponseEntity<?> managerList(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(name="categorySelect", required = false) String categorySelect,
                                         @RequestParam(name="searchText", required = false) String searchText,
                                         @RequestParam(name="order", required = false) String order,
                                         @RequestParam(name="orderDirection", required = false, defaultValue = "ASC") String orderDirection,
                                         @RequestParam(name="countPerPage", required = false, defaultValue = "10") int countPerPage,
                                         Model model) {
        int pagePerGroup = 5;

        PagenationUtil navi = service.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
        ArrayList<ClientManager> list = service.managerList(navi, categorySelect, searchText, order, orderDirection);

        model.addAttribute("navi", navi);
        model.addAttribute("list", list);
        model.addAttribute("order", order);
        model.addAttribute("orderDirection", orderDirection);
        model.addAttribute("categorySelect", categorySelect);
        model.addAttribute("searchText", searchText);
        model.addAttribute("countPerPage", countPerPage);

        return ResponseEntity.ok(list);
    }
    //顧客社詳細
    @GetMapping("/manager/detail/{manager_id}")
    public String companyDetails(@PathVariable int manager_id, Model model) {
        ClientManager manager = service.managerDetails(manager_id);
        model.addAttribute("manager", manager);
        return "";
    }
}
