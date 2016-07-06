package com.coxautoinc.cia.alks.dao;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.coxautoinc.cia.alks.model.db.AccountRecord;
import com.coxautoinc.cia.alks.model.db.RestRecord;

/**
 * Database class to retrieve and store account data
 * 
 * @author Shoban Sriram
 *
 */
public class RestDAO extends BaseEncryptDAO {

	private static Logger logger = Logger.getLogger(RestDAO.class);

	static String ENCODEDID = "encodedid";
	static String USERID = "userid";
	static String ACTIVE = "active";
	static String ROTATE_DATE = "rotateDate";
	
	/**
	 * Return a list of Rest records from the database
	 * 
	 * @return a list of Rest records
	 */
	public List<RestRecord> getAllKeys(){
	    DynamoDBScanExpression expression = new DynamoDBScanExpression();
	    logger.info("DB getAllKeys");
		return getMapper().scan(RestRecord.class, expression);
	}
	
	/**
	 * Return a list of active Rest records from the database
	 * 
	 * @return a list of active Rest records
	 */
	public List<RestRecord> getAllActiveRestRecords(){
	    DynamoDBScanExpression expression = new DynamoDBScanExpression();
	    expression.addFilterCondition(ACTIVE, 	                
	    		new Condition()
        .withComparisonOperator(ComparisonOperator.EQ)
        .withAttributeValueList(new AttributeValue().withS("1")));
	    logger.info("DB getAllActiveRestRecords");
		return getMapper().scan(RestRecord.class, expression);
	}
	
	/**
	 * Return an REST record that corresponds to the
	 * encodedid passed into the parameter
	 * 
	 * @param encodedid
	 * @return an Rest record
	 */
	public RestRecord getRESTRecordByEncodedId(String encodedid){
	       DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
	        scanExpression.addFilterCondition(ENCODEDID, 
	                new Condition()
	                   .withComparisonOperator(ComparisonOperator.EQ)
	                   .withAttributeValueList(new AttributeValue().withS(encodedid)));
	    
	     List<RestRecord> list = getMapper().scan(RestRecord.class, scanExpression);
	     logger.debug("DB encodedid:"+encodedid);
	     if(list!=null && list.size()>0){
	    	 return list.get(0);
	     }
		return null;
	}
	
	/**
	 * Save an Rest record to the database
	 * Note: This method should be called when new rest ids are created. 
	 * 
	 * @param userid
	 * @param acctDesc
	 * @param accessKey
	 * @param secretKey
	 * @param lastUpdatedBy
	 * @return whether or not save succeeded
	 */
	public boolean saveAccount(String userid, String encodedid, String active){
		
//		RestRecord actRec = new RestRecord();
//		
//		actRec.setUser(userid);
//		actRec.setAccountDesc(encodedid);
//		actRec.setAccessKey(accessKey);
//		actRec.setSecretKey(secretKey);
//		actRec.setUser(lastUpdatedBy);
//		actRec.setDate(DATE_FORMATTER.format(new Date()));
//		actRec.setActive(1);
//		actRec.setRotateDate(DATE_FORMATTER.format(new Date()));
//		actRec.setRotateBy(lastUpdatedBy);
//		saveAccounts(actRec);
		
		return true;
	}
	
	
}