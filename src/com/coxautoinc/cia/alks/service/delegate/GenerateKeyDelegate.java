package com.coxautoinc.cia.alks.service.delegate;

import java.util.List;

import org.apache.log4j.Logger;

import com.coxautoinc.cia.alks.dao.AccountRolePolicyDAO;
import com.coxautoinc.cia.alks.dao.AccountsDAO;
import com.coxautoinc.cia.alks.model.db.AccountRecord;
import com.coxautoinc.cia.alks.model.db.AccountRolePolicyRecord;
import com.coxautoinc.cia.alks.service.impl.CreateKeyImpl;

/**
 * A class that is used to generate keys
 * 
 * @author Shoban Sriram
 * @author Kamen Tsvetkov
 *
 */
public class GenerateKeyDelegate {

	private static Logger logger = Logger.getLogger(GenerateKeyDelegate.class);

	/**
	 * Return an array of keys that are generated for the user given
	 * 
	 * @param userName
	 * @param accountNo
	 * @param role
	 * @param time(mins)
	 * @return array of keys
	 */
	public static String[] getKeys(String userName, String accountNo, String role, String time){

		AccountRolePolicyDAO dao = new AccountRolePolicyDAO();
		AccountsDAO acctDAO = new AccountsDAO();
		
		AccountRecord ar = acctDAO.getAccountByAccountNo(accountNo);
		logger.debug(ar);
		
		List<AccountRolePolicyRecord> arpList = dao.getARPByAccountNoAndAWSRole(accountNo, role);
		
		logger.debug(arpList);
		
		AccountRolePolicyRecord arp = arpList.get(0);
		
		String policy = arp.getPolicy();
		String aMessage[] = null;
		try{
			aMessage = CreateKeyImpl.getKey(userName,ar.getAccessKey(),ar.getSecretKey(),policy,(Integer.parseInt(time)*60*60)); 
		}catch(Exception e){
			aMessage[0] = "Invalid Long term keys";
		}
		logger.info("Keys Generated for user:"+userName +" Role selected:"+role + " for account: "+ accountNo + "using accessKey: "+ ar.getAccessKey() + " duration: "+time);
		logger.info("Access key generated :"+ aMessage[0]);
		
		return aMessage;
	}

	/**
	 * Check to see if the policy entered in the UI is valid
	 * 
	 * @param accountNo
	 * @param policy
	 * @return validation message
	 * @throws Exception
	 */
	public static String[] validatePolicy(String accountNo, String policy) throws Exception{

		AccountsDAO acctDAO = new AccountsDAO();
		
		AccountRecord ar = acctDAO.getAccountByAccountNo(accountNo);
		
		String aMessage[] = CreateKeyImpl.getKey("alkspolicyvalidate",ar.getAccessKey(),ar.getSecretKey(),policy,900); 
			
		logger.debug("Access key generated :"+ aMessage[0]);
		
		return aMessage;
	}

}
