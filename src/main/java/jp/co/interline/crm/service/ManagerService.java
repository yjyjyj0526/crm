package jp.co.interline.crm.service;

import jp.co.interline.crm.domain.ClientManager;
import jp.co.interline.crm.util.PagenationUtil;

import java.util.ArrayList;

public interface ManagerService {
    public int registerManager(ClientManager manager);
    public int updateManager(ClientManager manager);
    public void deleteManager(int manager_id);
    public ArrayList<ClientManager> managerList(PagenationUtil pagenation, String categorySelect, String searchText, String order, String orderDirection);
    public PagenationUtil getPageNavigator(int pagePerGroup, int countPerPage, int page, String categorySelect, String searchText);
    ClientManager managerDetails(int company_id);
}
