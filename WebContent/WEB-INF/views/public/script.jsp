<%@ page pageEncoding="UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<script src="/AnotherKafkaMonitor/media/js/public/jquery.js" type="text/javascript"></script>
<script src="/AnotherKafkaMonitor/media/js/public/bootstrap.min.js"
	type="text/javascript"></script>
<script src="/AnotherKafkaMonitor/media/js/public/raphael.min.js" type="text/javascript"></script>
<script src="/AnotherKafkaMonitor/media/js/public/morris.min.js" type="text/javascript"></script>
<script src="/AnotherKafkaMonitor/media/js/public/navbar.js" type="text/javascript"></script>
<%
	String[] loader = request.getParameterValues("loader");
	if (loader == null) {
		return;
	}
	for (String s : loader) {
%>
<script src="/AnotherKafkaMonitor/media/js/<%=s%>"></script>
<%
	}
%>
