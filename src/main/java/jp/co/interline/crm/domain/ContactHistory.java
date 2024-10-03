package jp.co.interline.crm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContactHistory {
    public int contact_id;
    public int manager_id;
    public String user_id_1;
    public String user_id_2;
    public String user_id_3;
    public String contact_method;
    public String contact_date;
    public String contact_details;
    public String registration_date;
    public String register_member_id;
    public String update_date;
    public String update_member_id;
}
