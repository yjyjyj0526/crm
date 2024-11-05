package jp.co.interline.crm.service;

import jp.co.interline.crm.dao.ContactDAO;
import jp.co.interline.crm.domain.ContactHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ContactService {

    @Autowired
    private ContactDAO dao;

    public void registerContact(ContactHistory contact) {
        int opportunity_id = contact.getOpportunity_id();
        int step = contact.getStep();

        Integer maxOrder = dao.getMaxOrder(opportunity_id, step);

        int newOrder = (maxOrder == null ? 0 : maxOrder) + 1;
        contact.setOrder(newOrder);

        dao.registerContact(contact);
    }
}