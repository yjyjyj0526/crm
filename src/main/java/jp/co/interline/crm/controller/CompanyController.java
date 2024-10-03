package jp.co.interline.crm.controller;

import jp.co.interline.crm.domain.ClientCompany;
import jp.co.interline.crm.service.CompanyService;
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
public class CompanyController {
    @Autowired
    CompanyService service;
    //顧客社登録
    @PostMapping("/company/register")
    public ResponseEntity<?> registerCompany(@RequestParam("company_name") String company_name,
                                             @RequestParam(value = "CEO_name", required = false) String CEO_name,
                                             @RequestParam(value = "phone_number", required = false) String phone_number,
                                             @RequestParam("post_number") String post_number,
                                             @RequestParam("address") String address,
                                             @RequestParam("homepage") String homepage,
                                             @RequestParam(value = "business_type", required = false) String business_type,
                                             @RequestParam("contract_type") String contract_type,
                                             @RequestParam("register_member_id") String register_member_id){
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

        return ResponseEntity.ok("登録成功");
    }
    //顧客社修正
    @PostMapping("/company/update")
    public ResponseEntity<?> updateCompany(@RequestParam("company_id") int company_id,
                                           @RequestParam("company_name") String company_name,
                                           @RequestParam(value = "CEO_name", required = false) String CEO_name,
                                           @RequestParam(value = "phone_number", required = false) String phone_number,
                                           @RequestParam("post_number") String post_number,
                                           @RequestParam("address") String address,
                                           @RequestParam("homepage") String homepage,
                                           @RequestParam(value = "business_type", required = false) String business_type,
                                           @RequestParam("contract_type") String contract_type,
                                           @RequestParam("update_member_id") String update_member_id){
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

        return ResponseEntity.ok("修正成功");
    }
    //削除
    @PostMapping("/company/delete")
    public String deleteCompany(int company_id){
        service.deleteCompany(company_id);
        return "";
    }
    //顧客社リスト
    @GetMapping("/company/list")
    public ResponseEntity<?> companyList(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(name="categorySelect", required = false) String categorySelect,
                                                @RequestParam(name="searchText", required = false) String searchText,
                                                @RequestParam(name="order", required = false) String order,
                                                @RequestParam(name="orderDirection", required = false, defaultValue = "ASC") String orderDirection,
                                                @RequestParam(name="countPerPage", required = false, defaultValue = "10") int countPerPage,
                                                Model model) {
        int pagePerGroup = 5;

        PagenationUtil navi = service.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
        ArrayList<ClientCompany> list = service.companyList(navi, categorySelect, searchText, order, orderDirection);

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
    @GetMapping("/company/detail/{company_id}")
    public String companyDetails(@PathVariable int company_id, Model model) {
        ClientCompany company = service.companyDetails(company_id);
        model.addAttribute("company", company);
        return "userdetails";
    }
}
