package jp.co.interline.crm.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserList {
    private String user_id;
    private String user_name;
    private String password;
    private String phone_number;
    private String department;
    private int authority;
    private String registration_date;
    private String register_member_id;
    private String update_date;
    private String update_member_id;
    private int enabled;
    private String profile_image_path;
}
