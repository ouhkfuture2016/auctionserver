<%@ page import="mypackage.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>

<%
	TestMainController tmc = new TestMainController();
%>
<html>
	<body>	
		<br>
		<textarea name="output" rows="15" cols="60"><%=tmc.testDoLogin()%></textarea>
		<br>
	</body>
</html>