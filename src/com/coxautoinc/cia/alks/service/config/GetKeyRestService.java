package com.coxautoinc.cia.alks.service.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.coxautoinc.cia.alks.controller.LoginController;
import com.coxautoinc.cia.alks.model.rest.RequestDetails;
import com.coxautoinc.cia.alks.model.rest.ResponseDetails;
import com.coxautoinc.cia.alks.service.delegate.AccountMaster;
import com.coxautoinc.cia.alks.service.delegate.GenerateKeyDelegate;
import com.coxautoinc.cia.alks.service.impl.ADGroupsServiceImpl;

@RestController
public class GetKeyRestService {

	
	/**
	 * EndPoint: https://alks.coxautoinc.com/rest
	 * Resource: /getADGroups/
	 * Method:POST
	 * @param JSON 
	 * {
     *   "userid": "userid",
     *   "password": "password"
	 *	}
	 * @return      Returns All the AD Groups
	 */
	@RequestMapping(value="/getADGroups/", method=RequestMethod.POST)
	public String getADGroups(@RequestBody RequestDetails requestDetails) {
		List<String> adgroups = ADGroupsServiceImpl.authenticate(requestDetails.getUserid(), requestDetails.getPassword());
		return "<keyservice>" + "<input>" +"system id/password"+ "</input>" + "<output>" + adgroups + "</output>" + "</keyservice>";

	}

	/**
	 * EndPoint: https://alks.coxautoinc.com/rest
	 * Resource: /getAccounts/
	 * Method:POST
	 * @param JSON 
	 * {
     *   "userid": "youruserid",
     *   "password": "realpassword",
     *   "adGroup":"AWSG_MGMT_Network"
	 *	}
	 * @return      Returns associated roles for the account 
	 */
	
	@RequestMapping(value="/getAccounts/", method=RequestMethod.POST)
	public String getAccounts(@RequestBody RequestDetails requestDetails) {
		List<String> adgroups = ADGroupsServiceImpl.authenticate(requestDetails.getUserid(), requestDetails.getPassword());
		Map<String,List<String>> accounts = AccountMaster.getAccounts(adgroups);
		
		if(adgroups.contains(requestDetails.getAdGroup())){
		
		return  "<keyservice>" + "<input>" +"system id/password"+ " <adgroup> " + requestDetails.getAdGroup() + " </adgroup> </input>" +"<output>" + "<adgroups>" + adgroups + "</adgroups>" 
				+	"<accounts>" + accounts +"</accounts>"
				+"</output>" +"</keyservice>";
		}else{
		return 	 "<keyservice>" + "<input>" +"system id/password"+ " <adgroup> " + requestDetails.getAdGroup() + " </adgroup> </input>" +"<output>" + "<adgroups>" + adgroups + "</adgroups>" 
				+	"<error> No permissions to the AD group provided </error>"
				+"</output>" +"</keyservice>";
		}

	}


	/**
	 * EndPoint: https://alks.coxautoinc.com/rest
	 * Resource: /getKeys/
	 * Method:POST
	 * @param JSON 
	 * {
     *   "userid": "youruserid",
     *   "password": "realpassword",
     *   "account":"193118345547/ALKS_NP_Admin",
     *   "role":"IAM-1-AdministratorAccessRole-1E8MG35UGPDPH",
     *   "sessionTime":6
	 *	}
	 * @return      Returns associated roles for the account 
	 */
	
	@RequestMapping(value="/getKeys/", method=RequestMethod.POST)
	public ResponseEntity<ResponseDetails> getKeys(@RequestBody RequestDetails requestDetails) {
		List<String> adgroups = ADGroupsServiceImpl.authenticate(requestDetails.getUserid(), requestDetails.getPassword());
		String returnString = null;
		String account = requestDetails.getAccount();
		if(account!=null && account.indexOf('-')>0){
			account = account.substring(0, account.indexOf('-')).trim();
		}
		
		ResponseDetails response = new ResponseDetails();
		response.setAccount(requestDetails.getAccount());
		response.setRole(requestDetails.getRole());
		response.setSessionTime(requestDetails.getSessionTime());
		response.setUserid(requestDetails.getUserid());
		
		//Validate if session time is greater than 36
		if(requestDetails.getSessionTime()>36 || requestDetails.getSessionTime()<2){
			response.setStatusMessage("Session Time Invalid. Should be an integer value between 2 and 36");
			return new ResponseEntity<ResponseDetails> (response, HttpStatus.NOT_ACCEPTABLE);
		}
		
		if(adgroups==null){
			response.setStatusMessage("Invalid userid/password");
			return new ResponseEntity<ResponseDetails> (response, HttpStatus.UNAUTHORIZED);
		}else if(adgroups.size()==0){
			response.setStatusMessage("You are not enrolled for this service. Please contact administrator");
			return new ResponseEntity<ResponseDetails> (response, HttpStatus.UPGRADE_REQUIRED);			
		}else{
			if(adgroups.contains(LoginController.ADMIN_AD_GROUP)){
				adgroups = ADGroupsServiceImpl.getADGroupsByServiceAccount();
			}
			Map<String,List<String>> accounts = AccountMaster.getAccountRolesByADGroups(adgroups);
			if(accounts==null || accounts.size()==0){
				response.setStatusMessage("No permissions, please check with administrator");
				return new ResponseEntity<ResponseDetails> (response, HttpStatus.UNAUTHORIZED);
			}else{
				List accountId = new ArrayList(accounts.get(account));
				ArrayList<String> acctRoles = new ArrayList(accounts.get(account));
				if(acctRoles==null || acctRoles.size()==0){
					returnString = "No permissions for account #" + account + " , please check with administrator";												
				}else{
					if(acctRoles.contains(requestDetails.getRole())){
						String keys[] = GenerateKeyDelegate.getKeys(requestDetails.getUserid(), account, requestDetails.getRole(), requestDetails.getSessionTime().toString());
						response.setAccessKey(keys[0]);
						response.setSecretKey(keys[1]);
						response.setSessionToken(keys[2]);
						response.setStatusMessage("Success");
						//returnString =  "<key>"+ "<accesskey>" + keys[0] +"</accesskey>" +"<secretkey>"+ keys[1] +"</secretkey>"+"<sessionToken>"+ keys[2] +"</sessionToken>"+"</key>";			
					}else{
						returnString = "No "+requestDetails.getRole() +" permission for account #" + account + " , please check with administrator";
						response.setStatusMessage(returnString);
						return new ResponseEntity<ResponseDetails> (response, HttpStatus.UNAUTHORIZED);
					}
				}
			}
		}
		
    	return new ResponseEntity<ResponseDetails> (response, HttpStatus.OK);
	}

	
}
