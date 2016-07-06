<%@ include file="../commons/include.jsp"%>

<div id=messages>

	<c:if test="${!empty warning || !empty success || !empty notice}">
		<div id=errorMessage class="alert alert-warning" align="center"><strong style="color: #8e2525"></strong>
			<c:choose>
		        <c:when test="${!empty warning}">
		            <span id="warning" style="text-align: center;"> ${warning} </span>
		        </c:when> 
		        <c:when test="${!empty success}">
		            <span id="success" style="text-align: center;"> ${success} </span>
		        </c:when>
		        <c:when test="${!empty notice}">
		            <span id="notice" style="text-align: center;"> ${notice} </span>
		        </c:when>
			</c:choose>
		</div>
	</c:if>
	<c:if test="${!empty error}">
		<div id=errorMessage1 class="alert alert-danger" align="center"><strong style="color: #8e2525">Error!</strong>
			<c:choose>
		    	<c:when test="${!empty error}">
		        	<span id="error" align ="center"><strong>${error} </strong></span>
				</c:when>

			</c:choose>
		</div>
	</c:if>
</div>
 
<sec:authorize access="authenticated" >
</sec:authorize>
