<%@ page import="mypackage.*" %>
<%@ page import="java.util.concurrent.*" %>

<%
	Server s = new Server();
	s.startListening(12345);
	Thread.sleep(60 * 1000);
	s.endListening();
%>