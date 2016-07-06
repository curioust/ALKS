package com.coxautoinc.cia.alks.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.coxautoinc.cia.alks.dao.AccountsDAO;
import com.coxautoinc.cia.alks.model.aws.LongTermKey;
import com.coxautoinc.cia.alks.model.db.AccountRecord;
import com.coxautoinc.cia.alks.model.ui.Account;
import com.coxautoinc.cia.alks.service.AccountService;
import com.coxautoinc.cia.alks.service.aws.MasterIAMService;

/**
 * The implementation of an Account
 * forming/retrieving mechanism
 * 
 * @author Shoban Sriram
 * @author Kamen Tsvetkov
 *
 */
@Service
public class AccountServiceImpl implements AccountService {
	
	private static Logger logger = Logger.getLogger(AccountServiceImpl.class);
	private static AccountsDAO actDao = new AccountsDAO();
	//private static List<Account> accounts = new ArrayList<Account>();
	
	@Override
	public Account getAccountByAccountNo(String accountNo){
		AccountRecord ar = actDao.getAccountByAccountNo(accountNo);
		Account act = new Account();
		act.setAccessKey(ar.getAccessKey());
		act.setAccountDesc(ar.getAccountDesc());
		act.setAccountNo(ar.getAccountNo());
		act.setActive(ar.getActive());
		act.setLastUpdatedBy(ar.getUser());
		act.setLastUpdateTime(ar.getDate());
		act.setSecretKey(ar.getSecretKey());
		act.setRotateDate(ar.getRotateDate());
		act.setRotateBy(ar.getRotateBy());
		return act;
		
	}
	
	@Override
	public List<Account> getAllAccounts() {
		List<Account>  actList = new ArrayList<Account>();
		List<AccountRecord> recList = actDao.getAllAccounts();
		if(recList!=null){
			for(int i=0;i<recList.size();i++){
				AccountRecord ar = recList.get(i);
				Account acct = new Account();
				acct.setAccountNo(ar.getAccountNo());
				acct.setAccountDesc(ar.getAccountDesc());
				acct.setAccessKey(ar.getAccessKey());
				acct.setSecretKey(ar.getSecretKey());
				acct.setLastUpdatedBy(ar.getUser());
				acct.setLastUpdateTime(ar.getDate());
				acct.setActive(ar.getActive());
				acct.setRotateDate(ar.getRotateDate());
				acct.setRotateBy(ar.getRotateBy());
				actList.add(acct);
			}
		}
		
		return actList; 
	}

	@Override
	public List<Account> getAllActiveAccounts() {
		List<Account>  actList = new ArrayList<Account>();
		List<AccountRecord> recList = actDao.getAllAccounts();
		if(recList!=null){
			for(int i=0;i<recList.size();i++){
				AccountRecord ar = recList.get(i);
				Account acct = new Account();
				if(ar.getActive()==1){
				acct.setAccountNo(ar.getAccountNo());
				acct.setAccountDesc(ar.getAccountDesc());
				acct.setAccessKey(ar.getAccessKey());
				acct.setSecretKey(ar.getSecretKey());
				acct.setLastUpdatedBy(ar.getUser());
				acct.setLastUpdateTime(ar.getDate());
				acct.setActive(ar.getActive());
				acct.setRotateDate(ar.getRotateDate());
				acct.setRotateBy(ar.getRotateBy());
				actList.add(acct);
				}
			}
		}
		
		return actList; 
	}

	
	@Override
	public boolean addAccount(Account account, String emailId) {

		MasterIAMService iam = new MasterIAMService(account.getAccessKey(), account.getSecretKey());
		LongTermKey ltk = iam.refreshKeys();
		
		logger.info("Refreshing long term keys" + " user="+emailId + " Account="+account.getAccountNo() + " AccountDesc="+account.getAccountDesc());

		return actDao.saveAccount(account.getAccountNo(), account.getAccountDesc(), ltk.getAccessKey(), ltk.getSecretKey(), emailId);

	}
	
	@Override
	public boolean activateAccount(String account, String emailId) {

		return actDao.activateAccount(account,emailId);

	}	
	
	@Override
	public boolean inactivateAccount(String account, String emailId) {

		return actDao.inactivateAccount(account,emailId);

	}

	@Override
	public boolean regenerateAccountKey(String account, String emailId) {
		
		//get new keys for account
		Account act = getAccountByAccountNo(account);
		MasterIAMService iam = new MasterIAMService(act.getAccessKey(), act.getSecretKey());
		LongTermKey ltk = iam.refreshKeys();
		logger.info("Refreshing long term keys" + " user="+emailId + " Account="+act.getAccountNo() + " AccountDesc="+act.getAccountDesc());
		actDao.saveAccount(account, act.getAccountDesc(), ltk.getAccessKey(), ltk.getSecretKey(), emailId);
		
		return true;

	}

	/**
	 * Returns the long term keys of the account
	 * 
	 * @param accountNo
	 * @return long term keys 
	 */
	public String[] getLongTermKeys(String accountNo){
		String[] keys = actDao.getLongTermKeysByAccount(accountNo);
		return keys;
	}

}
