package jp.co.interline.crm.controller.api;

import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.domain.UserList;
import jp.co.interline.crm.service.CompanyService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/company")
public class CompanyApiController {
    @Autowired
    CompanyService service;

    //顧客社登録
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerCompany(@RequestParam("company_name") String company_name,
                                             @RequestParam(value = "CEO_name", required = false) String CEO_name,
                                             @RequestParam(value = "phone_number", required = false) String phone_number,
                                             @RequestParam("post_number") String post_number,
                                             @RequestParam("address") String address,
                                             @RequestParam("homepage") String homepage,
                                             @RequestParam(value = "business_type", required = false) String business_type,
                                             @RequestParam("contract_type") String contract_type,
                                             @RequestParam("register_member_id") String register_member_id){

        Map<String, Object> response = new HashMap<>();

        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
            String formattedDate = now.format(formatter);

            ClientCompany company = new ClientCompany();
            company.setCompany_name(company_name);
            company.setCEO_name(CEO_name);
            company.setPhone_number(phone_number);
            company.setPost_number(post_number);
            company.setAddress(address);
            company.setHomepage(homepage);
            company.setBusiness_type(business_type);
            company.setContract_type(contract_type);
            company.setRegistration_date(formattedDate);
            company.setRegister_member_id(register_member_id);
            service.registerCompany(company);

            response.put("success", true);
            response.put("message", "顧客社登録成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "顧客社登録失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    //　顧客社情報修正
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCompany(@RequestParam("company_id") int company_id,
                                           @RequestParam("company_name") String company_name,
                                           @RequestParam(value = "CEO_name", required = false) String CEO_name,
                                           @RequestParam(value = "phone_number", required = false) String phone_number,
                                           @RequestParam("post_number") String post_number,
                                           @RequestParam("address") String address,
                                           @RequestParam("homepage") String homepage,
                                           @RequestParam(value = "business_type", required = false) String business_type,
                                           @RequestParam("contract_type") String contract_type,
                                           @RequestParam("update_member_id") String update_member_id){

        Map<String, Object> response = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
        String formattedDate = now.format(formatter);

        ClientCompany company = new ClientCompany();
        company.setCompany_id(company_id);
        company.setCompany_name(company_name);
        company.setCEO_name(CEO_name);
        company.setPhone_number(phone_number);
        company.setPost_number(post_number);
        company.setAddress(address);
        company.setHomepage(homepage);
        company.setBusiness_type(business_type);
        company.setContract_type(contract_type);
        company.setUpdate_date(formattedDate);
        company.setUpdate_member_id(update_member_id);
        service.updateCompany(company);

        response.put("success", true);
        response.put("message", "顧客社修正成功");
        return ResponseEntity.ok(response);
    }

    //顧客社リスト
    @GetMapping("/list/json")
    public ResponseEntity<Map<String, Object>> companyList(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(name="categorySelect", required = false) String categorySelect,
                                                @RequestParam(name="searchText", required = false) String searchText,
                                                @RequestParam(name="order", required = false) String order,
                                                @RequestParam(name="orderDirection", required = false, defaultValue = "ASC") String orderDirection,
                                                @RequestParam(name="countPerPage", required = false, defaultValue = "10") int countPerPage) {
        int pagePerGroup = 5;

        if (searchText == null || searchText.isEmpty()) {
            searchText = null;
        }

        PagenationUtil navi = service.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
        ArrayList<ClientCompany> list = service.companyList(navi, categorySelect, searchText, order, orderDirection);

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

    @GetMapping(value = "/details/{company_id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> companyDetails(@PathVariable int company_id) {
        ClientCompany company = service.companyDetails(company_id);
        if (company != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("company", company);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/edit/{company_id}")
    public ResponseEntity<ClientCompany> getUserEditInfo(@PathVariable int company_id) {
        ClientCompany company = service.companyDetails(company_id);
        if (company != null) {
            return ResponseEntity.ok(company);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

//    //社員削除
//    @PostMapping("/delete")
//    public String companyDelete(int company_id){
//        service.companyDelete(company_id);
//        return "";
//    }
}
