package jp.co.interline.crm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class SalesOpportunity {
    public int opportunity_id;
    public String opportunity_name;
    public int company_id;
    public String close_date;
    public String registration_date;
    public String register_member_id;
    public String update_date;
    public String update_member_id;
    public String notes;
    public int status;
}
