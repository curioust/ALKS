package com.coxautoinc.cia.alks.model.db;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.encryption.DoNotTouch;

/**
 * A class representing an Account record
 * 
 * @author Shoban Sriram
 * @author Kamen Tsvetkov
 *
 */
@DynamoDBTable(tableName = "com.coxautoinc.cia.alks.table.rest.application")
public class RestRecord extends BaseModel {
		
	private String encodedId;
	private String userid;
	private String active;
	private String rotateDate;
	 
	/**
	 * Returns an Account Record's account number
	 * Note: Not encrypted because it is a hashkey
	 * 
	 * @return accountNo
	 */
	@DynamoDBHashKey (attributeName = "encodedid")
	public String getEncodedId() {
		return encodedId;
	}
}
