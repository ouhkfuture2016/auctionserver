<%@ page import="mypackage.*" %>
<html>
	<body>	
		<%
			MainController mc = new MainController();
		%>
		Receive Output:
		<br>
		<textarea name="output" rows="15" cols="60"><%=mc.getResponse(request.getParameter("xml"))%></textarea>
		<br>
	</body>
</html>