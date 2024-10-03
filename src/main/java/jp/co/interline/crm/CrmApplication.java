package jp.co.interline.crm;

import jp.co.interline.crm.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CrmApplication {
	@Autowired
	private AdminService service;

	public static void main(String[] args) {
		SpringApplication.run(CrmApplication.class, args);
	}

	@PostConstruct
	public synchronized void init() {
		service.createManager();
	}
}
