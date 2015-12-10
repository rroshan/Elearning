<%@page import="java.util.Iterator"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.elearning.model.*, java.io.IOException, java.util.ArrayList" %>
<!DOCTYPE html>
<%
	String word = request.getParameter("k").toLowerCase();
	StringBuilder sb = new StringBuilder();
	
	ArrayList<String> arrl = new ArrayList<String>();
	
	Trie t = TrieFactory.getTrie();
	
	if(t.find(word))
	{
		sb.append("<div style=\"style=WIDTH:250px;BACKGROUND-COLOR:#f2f2f2;border-radius: 5px 5px 5px 5px;font-size:20px;border-bottom-style:solid;border-width:0px\" onclick=\"auto_comp(this.innerHTML)\">"+word+"</div>");
	}
	else
	{
		arrl = t.autocomplete(word, arrl);
		if(arrl.size() == 0)
		{
			sb.append("");
		}
		else
		{
			Iterator<String> it = arrl.iterator();
			while(it.hasNext())
			{
				sb.append("<div style=\"style=WIDTH:250px;BACKGROUND-COLOR:#f2f2f2;border-radius: 5px 5px 5px 5px;font-size:20px;border-bottom-style:solid;border-width:0px\" onclick=\"auto_comp(this.innerHTML)\">"+it.next()+"</div>");
			}
		}
	}
	
	out.print(sb.toString());
	out.flush();
%>