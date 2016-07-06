<%@ include file="commons/include.jsp"%>
<script src="../lib/sweet-alert.min.js"></script>
<link rel="stylesheet" type="text/css" href="../lib/sweet-alert.css">
<link rel="stylesheet" type="text/css" href="../css/select2.css">
<script type="text/javascript" src="../js/select2.full.js"></script>

<c:url var="findRolesURL" value="getRoles.htm" />
<c:url var="findAccountIdURL" value="getAccountId.htm" />
<script type="text/javascript">
	$(document)
			.ready(
					function() {
						$('#accountNo')
								.change(
										function() {
											$
													.ajax({
														async: false,
														type : 'GET',
														url : "${findRolesURL}",
														data : "accountNumber="
																+ $(this).val(),
														success : function(data) {
															var obj = JSON
																	.parse(data);
															var html = '<option value="">Select Role</option>';
															var len = obj.length;
															for (var i = 0; i < len; i++) {
																html += '<option value="' + obj[i] + '">'
																		+ obj[i]
																		+ '</option>';
															}
															html += '</option>';

															$('#roleField').html(
																	html);
														},
														error : function(e) {
															alert('Error in Processing, please try again later or contact system admin if error persists');
														}
													});
										});
						$('#accountNo').select2({
							placeholder: "Select Account"
						});
						
					});

	$(document)
			.ready(
					function() {
						$('#roleField')
								.change(
										function() {
											$
													.ajax({
														async: false,
														type : 'GET',
														url : "${findAccountIdURL}",
														data : "role="
																+ $(this).val() +
																"&accountNumber="
																+ $('#accountNo').val(),
														success : function(data) {
															var obj = JSON
															.parse(data);															
															$('#accountId').val(obj[0]);
														},
														error : function(e) {
															alert('Error in Processing, please try again later or contact system admin if error persists');
														}
													});
										});
						$('#roleField').select2({
							placeholder: "Select Role"
						});
						
						$('#adGroupField').select2({
							placeholder: "Select Group"
						});
					});

	//Enable AD group only after role is selected.
	/*function checkRole(){
		var role = document.getElementById("role");
		if(role.selectedIndex >0){
			adGroup.enable = true;
		}
	}*/



</script>


<script type="text/javascript">
	
	function validateForm(){
		var roleInd = document.getElementById('roleField').selectedIndex;
		var adgForm = document.forms['ADGForm'];
		if(roleInd==0){
			swal("Please select account and role");
		}else{
			var adGroupInd = document.getElementById('adGroupField').selectedIndex;
			if(adGroupInd==0 || adGroupInd==null){
				swal("Please select an AD Group");
			}else{
				adgForm.submit();
			}
		}
	}
	
</script>	 


<form:form id="ADGForm" action="add.htm" data-ajax="false" method="POST" modelAttribute="adg">
    <div class="col-xs-7">
    <label  for="AccountNumber">Account Number</label>
					<form:select class="form-control" id="accountNo" path="accountNo" name="accountNo">
						<form:option value="NONE" label="Select Account " />
						<form:options items="${accounts}" />
					</form:select>
  </div>
  <div class="col-xs-7">
  <label>&nbsp;</label>
  </div>
  <div class="col-xs-7">
    <label for="AWS Role">AWS Role</label>
					<form:select class="form-control" id="roleField" path="role" name="roleField">
						<form:option value="">Select Role</form:option>
					</form:select>
  </div>
  <div class="col-xs-7">
  <label>&nbsp;</label>
  </div>
  <div class="col-xs-7">
    <label for="AD Group">AD Group</label>
					<form:select class="form-control" id="adGroupField" path="adGroup" name="adGroup">
						<form:option value="">Select AD Group</form:option>
						<form:options items="${adGroups}" />
					</form:select>
  </div>


  <div class="col-xs-7">
  <label>&nbsp;</label>
  </div>

   <div class="form-group">
    <div class="col-sm-offset-2 col-sm-5">
      <a class="btn btn-primary btn-sm"  onclick="validateForm();"> Add/Update </a>
    </div>
   </div>

<form:hidden path="accountId" />

</form:form>
