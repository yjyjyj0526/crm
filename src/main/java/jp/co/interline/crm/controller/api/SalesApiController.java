package jp.co.interline.crm.controller.api;

import jp.co.interline.crm.domain.ContactHistory;
import jp.co.interline.crm.domain.SalesOpportunity;
import jp.co.interline.crm.service.SalesService;
import jp.co.interline.crm.util.PagenationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sales")
public class SalesApiController {
    @Autowired
    SalesService salesService;
    //顧客登録
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerSales(@RequestParam("opportunity_name") String opportunity_name,
                                                               @RequestParam("company_id") int company_id,
                                                               @RequestParam(value = "notes", required = false) String notes,
                                                               @RequestParam("register_member_id") String register_member_id){
        Map<String, Object> response = new HashMap<>();
        try {
            LocalDateTime now = LocalDateTime.now();

            SalesOpportunity sales = new SalesOpportunity();
            sales.setOpportunity_name(opportunity_name);
            sales.setCompany_id(company_id);
            sales.setNotes(notes);
            sales.setStatus(0);
            sales.setRegister_member_id(register_member_id);
            sales.setRegistration_date(String.valueOf(now));
            salesService.registerSales(sales);

            response.put("success", true);
            response.put("message", "営業登録成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "営業登録失敗" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    //顧客修正
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateSales(@RequestParam("opportunity_id") int opportunity_id,
                                                             @RequestParam("opportunity_name") String opportunity_name,
                                                             @RequestParam("company_id") int company_id,
                                                             @RequestParam(value = "notes", required = false) String notes,
                                                             @RequestParam(value = "status", required = false) int status,
                                                             @RequestParam("update_member_id") String update_member_id){

        Map<String, Object> response = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();

        SalesOpportunity sales = new SalesOpportunity();

        if(status == 1) {
            sales.setOpportunity_id(opportunity_id);
            sales.setOpportunity_name(opportunity_name);
            sales.setCompany_id(company_id);
            sales.setNotes(notes);
            sales.setStatus(status);
            sales.setClose_date(String.valueOf(now));
            sales.setUpdate_member_id(update_member_id);
            sales.setUpdate_date(String.valueOf(now));
        } else {
            sales.setOpportunity_id(opportunity_id);
            sales.setOpportunity_name(opportunity_name);
            sales.setCompany_id(company_id);
            sales.setNotes(notes);
            sales.setStatus(status);
            sales.setClose_date(null);
            sales.setUpdate_member_id(update_member_id);
            sales.setUpdate_date(String.valueOf(now));
        }
        salesService.updateSales(sales);

        response.put("success", true);
        response.put("message", "営業情報修正成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/json")
    public ResponseEntity<Map<String, Object>> salesList(@RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(name="categorySelect", required = false) String categorySelect,
                                                           @RequestParam(name="searchText", required = false) String searchText,
                                                           @RequestParam(name="order", required = false) String order,
                                                           @RequestParam(name="orderDirection", required = false, defaultValue = "ASC") String orderDirection,
                                                           @RequestParam(name="countPerPage", required = false, defaultValue = "10") int countPerPage) {
        int pagePerGroup = 5;

        if (searchText == null || searchText.isEmpty()) {
            searchText = null;
        }
        PagenationUtil navi = salesService.getPageNavigator(pagePerGroup, countPerPage, page, categorySelect, searchText);
        ArrayList<SalesOpportunity> list = salesService.salesList(navi, categorySelect, searchText, order, orderDirection);

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

    @GetMapping(value = "/details/{opportunity_id}/json", produces = "application/json")
    public ResponseEntity<Map<String, Object>> salesDetails(@PathVariable int opportunity_id) {
        SalesOpportunity sales = salesService.getSalesOpportunityById(opportunity_id);
        List<ContactHistory> contacts = salesService.getContactHistoriesByOpportunityId(opportunity_id);

        if (sales != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("sales", sales);
            response.put("contacts", contacts);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping(value = "/details/{opportunity_id}")
    public String salesDetailsPage(@PathVariable int opportunity_id, Model model) {
        model.addAttribute("opportunity_id", opportunity_id);
        return "contact_list";
    }

//    @GetMapping("/edit/{opportunity_id}")
//    public ResponseEntity<SalesOpportunity> getSalesEditInfo(@PathVariable int opportunity_id) {
//        SalesOpportunity sales = salesService.salesDetails(opportunity_id);
//        if (sales != null) {
//            return ResponseEntity.ok(sales);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//    }
}
