package com.coxautoinc.cia.alks.service.delegate;

import java.util.List;

import com.coxautoinc.cia.alks.model.ui.ARP;
import com.coxautoinc.cia.alks.model.ui.Account;
import com.coxautoinc.cia.alks.service.config.MessageUtils;
import com.coxautoinc.cia.alks.service.impl.ARPServiceImpl;
import com.coxautoinc.cia.alks.service.impl.AccountServiceImpl;

/**
 * A class that is used to get Role Policy data
 * 
 * @author Shoban Sriram
 * @author Kamen Tsvetkov
 *
 */
public class ARPDeligate {

	/**
	 * Returns all the Role Policies values
	 * 
	 * @return Role Policies
	 */
	public static List<ARP> getAllARP(){
		 AccountServiceImpl accountService = new AccountServiceImpl();
		 ARPServiceImpl arpService = new ARPServiceImpl();
		List<ARP> list = arpService.getAllARPs(); 
		//TODO Check how to reduce number of calls to arpService.
		if(list!=null && list.size()>0){
			for(int i =0;i<list.size();i++){
				ARP arp = list.get(i);
				Account acct = accountService.getAccountByAccountNo(arp.getAccountNo());
				arp.setAccountNo(MessageUtils.getAccountDisplayString(arp.getAccountNo(),acct.getAccountDesc()));
			}
		}
		return list;
	}
	
	
}
