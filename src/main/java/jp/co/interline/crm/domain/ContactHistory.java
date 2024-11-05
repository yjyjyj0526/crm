package jp.co.interline.crm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContactHistory {
    private int contact_id;
    private int manager_id;
    private int opportunity_id;
    private String user_id_1;
    private String user_id_2;
    private String user_id_3;
    private String contact_method;
    private String contact_date;
    private String contact_details;
    private String registration_date;
    private String register_member_id;
    private String update_date;
    private String update_member_id;
    private int step;
    private int order;
}
