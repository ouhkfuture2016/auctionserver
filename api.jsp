<%@ page import="mypackage.*" %>
<%@ page import="java.io.*" %>
<%
	XMLParser a = new XMLParser(request.getParameter("xml"));
%>
<%=a.invoke()%>