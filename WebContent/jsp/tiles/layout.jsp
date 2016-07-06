<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<!-- bootstrap stylesheets -->
	<link rel="stylesheet" href="../css/bootstrap.min.css">	
	<link rel="stylesheet" href="https://bootswatch.com/yeti/bootstrap.min.css">
	<link rel="stylesheet" href="../css/theme.bootstrap.css">
		
	<!-- jQuery library -->
	<script src="https://code.jquery.com/jquery-2.2.0.js"></script>
	
	<!-- Latest compiled JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	
	<!-- Sweet Alerts -->
	<link rel="stylesheet" type="text/css" href="../lib/sweet-alert.css">
	<script src="../lib/sweet-alert.min.js"></script>
	
	<!-- JavaScript libraries -->
 	<script src="../js/jquery.filtertable.js"></script> 
	<script src="../js/jquery.tablesorter.combined.js"></script>  	
	<script type="text/javascript" src="../js/jquery.stickytableheaders.js"></script> 
	<script type="text/javascript" src="../js/select2.full.js"></script>

	<script >
		$(document).ready(function() {
	        // apply filterTable to all tables on the page
	        $('table').filterTable({ inputSelector: '#input-filter'});
	        //apply stickyTableHeaders to all tables on the page
	        $('table').stickyTableHeaders();
	    });
	</script>
	
	<script>
	$(function() {
	
		  // NOTE: $.tablesorter.theme.bootstrap is ALREADY INCLUDED in the jquery.tablesorter.widgets.js
		  // file; it is included here to show how you can modify the default classes
		  $.tablesorter.themes.bootstrap = {
		    // these classes are added to the table. To see other table classes available,
		    // look here: http://getbootstrap.com/css/#tables
		    table        : 'table table-striped',
		    caption      : 'caption',
		    // header class names
		    header       : 'bootstrap-header', // give the header a gradient background (theme.bootstrap_2.css)
		    sortNone     : '',
		    sortAsc      : '',
		    sortDesc     : '',
		    active       : '', // applied when column is sorted
		    hover        : '', // custom css required - a defined bootstrap style may not override other classes
		    // icon class names
		    icons        : '', // add "icon-white" to make them white; this icon class is added to the <i> in the header
		    iconSortNone : 'bootstrap-icon-unsorted', // class name added to icon when column is not sorted
		    iconSortAsc  : 'glyphicon glyphicon-chevron-up', // class name added to icon when column has ascending sort
		    iconSortDesc : 'glyphicon glyphicon-chevron-down', // class name added to icon when column has descending sort
		    filterRow    : '', // filter row class; use widgetOptions.filter_cssFilter for the input/select element
		    footerRow    : '',
		    footerCells  : '',
		    even         : '', // even row zebra striping
		    odd          : ''  // odd row zebra striping
		  };
	
		  // call the tablesorter plugin and apply the uitheme widget
		  $("table").tablesorter({
		    // this will apply the bootstrap theme if "uitheme" widget is included
		    // the widgetOptions.uitheme is no longer required to be set
		    theme : "bootstrap",
		    headers: {
		 	      // disable sorting of the first & second column - before we would have to had made two entries
		 	      // note that "first-name" is a class on the span INSIDE the first column th cell
		 	      '.first-name, .last-name' : {
		 	        // disable it by setting the property sorter to false
		 	        sorter: false
		 	      }
		    },
	
		    widthFixed: true,
	
		    headerTemplate : '{content} {icon}', // new in v2.7. Needed to add the bootstrap icon!
	
		    // widget code contained in the jquery.tablesorter.widgets.js file
		    // use the zebra stripe widget if you plan on hiding any rows (filter widget)
		    widgets : [ "uitheme"],
	
		    widgetOptions : {
		      // using the default zebra striping class name, so it actually isn't included in the theme variable above
		      // this is ONLY needed for bootstrap theming if you are using the filter widget, because rows are hidden
		      zebra : ["even", "odd"],
	
		      // reset filters button
		      filter_reset : ".reset",
	
		      // extra css class name (string or array) added to the filter element (input or select)
		      filter_cssFilter: "form-control",
	
		      // set the uitheme widget to use the bootstrap theme class names
		      // this is no longer required, if theme is set
		      // ,uitheme : "bootstrap"
	
		    }
		  })
		  
		});
	</script>

	<tiles:insertAttribute name="header" />	
	<title>
		<tiles:insertAttribute name="title" ignore="true" />
	</title>
	<tiles:insertAttribute name="menu" />


</head>
<body class="background">
	<div class="container">				
		<div id="content">
		<tiles:insertAttribute name="messages" />
		
		<tiles:insertAttribute name="body" />
		
		<!-- Trigger the modal with a button -->

<!-- Modal -->
<div class="modal fade" id="myModal">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
        </div> <!-- /.modal-content -->
    </div> <!-- /.modal-dialog -->
</div> <!-- /.modal -->		
		<tiles:insertAttribute name="footer" />
		</div>
</div>
</body> 
</html>