<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ include file="commons/include.jsp"%>

<script>
$(document).ready(function(){
    $('[data-toggle="popover"]').popover(); 
});
</script>

<form:form id="ARPForm">
<div class="container-fluid well">
<table id="myTable" class="table table-hover tablesorter">
  <thead>
  <tr class='heading'>
  <th>AWS Account/User Name - Description</th> <th>Role</th> <th class="sorter-false">Policy</th><th>Last Modified</th> </tr>
  </thead>
  
	<tbody>
		<c:forEach items="${arps}" var="arp" varStatus="theCount">
			<tr  class="${theCount.index % 2 == 0 ? 'even' : 'odd'}">
				<td>${arp.accountNo}</td>
				<td>${arp.role}</td>
				<td>
				<a href="#" onclick="return false" data-toggle="popover" data-content='${arp.policy}'>
					<span class="glyphicon glyphicon glyphicon-align-center" ></span>
    			</a>
				</td>	
				<td>
					<div id="lastModified">
				    	<c:if test="${ fn:contains(arp.lastUpdatedBy, '@')}">
							${fn:substring(arp.lastUpdatedBy, 0, fn:indexOf(arp.lastUpdatedBy, "@"))} 
						</c:if>
						<c:if test="${!fn:contains(arp.lastUpdatedBy, '@')}">
							${arp.lastUpdatedBy}
						</c:if>
						<br>
						${arp.lastUpdateTime}
					</div>
				</td>	
			</tr>
		</c:forEach>
	</tbody>
</table>
</div>
<div id="addButtonContainer" align="center"><a class="btn btn-primary btn-sm" href="new.htm"><b id="addSign">+</b> ARP</a></div>
</form:form>

 