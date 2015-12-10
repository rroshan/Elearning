package com.elearning.web;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.elearning.model.OntologyQuery;
import com.elearning.model.OntologyQueryFactory;
import com.elearning.model.Result;
import com.elearning.model.Trie;
import com.elearning.model.TrieFactory;

public class TestInputClass extends HttpServlet 
{
	private OntologyQuery q;
	private Trie trie;
	
	private static final long serialVersionUID = 1L;
    
	public void init(ServletConfig config)
	{
		q = OntologyQueryFactory.getOntologyReferences();
		trie = TrieFactory.getTrie();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType("text/html");
		request.setAttribute("ont_query", q);
		String str = request.getParameter("q");
		str = str.trim();
		Result res = null;
		ArrayList<String> results = null;
		
		if(trie.find(str))
		{
			String query = str.replaceAll(" ", "_").toLowerCase();
			res = q.search(query);
			request.setAttribute("result", res);
		}
		else
		{
			 results = trie.search(str, 3);
			 request.setAttribute("auto_correct", results);
		}
		
		RequestDispatcher view = request.getRequestDispatcher("result.jsp");
		view.forward(request, response);
	}
}