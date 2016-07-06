<%@ include file="../commons/include.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<style>
.affix {
	top: 0;
	width: 100%;
}

.affix+.container-fluid {
	padding-top: 50px;
}

.navbar {
	z-index: 100;
}
</style>


<div id="nav">
	<nav class="navbar navbar-xs navbar-default navbar-static"
		role="navigation" data-spy="affix" data-offset-top="50">
		<div class="container-fluid">
			<div class="navbar-header">
				<a class="navbar-brand" href="../login/validate.htm">Air Lift
					Key Services</a>
			</div>
			<c:if test="${sessionScope.user.emailId!=null}">
				<ul id=nav class="nav navbar-nav">
					<c:if test="${sessionScope.user.admin==true}">
						<li class="dropdown"><a class="dropdown-toggle"
							data-toggle="dropdown" href="#">View<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="../login/validate.htm">List Roles</a></li>
								<li><a href="../account/viewAllAccounts.htm">Accounts</a></li>
								<li><a href="../arp/viewAllARP.htm">Role Policies</a></li>
								<li><a href="../adg/viewAllADG.htm">AD Groups</a></li>
							</ul></li>
						<li class="dropdown"><a class="dropdown-toggle"
							data-toggle="dropdown" href="#">Add/Update<span class="caret"></span></a>
							<ul class="dropdown-menu">
								<li><a href="../account/new.htm">Accounts</a></li>
								<li><a href="../arp/new.htm">Role Policies</a></li>
								<li><a href="../adg/new.htm">AD Groups</a>
								<li>
							</ul></li>
					</c:if>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li>
						<form class="navbar-form" role="search">
							<div class="input-group">
								<input id="input-filter" type="text" class="form-control"
									placeholder="Filter">
							</div>
						</form>
					</li>
					<li><a href="../jsp/tiles/help.html" data-toggle="modal" data-target="#myModal"><span
							class="glyphicon glyphicon-info-sign"></span> Help </a></li>
					<li class="user"><a><span class="glyphicon glyphicon-user"></span>
							${(sessionScope.user.emailId!=null)? fn:substring(sessionScope.user.emailId, 0, fn:indexOf(sessionScope.user.emailId, "@")):""}
					</a></li>
					<li><a href="../login/logout.htm"><span
							class="glyphicon glyphicon-log-out"></span> Logout</a></li>
				</ul>
			</c:if>
			<div class="navbar-inner"></div>
			<c:if test="${sessionScope.user.emailId==null}">
				<div>
					<ul class="nav navbar-nav">
						<li class="active"><a href="../account/viewAllAccounts.htm">Home</a></li>
					</ul>
					<ul class="nav navbar-nav navbar-right">
						<li><a href="../login/loginPage.htm"><span
								class="glyphicon glyphicon-log-in"></span> Login</a></li>
					</ul>
				</div>
			</c:if>
		</div>
	</nav>
</div>