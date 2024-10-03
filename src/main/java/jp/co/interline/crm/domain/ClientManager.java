package jp.co.interline.crm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientManager {
    private String manager_id;
    private String manager_name;
    private int company_id;
    private String department;
    private String position;
    private String manager_age;
    private String manager_mail;
    private String phone_number_1;
    private String phone_number_2;
    private String registration_date;
    private String register_member_id;
    private String update_date;
    private String update_member_id;
}
