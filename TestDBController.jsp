<%@ page import="mypackage.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>

<%
	TestDBController tdb = new TestDBController();
	String userType = "Host";
	String column = "rfinance";
	String condition = "sessionId = 'VWRGTBIXIKIILHGFVVLA'";
%>
<html>
	<body>	
		<br>
		<textarea name="output" rows="15" cols="60"><%=tdb.sqlTest(userType, column, condition)%></textarea>
		<br>
	</body>
</html>