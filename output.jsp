<%@ page import="mypackage.*" %>
<%@ page import="java.io.*" %>
<html>
	<body>
		<%
			XMLParser a = new XMLParser(request.getParameter("xml"));
		%>
		Receive Output:
		<br>
		<textarea name="output" rows="15" cols="60"><%=a.invoke()%></textarea>
		<br>
	</body>
</html>