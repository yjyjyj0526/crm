package jp.co.interline.crm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientCompany {
    private int company_id;
    private String company_name;
    private String CEO_name;
    private String phone_number;
    private String post_number;
    private String address;
    private String detail_address;
    private String homepage;
    private String business_type;
    private String contract_type;
    private String registration_date;
    private String register_member_id;
    private String update_date;
    private String update_member_id;
}
