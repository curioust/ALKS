<%@ include file="commons/include.jsp"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<script src="../lib/sweet-alert.min.js"></script>
<link rel="stylesheet" type="text/css" href="../lib/sweet-alert.css">
<style>
.modal {
   margin-top: 75px;
}
</style>
<style>
label {
  clear: both;
  display: block;
  font-size: 0.85em;
  font-weight: bold;
  padding: 0.8em 0 0.2em 0;
  user-select: none;
}

input,textarea,button {
  float: left;
  font-size: 1em;
  padding: 3px 6px;
  margin: 0;
  outline: 0;
  box-shadow: none;
}

::-moz-focus-inner { 
  padding: 0;
  border: 0;
}

button {
  position: relative;
  background-color: #aaa;
  border-radius: 0 3px 3px 0;
  cursor: pointer;
  border: 1px solid #333;
}

.copied::after {
  position: absolute;
  top: 12%;
  right: 110%;
  display: block;
  content: "copied";
  font-size: 0.75em;
  padding: 2px 3px;
  color: #fff;
  background-color: #22a;
  border-radius: 3px;
  opacity: 0;
  will-change: opacity, transform;
  animation: showcopied 1.5s ease;
}

@keyframes showcopied {
  0% {
    opacity: 0;
    transform: translateX(100%);
  }
  70% {
    opacity: 1;
    transform: translateX(0);
  }
  100% {
    opacity: 0;
  }
}
</style>
<script>
$(document).ready(function(){
    $('[data-toggle="popover"]').popover(); 
});
</script>
<script>
/*
Copy text from any appropriate field to the clipboard
By Craig Buckler, @craigbuckler
use it, abuse it, do whatever you like with it!
*/
(function() {

'use strict';

// click events
document.body.addEventListener('click', copy, true);

// event handler
function copy(e) {

// find target element
var 
  t = e.target,
  c = t.dataset.copytarget,
  inp = (c ? document.querySelector(c) : null);
  
// is element selectable?
if (inp && inp.select) {
  
  // select text
  inp.select();

  try {
    // copy text
    document.execCommand('copy');
    inp.blur();
    
    // copied animation
    t.classList.add('copied');
    setTimeout(function() { t.classList.remove('copied'); }, 1500);
  }
  catch (err) {
    alert('please press Ctrl/Cmd+C to copy');
  }
  
}

}

})();



</script>


<script type="text/javascript">
<c:url var="getKeysURL" value="getKeys.htm" />
function showKeyPopup(accountIndex, roleIndex) {
	
	var accountNo = document.getElementById('selectedAccount'+(accountIndex)).value;
	var role = document.getElementById('selectedRole'+(accountIndex)+(roleIndex)).value;
	var e = document.getElementById('duration'+(accountIndex)+(roleIndex));
	var time = e.options[e.selectedIndex].value;
	document.getElementById('accNo').value = accountNo;
	document.getElementById('accRole').value = role;
	document.getElementById('erraccNo').value = accountNo;
	document.getElementById('erraccRole').value = role;
	document.getElementById('tokenTime').value = time;
	
    $.ajax({
    	type : 'GET',
    	url : "${getKeysURL}",
    	data : "selectedAccount="
    			+ accountNo +
    			"&selectedRole="
    			+ role +
    			"&time="
    			+time,
    	success : function(data) {
    		var obj = JSON.parse(data);
    		
    		var accessKey = obj[0];
    		var secretKey = obj[1];
    		var sessionToken = obj[2];
    		var consoleUrl = obj[3];

    		$('#windowscopyid').popover('hide');
    		$('#unixcopyid').popover('hide');

    		document.getElementById('accKey').value = accessKey;
    		document.getElementById('secKey').value = secretKey;
    		document.getElementById('seshToken').value = sessionToken;
    		document.getElementById('conUrl').href = consoleUrl; 
    		
    		document.getElementById('windowscopyid').value =
    				"set AWS_ACCESS_KEY_ID="+ accessKey +"\n"+
    				"set AWS_SECRET_ACCESS_KEY="+ secretKey +"\n"+
    				"export AWS_SESSION_TOKEN=" + sessionToken + "\n" +
    				"set AWS_DEFAULT_REGION=us-east-1"; 
    		
    		document.getElementById('unixcopyid').value =
    				"export AWS_ACCESS_KEY_ID="+ accessKey +"\n"+
    				"export AWS_SECRET_ACCESS_KEY="+ secretKey +"\n"+
    				"export AWS_SESSION_TOKEN=" + sessionToken + "\n" +
    				"export AWS_DEFAULT_REGION=us-east-1"; 
    		
    		
    		//document.getElementById('keyModal').toggle();
    		$("#keyModal").modal()
    		
    	},
    	error : function(xhr, textStatus, thrownError, data) {
    		
    		$("#errorkeyModal").modal()
    		
    	}
    });   
}
</script>

 <div id="keyModal" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg">

    <!-- Modal content-->
    <div id="keypop" class="modal-content">
      <div class="modal-header" style="background-color:#333333">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title" align="center" style="color:white"><strong>Key Information</strong></h4>
        
      </div>
      <div id="help" class="modal-body">
			<table class="table table-striped">
				<tr><td colspan=1>Account No :</td><td colspan=4><input style="width:100%; border:none; background-color: transparent"  id='accNo' readonly ></td></tr>
				<tr><td colspan=1>Role :</td><td colspan=4><input style="width:100%; border:none; background-color: transparent"  id='accRole' readonly ></td></tr>
				<tr><td colspan=1>Expires in (Hours) :</td><td colspan=4><input style="width:100%; border:none; background-color: transparent"  id='tokenTime' readonly></td></tr>
				<tr><td>Access Key    :</td>
					<td colspan=4><input id="accKey" style="width:80%" readonly><button data-copytarget="#accKey">copy</button></td></tr>
				<tr><td>Secret Key    :</td>
					<td colspan=4><input id="secKey" style="width:80%" readonly><button data-copytarget="#secKey">copy</button></td></tr>
				<tr><td>Session Token :</td>
					<td colspan=4><textarea rows="8" cols="50" id="seshToken" readonly></textarea><button data-copytarget="#seshToken">copy</button></td></tr>
				<tr><td>Temporary AWS Console URL :</td>
					<td colspan="4"><a href=' + obj[3] + ' id= 'conUrl' target="_blank">Access AWS console</a></td></tr>
				<tr><td>Set AWS environment variables:</td> <td>Windows:</td> <td> <textarea  style="width:25%"  id="windowscopyid" style="width:80%" readonly></textarea><button   data-copytarget="#windowscopyid">copy</button>
				</td><td>
				Linux/Unix </td><td><textarea  style="width:25%"  id="unixcopyid" style="width:80%" readonly></textarea><button   data-copytarget="#unixcopyid">copy</button></td></tr>	
			</table>
     <div class="modal-footer">
        <button style="float:right" type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
      </div>
    </div>
  </div>
 </div>
 
  <div id="errorkeyModal" class="modal fade" role="dialog">
  <div class="modal-dialog modal-lg">

    <!-- Modal content-->
    <div id="keypop" class="modal-content">
      <div class="modal-header" style="background-color:#333333">
        <button type="button" class="close" data-dismiss="modal">&times;</button>
        <h4 class="modal-title" align="center" style="color:white"><strong>Key Information</strong></h4>       
      </div>
      <div id="help" class="modal-body">
			<table style="align:center" class="table table-striped">
				<tr><td colspan=1>Account No :</td><td colspan=2><input style="width:100%; border:none; background-color: transparent"  id='erraccNo' readonly ></td></tr>
				<tr><td colspan=1>Role :</td><td colspan=2><input style="width:100%; border:none; background-color: transparent"  id='erraccRole' readonly ></td></tr>
			</table>
			<div class="alert alert-danger">
				ERROR: The above account is not configured for the Role requested, please contact system admin.
			</div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>

  </div>
 </div>
 
<form:form action="../key/KeyController.htm" id="KeyForm" method="post"
	modelAttribute="selectedAccount">
	<fieldset>
<div class="container-fluid well">
			<c:choose>
				<c:when test="${(fn:length(acctRoles) > 0)}">

 <table id="myTable" class="table table-hover table-striped tablesorter">
   <thead>
								<tr class='heading'>
									<th>Account</th><th >Role</th><th  class="sorter-false">Session Time</th><th class="sorter-false"></th><tr>
									  </thead>
							<c:forEach var="selectedAccount" items="${acctRoles}"
								varStatus="selectedAccountIndex">
									<form:hidden id="selectedAccount${selectedAccountIndex.count}" path="accountNo" value="${selectedAccount.key}"/>
								<c:forEach var="role" items="${selectedAccount.value}"
									varStatus="selectedRoleIndex">
									<tr
										class="${selectedRoleIndex.index % 2 == 0 ? 'even' : 'odd'}">
										<td>${selectedAccount.key}</td>
										<td>${role} </td>
										<form:hidden id="selectedRole${selectedAccountIndex.count}${selectedRoleIndex.count}" path="role" value="${role}"/>
										
										<!-- Don't need all of these fields -->
										<form:hidden id="accessKey${selectedAccountIndex.count}${selectedRoleIndex.count}" path="accessKey" value=""/>
										<form:hidden id="secretKey${selectedAccountIndex.count}${selectedRoleIndex.count}" path="secretKey" value=""/>
										<form:hidden id="sessionToken${selectedAccountIndex.count}${selectedRoleIndex.count}" path="sessionToken" value=""/>
										<!-- Don't need all of these fields -->
										
										<td><select class="form-control" id="duration${selectedAccountIndex.count}${selectedRoleIndex.count}">
										
										<option value="2">2 Hour</option>
										<option value="6">6 Hour</option>
										<option value="12">12 Hour</option>
										<option value="18">18 Hour</option>
										<option value="24">24 Hour</option>
										<option value="36">36 Hour</option>
										
										</select>
										</td>
										<td><a class="btn btn-default"  onclick="showKeyPopup(${selectedAccountIndex.count},${selectedRoleIndex.count})">
											Generate Keys </a></td>
									</tr>
								</c:forEach>
							</c:forEach>
					</table>
				</c:when>
				<c:when test="${!(fn:length(acctRoles) > 0)}">
								Sorry there are no <u><b>active</b></u> accounts to create temporary tokens, please contact help desk.
				</c:when>
			</c:choose>
		</div>
	</fieldset>
</form:form>