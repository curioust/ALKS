package com.coxautoinc.cia.alks.service.impl;

import java.text.MessageFormat;
import java.util.*;    

import javax.naming.*;    
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.log4j.Logger;

import com.coxautoinc.cia.alks.service.config.MessageUtils;

public class ADGroupsServiceImpl {
	
	private static Logger logger = Logger.getLogger(ADGroupsServiceImpl.class);

    public static final String CN = "cn";
    public static final String MEMBER_OF = "memberOf";
    public static final String MAIL = "(mail={0})";
    
//    /**
//     * Prepares and returns CN that can be used for AD query
//     * e.g. Converts "CN=**Dev - Test Group" to "**Dev - Test Group"
//     * Converts CN=**Dev - Test Group,OU=Distribution Lists,DC=DOMAIN,DC=com to "**Dev - Test Group"
//     * 
//     * @param cnName
//     * @return CN that can be used for AD query
//     */
//    public static String getCN(String cnName) {
//    	int position=0;
//        if (cnName != null && cnName.toUpperCase().startsWith("CN=")) {
//            cnName = cnName.substring(3);
//            position = cnName.indexOf(',');
//        }
//       
//        if (position == -1) {
//            return cnName;
//        } else {
//            return cnName.substring(0, position);
//        }
//    }
//    
//    /**
//     * Returns whether the target and the candidate
//     * are the same
//     * 
//     * @param target
//     * @param candidate
//     * @return whether target and candidate are the same
//     */
//    public static boolean isSame(String target, String candidate) {
//        if (target != null && target.equalsIgnoreCase(candidate)) {
//            return true;
//        }
//        return false;
//    }

    /**
     * Authenticated a user and returns a list of groups
     * that the user belongs to
     * 
     * @param username
     * @param password
     * @return list of groups
     * @deprecated
     */
    public static List<String> authenticate_email(String username, String password) {

    	logger.debug("Starting.....");
    	if(username==null || password==null){
    		return null; 
    	}
    	boolean isATCUser = !username.endsWith("@manheim.com");

    	List<String> awsList = null;
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        if(isATCUser){
        	env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.url"));        	
        }else{
        	env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.man.url"));        	
        }
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = null;
        String defaultSearchBase = "";
//        String dn = "";
        if(isATCUser){        	
             defaultSearchBase = MessageUtils.getMessage("ldap.service.account.conf.search_base");//"dc=na,dc=autotrader,dc=int";
//             dn = DISTINGUISHED_NAME_AT;
        }else{
             defaultSearchBase = MessageUtils.getMessage("ldap.service.account.man.conf.search_base");
//             dn = DISTINGUISHED_NAME_MAN;
    }

        try {
        	logger.debug("In the try block");
            ctx = new InitialDirContext(env);
            logger.debug("After InitialDirContext");
            
            // userName is SAMAccountName
            SearchResult sr = executeSearchSingleResult(ctx, SearchControls.SUBTREE_SCOPE, defaultSearchBase,
                    MessageFormat.format( MAIL, new Object[] {username}),
                    new String[] {MEMBER_OF}
                    );
            logger.debug("Search results :" + sr.getName());
            
//            String groupCN = getCN(groupDistinguishedName);
            
//            HashMap<String,String> processedUserGroups = new HashMap<String,String>();
//            HashMap<String,String> unProcessedUserGroups = new HashMap<String,String>();

            // Look for and process memberOf
            Attribute memberOf = sr.getAttributes().get(MEMBER_OF);
            
            logger.debug("Member of :"+memberOf);
            NamingEnumeration<?> ne = memberOf.getAll();
            
            awsList = new ArrayList<String>();
            
            while(ne.hasMoreElements()){
            	String member = (String) ne.nextElement();
            	
            	if(member!=null && member.startsWith("CN=AWS")){
            		member = member.substring(3,member.indexOf(","));
            		awsList.add(member);
            	}
            	
            }
            logger.debug("Size in auth method: "+ awsList.size());
            
//            for(int i =0 ; i<awsList.size();i++){
//            	logger.debug("MemberOf:"+ awsList.get(i));
//            }
            
//            
//            if (memberOf != null) {
//                for ( Enumeration e1 = memberOf.getAll() ; e1.hasMoreElements() ; ) {
//                    String unprocessedGroupDN = e1.nextElement().toString();
//                    String unprocessedGroupCN = getCN(unprocessedGroupDN);
//                    // Quick check for direct membership
//                    if (isSame (groupCN, unprocessedGroupCN) && isSame (groupDistinguishedName, unprocessedGroupDN)) {
//                        logger.info(username + " is authorized.");
//                        return true;
//                    } else {
//                        unProcessedUserGroups.put(unprocessedGroupDN, unprocessedGroupCN);
//                    }
//                }
//                if (userMemberOf(ctx, defaultSearchBase, processedUserGroups, unProcessedUserGroups, groupCN, groupDistinguishedName)) {
//                    logger.info(username + " is authorized.");
//                    return true;
//                }
//            }

            return awsList;
        } catch (AuthenticationException e) {
        	logger.info(username + " is NOT authenticated");
            return null;
        } catch (NamingException e) {
        	logger.info("Unable to connect");
        	logger.fatal(e.getStackTrace());
        	 return null; 
        } catch(Exception e){
        	logger.error(e.getMessage() + " Returning null for AD groups");
        	return null;
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    
                }
            }
        }
    }

    /**
     * Authenticated a user and returns a list of groups
     * that the user belongs to
     * 
     * @param username
     * @param password
     * @return list of groups
     * @deprecated
     */
    public static List<String> authenticateTrying(String sAMAccountName, String password) {

    	logger.debug("Starting.....");
    	if(sAMAccountName==null || password==null){
    		return null; 
    	}
    	boolean isATCUser = true; //!username.endsWith("@manheim.com");

    	List<String> awsList = null;
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        if(isATCUser){
        	env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.url"));        	
        }else{
        	env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.man.url"));        	
        }
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, sAMAccountName);
        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = null;
        String defaultSearchBase = "";
        if(isATCUser){        	
             defaultSearchBase = MessageUtils.getMessage("ldap.service.account.conf.search_base");//"dc=na,dc=autotrader,dc=int";
        }else{
             defaultSearchBase = MessageUtils.getMessage("ldap.service.account.man.conf.search_base");
    }

        try {
        	logger.debug("In the try block");
            ctx = new InitialDirContext(env);
            logger.debug("After InitialDirContext");
            
            // userName is SAMAccountName
            SearchResult sr = executeSearchSingleResult(ctx, SearchControls.SUBTREE_SCOPE, defaultSearchBase,
                    MessageFormat.format( "sAMAccountName", new Object[] {sAMAccountName}),
                    new String[] {MEMBER_OF}
                    );
            logger.debug("Search results :" + sr.getName());
            
            // Look for and process memberOf
            Attribute memberOf = sr.getAttributes().get(MEMBER_OF);
            
            logger.debug("Member of :"+memberOf);
            NamingEnumeration<?> ne = memberOf.getAll();
            
            awsList = new ArrayList<String>();
            
            while(ne.hasMoreElements()){
            	String member = (String) ne.nextElement();
            	
            	if(member!=null && member.startsWith("CN=AWS")){
            		member = member.substring(3,member.indexOf(","));
            		awsList.add(member);
            	}
            	
            }
            logger.debug("Size in auth method: "+ awsList.size());

            return awsList;
        } catch (AuthenticationException e) {
        	logger.info(sAMAccountName + " is NOT authenticated");
            return null;
        } catch (NamingException e) {
        	logger.info("Unable to connect");
        	logger.fatal(e.getStackTrace());
        	 return null; 
        } catch(Exception e){
        	logger.error(e.getMessage() + " Returning null for AD groups");
        	return null;
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    
                }
            }
        }
    }

    
    public static List<String> authenticate(String userName, String userPassword) {
        Hashtable<String, String> env = new Hashtable<String, String>();

    	List<String> awsList = null;
    	
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.url")); 

        env.put(Context.SECURITY_AUTHENTICATION, "simple");	
        env.put(Context.SECURITY_PRINCIPAL, "autotrader\\"+userName);
        env.put(Context.SECURITY_CREDENTIALS, userPassword);
        boolean isATCUser = true;
        DirContext ctx = null;
        try {
           ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            try {
            	env.put(Context.SECURITY_PRINCIPAL, "man\\"+userName);
                env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.man.url"));             	
                ctx = new InitialDirContext(env);
                isATCUser = false;
             } catch (AuthenticationException ae) {
             	logger.info(userName + " is NOT authenticated:" +ae.getMessage());
                return null;
            } catch (NamingException ne) {
            	logger.info("Unable to connect:"+ne.getMessage());
            	logger.fatal(ne.getStackTrace());
            	 return null; 
            } catch(Exception e1){
            	logger.error(e1.getMessage() + " Returning null for AD groups");
            	return null;
            }
            finally {
            	//Do not close ctx. we still need it to query.
            }
        }

        NamingEnumeration<SearchResult> results = null;
        SearchResult sr = null;
        
        try {
           SearchControls controls = new SearchControls();
           controls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search Entire Subtree
           controls.setReturningAttributes(new String[] {MEMBER_OF});

           
           //controls.setCountLimit(1);   //Sets the maximum number of entries to be returned as a result of the search
           //controls.setTimeLimit(5000); // Sets the time limit of these SearchControls in milliseconds

           String searchString = "(&(objectClass=user)(objectcategory=person)(samaccountname=" + userName + "))";
           String defaultSearchBase = "";
           
           if(isATCUser){        	
                defaultSearchBase = MessageUtils.getMessage("ldap.service.account.conf.search_base");//"dc=na,dc=autotrader,dc=int";
           }else{
                defaultSearchBase = MessageUtils.getMessage("ldap.service.account.man.conf.search_base");
           }
           
           results = ctx.search(defaultSearchBase, searchString, controls);
           
           // Loop through the search results
           while (results.hasMoreElements()) {
               sr = (SearchResult) results.next();
               break;
           }
           // Look for and process memberOf
           Attribute memberOf = sr.getAttributes().get(MEMBER_OF);
           
           logger.debug("Member of :"+memberOf);
           NamingEnumeration<?> ne = memberOf.getAll();
           
           awsList = new ArrayList<String>();
           
           while(ne.hasMoreElements()){
           	String member = (String) ne.nextElement();
           	
           	if(member!=null && member.startsWith("CN=AWS")){
           		member = member.substring(3,member.indexOf(","));
           		awsList.add(member);
           	}
           	
           }
           logger.debug("Size in auth method: "+ awsList.size());

           return awsList;

        } catch (AuthenticationException e) { // Invalid Login
        	logger.error(e.getMessage());
        	return null;
        } catch (NameNotFoundException e) { // The base context was not found.
        	logger.error(e.getMessage());
        	return null;
        } catch (SizeLimitExceededException e) {
        	logger.error(e.getMessage());
        	return null;
        } catch (NamingException e) {
        	logger.error(e.getMessage());
           return null;
        } finally {

           if (results != null) {
              try { results.close(); } catch (Exception e) { /* Do Nothing */ }
           }

           if (ctx != null) {
              try { ctx.close(); } catch (Exception e) { /* Do Nothing */ }
           }
        }
    }

    /**
     * 
     * @deprecated
     * @param userName
     * @param userPassword
     * @return
     */
   
    public static List<String> authenticate_userPrincipalName(String userName, String userPassword) {
        Hashtable<String, String> env = new Hashtable<String, String>();

    	List<String> awsList = null;
    	
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

        env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.url")); 
        // To get rid of the PartialResultException when using Active Directory
        env.put(Context.REFERRAL, "follow");
        // Needed for the Bind (User Authorized to Query the LDAP server) 
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, userName);
        env.put(Context.SECURITY_CREDENTIALS, userPassword);
        boolean isATCUser = true;
        DirContext ctx;
        
        try {
           ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            try {
            	//Manheim
                env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.man.url"));             	
                ctx = new InitialDirContext(env);
                isATCUser = false;
             } catch (NamingException e2) {
                throw new RuntimeException(e2);
             }
        }

        NamingEnumeration<SearchResult> results = null;
        SearchResult sr = null;
        
        try {
           SearchControls controls = new SearchControls();
           controls.setSearchScope(SearchControls.SUBTREE_SCOPE); // Search Entire Subtree
           controls.setReturningAttributes(new String[] {MEMBER_OF});
           controls.setCountLimit(1);   //Sets the maximum number of entries to be returned as a result of the search
           controls.setTimeLimit(5000); // Sets the time limit of these SearchControls in milliseconds

           String searchString = "(&(objectCategory=user)(userPrincipalName=" + userName + "))";
           String defaultSearchBase = "";
           
           if(isATCUser){        	
                defaultSearchBase = MessageUtils.getMessage("ldap.service.account.conf.search_base");//"dc=na,dc=autotrader,dc=int";
           }else{
                defaultSearchBase = MessageUtils.getMessage("ldap.service.account.man.conf.search_base");
           }

           
           results = ctx.search(defaultSearchBase, searchString, controls);

           
           // Loop through the search results
           while (results.hasMoreElements()) {
               sr = (SearchResult) results.next();
               break;
           }
           // Look for and process memberOf
           Attribute memberOf = sr.getAttributes().get(MEMBER_OF);
           
           logger.debug("Member of :"+memberOf);
           NamingEnumeration<?> ne = memberOf.getAll();
           
           awsList = new ArrayList<String>();
           
           while(ne.hasMoreElements()){
           	String member = (String) ne.nextElement();
           	
           	if(member!=null && member.startsWith("CN=AWS")){
           		member = member.substring(3,member.indexOf(","));
           		awsList.add(member);
           	}
           	
           }
           logger.debug("Size in auth method: "+ awsList.size());

           return awsList;

        } catch (AuthenticationException e) { // Invalid Login
        	return null;

        } catch (NameNotFoundException e) { // The base context was not found.
        	return null;
        } catch (SizeLimitExceededException e) {
            throw new RuntimeException("LDAP Query Limit Exceeded, adjust the query to bring back less records", e);
        } catch (NamingException e) {
           throw new RuntimeException(e);
        } finally {

           if (results != null) {
              try { results.close(); } catch (Exception e) { /* Do Nothing */ }
           }

           if (ctx != null) {
              try { ctx.close(); } catch (Exception e) { /* Do Nothing */ }
           }
        }
    }

    
    /**
     * This method will be used to authenticated a user for multiple domains and returns a list of groups
     * that the user belongs to. The groups should exists in those AD groups if it needs to be returned.
     * 
     * @deprecated
     * @param username
     * @param password
     * @return list of AD groups
     */
    public static List<String> authenticateAllDomains(String username, String password) {


    	if(username==null || password==null){
    		return null; 
    	}
    	boolean isATCUser = !username.endsWith("@manheim.com");

    	List<String> awsList = null;
        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        if(isATCUser){
        	env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.url"));        	
        }else{
        	env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.man.url"));        	
        }
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = null;
        String defaultSearchBase = "";
//        String dn = "";
        if(isATCUser){        	
             defaultSearchBase = MessageUtils.getMessage("ldap.service.account.conf.search_base");//"dc=na,dc=autotrader,dc=int";
//             dn = DISTINGUISHED_NAME_AT;
        }else{
             defaultSearchBase = MessageUtils.getMessage("ldap.service.account.man.conf.search_base");
//             dn = DISTINGUISHED_NAME_MAN;
    }

        try {
        	logger.debug("In the try block");
            ctx = new InitialDirContext(env);
            logger.debug("After InitialDirContext");
            
            // userName is SAMAccountName
            SearchResult sr = executeSearchSingleResult(ctx, SearchControls.SUBTREE_SCOPE, defaultSearchBase,
                    MessageFormat.format( MAIL, new Object[] {username}),
                    new String[] {MEMBER_OF}
                    );
            logger.debug("Search results :" + sr.getName());
            
//            String groupCN = getCN(groupDistinguishedName);
            
//            HashMap<String,String> processedUserGroups = new HashMap<String,String>();
//            HashMap<String,String> unProcessedUserGroups = new HashMap<String,String>();

            // Look for and process memberOf
            Attribute memberOf = sr.getAttributes().get(MEMBER_OF);
            
            logger.debug("Member of :"+memberOf);
            NamingEnumeration<?> ne = memberOf.getAll();
            
            awsList = new ArrayList<String>();
            
            while(ne.hasMoreElements()){
            	String member = (String) ne.nextElement();
            	
            	if(member!=null && member.startsWith("CN=AWS")){
            		member = member.substring(3,member.indexOf(","));
            		awsList.add(member);
            	}
            	
            }
            logger.debug("Size in auth method: "+ awsList.size());
            
//            for(int i =0 ; i<awsList.size();i++){
//            	logger.debug("MemberOf:"+ awsList.get(i));
//            }
            
//            
//            if (memberOf != null) {
//                for ( Enumeration e1 = memberOf.getAll() ; e1.hasMoreElements() ; ) {
//                    String unprocessedGroupDN = e1.nextElement().toString();
//                    String unprocessedGroupCN = getCN(unprocessedGroupDN);
//                    // Quick check for direct membership
//                    if (isSame (groupCN, unprocessedGroupCN) && isSame (groupDistinguishedName, unprocessedGroupDN)) {
//                        logger.info(username + " is authorized.");
//                        return true;
//                    } else {
//                        unProcessedUserGroups.put(unprocessedGroupDN, unprocessedGroupCN);
//                    }
//                }
//                if (userMemberOf(ctx, defaultSearchBase, processedUserGroups, unProcessedUserGroups, groupCN, groupDistinguishedName)) {
//                    logger.info(username + " is authorized.");
//                    return true;
//                }
//            }

            return awsList;
        } catch (AuthenticationException e) {
        	logger.info(username + " is NOT authenticated");
            return null;
        } catch (NamingException e) {
        	logger.info("Unable to connect");
        	logger.fatal(e.getStackTrace());
        	 return null; 
        } catch(Exception e){
        	logger.error(e.getMessage() + " Returning null for AD groups");
        	return null;
        }
        finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    
                }
            }
        }
    }

    
//    /**
//     * Checks whether a user is a member of a group
//     * 
//     * @param ctx
//     * @param searchBase
//     * @param processedUserGroups
//     * @param unProcessedUserGroups
//     * @param groupCN
//     * @param groupDistinguishedName
//     * @return whether or not the user is a member
//     * @throws NamingException
//     */
//    public static boolean userMemberOf(DirContext ctx, String searchBase, HashMap<String,String> processedUserGroups, HashMap<String,String> unProcessedUserGroups, String groupCN, String groupDistinguishedName) throws NamingException {
//        HashMap<String,String> newUnProcessedGroups = new HashMap<String,String>();
//        for (Iterator<String> entry = unProcessedUserGroups.keySet().iterator(); entry.hasNext();) {
//            String  unprocessedGroupDistinguishedName = (String) entry.next();
//            String unprocessedGroupCN = (String)unProcessedUserGroups.get(unprocessedGroupDistinguishedName);
//            if ( processedUserGroups.get(unprocessedGroupDistinguishedName) != null) {
//                logger.debug("Found  : " + unprocessedGroupDistinguishedName +" in processedGroups. skipping further processing of it..." );
//                // We already traversed this.
//                continue;
//            }
//            if (isSame (groupCN, unprocessedGroupCN) && isSame (groupDistinguishedName, unprocessedGroupDistinguishedName)) {
//            	logger.debug("Found Match DistinguishedName : " + unprocessedGroupDistinguishedName +", CN : " + unprocessedGroupCN );
//                return true;
//            }
//        }
//
//        for (Iterator<String> entry = unProcessedUserGroups.keySet().iterator(); entry.hasNext();) {
//            String  unprocessedGroupDistinguishedName = (String) entry.next();
//            String unprocessedGroupCN = (String)unProcessedUserGroups.get(unprocessedGroupDistinguishedName);
//
//            processedUserGroups.put(unprocessedGroupDistinguishedName, unprocessedGroupCN);
//
//            logger.debug("Search Base: "+searchBase);
//            logger.debug("UnprocessedGroupCN: "+unprocessedGroupCN);
//            logger.debug("");
//            logger.debug("");
//                      
//            // Fetch Groups in unprocessedGroupCN and put them in newUnProcessedGroups
//            NamingEnumeration ns = executeSearch(ctx, SearchControls.SUBTREE_SCOPE, searchBase,
//                    MessageFormat.format( SEARCH_GROUP_BY_GROUP_CN_AT, new Object[] {unprocessedGroupCN}),
//                    new String[] {CN, DISTINGUISHED_NAME_AT, MEMBER_OF});
//
//            // Loop through the search results
//            while (ns.hasMoreElements()) {
//                SearchResult sr = (SearchResult) ns.next();
//
//                // Make sure we're looking at correct distinguishedName, because we're querying by CN
//                String userDistinguishedName = sr.getAttributes().get(DISTINGUISHED_NAME_AT).get().toString();
//                if (!isSame(unprocessedGroupDistinguishedName, userDistinguishedName)) {
//                	logger.debug("Processing CN : " + unprocessedGroupCN + ", DN : " + unprocessedGroupDistinguishedName +", Got DN : " + userDistinguishedName +", Ignoring...");
//                    continue;
//                }
//
//                logger.debug("Processing for memberOf CN : " + unprocessedGroupCN + ", DN : " + unprocessedGroupDistinguishedName);
//                // Look for and process memberOf
//                Attribute memberOf = sr.getAttributes().get(MEMBER_OF);
//                if (memberOf != null) {
//                    for ( Enumeration e1 = memberOf.getAll() ; e1.hasMoreElements() ; ) {
//                        String unprocessedChildGroupDN = e1.nextElement().toString();
//                        String unprocessedChildGroupCN = getCN(unprocessedChildGroupDN);
//                        logger.debug("Adding to List of un-processed groups : " + unprocessedChildGroupDN +", CN : " + unprocessedChildGroupCN);
//                        newUnProcessedGroups.put(unprocessedChildGroupDN, unprocessedChildGroupCN);
//                    }
//                }
//            }
//        }
//        if (newUnProcessedGroups.size() == 0) {
//        	logger.debug("newUnProcessedGroups.size() is 0. returning false...");
//            return false;
//        }
//
//        //  process unProcessedUserGroups
//        return userMemberOf(ctx, searchBase, processedUserGroups, newUnProcessedGroups, groupCN, groupDistinguishedName);
//    }
 
    /**
     * Fetches groups given certain attributes
     * 
     * @param ctx
     * @param searchScope
     * @param searchBase
     * @param searchFilter
     * @param attributes
     * @return groups
     * @throws NamingException
     */
    private static NamingEnumeration<SearchControls> executeSearch(DirContext ctx, int searchScope,  String searchBase, String searchFilter, String[] attributes) throws NamingException {
        // Create the search controls
        SearchControls searchCtls = new SearchControls();

        // Specify the attributes to return
        if (attributes != null) {
            searchCtls.setReturningAttributes(attributes);
        }

        // Specify the search scope
        searchCtls.setSearchScope(searchScope);
        logger.debug("In executeSearch");
        // Search for objects using the filter
        NamingEnumeration result = ctx.search(searchBase, searchFilter,searchCtls);
        return result;
    }

    /**
     * Fetches a group given certain attributes
     * 
     * @param ctx
     * @param searchScope
     * @param searchBase
     * @param searchFilter
     * @param attributes
     * @return group
     * @throws NamingException
     */
    private static SearchResult executeSearchSingleResult(DirContext ctx, int searchScope,  String searchBase, String searchFilter, String[] attributes) throws NamingException {
        NamingEnumeration result = executeSearch(ctx, searchScope,  searchBase, searchFilter, attributes);

        SearchResult sr = null;
        // Loop through the search results
        while (result.hasMoreElements()) {
            sr = (SearchResult) result.next();
            break;
        }
        return sr;
    }
    
//    //TODO Do we need to cache this list? How frequently are we going to change the groups.
//    /**
//     * Returns a list of AD Groups using the 
//     * service account
//     * 
//     * @return list of AD Groups
//     */
//    public static List<String> getGroupsByServiceAccount() {
//    	
//    	String username = MessageUtils.getMessage("ldap.service.account.username");
//      	String password = MessageUtils.getMessage("ldap.service.account.password");
//        List<String> awsAllGroups = null;  	
//    	
//    	logger.debug("Starting.....");
//    	if(username==null || password==null){
//    		return new ArrayList<String>(); 
//    	}
//        Hashtable<String,String> env = new Hashtable<String,String>();
//        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
//        //env.put(Context.PROVIDER_URL, "ldap://adcp.autotrader.int:389");
//        env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.url"));
//        env.put(Context.SECURITY_AUTHENTICATION, "simple");
//        env.put(Context.SECURITY_PRINCIPAL, username);
//        env.put(Context.SECURITY_CREDENTIALS, password);
//        DirContext ctx = null;
//        //String defaultSearchBase = "ou=app,dc=na,dc=autotrader,dc=int";
//        //String groupDistinguishedName = "dc=na,dc=autotrader,dc=int";
//
//        try {
//        	logger.debug("Getting AWS groups using service account");
//            ctx = new InitialDirContext(env);
//            logger.debug("After InitialDirContext");
//     	   SearchControls controls = new SearchControls();
//    	   controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
//    	   String attr[] = {"samaccountname"};
//    	   controls.setReturningAttributes(attr);
//    	   //ou=aws,ou=app,dc=na,dc=autotrader,dc=int
//    	   NamingEnumeration<SearchResult> result =  ctx.search("ou=aws,ou=app,dc=na,dc=autotrader,dc=int","(&(objectCategory=group)(objectClass=group))", controls);
//    	   awsAllGroups = new ArrayList<String>();
//    	   
//    	     SearchResult sr = null;
//    	        // Loop through the search results
//    	        while (result.hasMoreElements()) {
//    	            sr = (SearchResult) result.next();
//    	            awsAllGroups.add(sr.getName().substring(3));
//    	        }  
//    	        
//            return awsAllGroups;
//        } catch (AuthenticationException e) {
//        	logger.error("Service account details are not correct."+e.getMessage());
//            return null;
//        } catch (NamingException e) {
//        	logger.error("Unable to connect"+e.getMessage());
//        	 return new ArrayList<String>(); 
//        } finally {
//            if (ctx != null) {
//                try {
//                    ctx.close();
//                } catch (NamingException e) {
//                    
//                }
//            }
//        }
//    }
    
    /**
     * This method always gets AD groups from autotrader domain. As we have a logic in the
     * getADGroups to check if the username ends with '@manheim'. 
     * 
     * @return
     */
    public static List<String> getADGroupsByServiceAccount(){
    	String username = MessageUtils.getMessage("ldap.service.account.username");
      	String password = MessageUtils.getMessage("ldap.service.account.password");
      	return getADGroups(username, password,true);
    }
    
    //TODO Do we need to cache this list? How frequently are we going to change the groups.
    /**
     * Returns a list of AD Groups using the 
     * service account
     * 
     * @return list of AD Groups
     */
    public static List<String> getADGroups(String username,String password,boolean isATGAD) {
 

        List<String> awsAllGroups = null;  	
    	
    	logger.debug("Starting.....");

        Hashtable<String,String> env = new Hashtable<String,String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        //env.put(Context.PROVIDER_URL, "ldap://adcp.autotrader.int:389");
        if(isATGAD){
            env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.url"));        	
        }else{
        	env.put(Context.PROVIDER_URL, MessageUtils.getMessage("ldap.service.account.man.url"));
        }
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);
        DirContext ctx = null;
        //String defaultSearchBase = "ou=app,dc=na,dc=autotrader,dc=int";
        //String groupDistinguishedName = "dc=na,dc=autotrader,dc=int";

        try {
        	logger.debug("Getting AWS groups using service account");
            ctx = new InitialDirContext(env);
            logger.debug("After InitialDirContext");
     	   SearchControls controls = new SearchControls();
    	   controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    	   String attr[] = {"samaccountname"};
    	   controls.setReturningAttributes(attr);
    	   //ou=aws,ou=app,dc=na,dc=autotrader,dc=int
    	   NamingEnumeration<SearchResult> result = null;
    	   if(isATGAD){
        	   result =  ctx.search("ou=aws,ou=app,dc=na,dc=autotrader,dc=int","(&(objectCategory=group)(objectClass=group))", controls);    		   
    	   }else{
    		   result =  ctx.search("OU=Privileged Groups,OU=_Special Access,DC=man,DC=co","(&(objectCategory=group)(sAMAccountName=AWS*))", controls);
    	   }
    	    awsAllGroups = new ArrayList<String>();
    	   
    	     SearchResult sr = null;
    	        // Loop through the search results
    	        while (result.hasMoreElements()) {
    	            sr = (SearchResult) result.next();
    	            awsAllGroups.add(sr.getName().substring(3));
    	        }  
    	        
            return awsAllGroups;
        } catch (AuthenticationException e) {
        	logger.error("Service account details are not correct."+e.getMessage());
            return null;
        } catch (NamingException e) {
        	logger.error("Unable to connect"+e.getMessage());
        	 return new ArrayList<String>(); 
        } finally {
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (NamingException e) {
                    
                }
            }
        }
    }
    
    public static boolean areADGropsSame(){
    	List<String>  manheimList = getADGroups(MessageUtils.getMessage("ldap.service.account.man.username"), MessageUtils.getMessage("ldap.service.account.man.password"),false);
    	List<String>  atgList = getADGroups(MessageUtils.getMessage("ldap.service.account.username"),MessageUtils.getMessage("ldap.service.account.password"),true);
        Collection<String> similar = new HashSet<String>(manheimList);
        similar.retainAll(atgList);
        if((similar.size()==atgList.size()) && (atgList.size()==manheimList.size())){
        	return true;
        }
        return false;
    }

    
}
