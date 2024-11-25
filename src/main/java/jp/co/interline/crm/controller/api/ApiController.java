package jp.co.interline.crm.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/zipcode")
    public ResponseEntity<String> getZipcodeInfo(@RequestParam("zipcode") String zipcode) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://zipcloud.ibsnet.co.jp/api/search?zipcode=" + zipcode;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return response;
    }
}
