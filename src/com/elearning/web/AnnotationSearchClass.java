package com.elearning.web;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import com.elearning.model.OntologyQuery;
import com.elearning.model.OntologyQueryFactory;
import com.elearning.model.Result;

public class AnnotationSearchClass extends HttpServlet 
{
	private OntologyQuery q;
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config)
	{
		q = OntologyQueryFactory.getOntologyReferences();
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		//only for individuals (video).....//Super Classes and Sub classes remain empty (null)
		Result res = new Result();
		
		response.setContentType("text/html");
		
		String query = request.getParameter("aq");
		query = query.trim();
		OWLOntologyManager manager = q.getManager();
		OWLOntology ontology = q.getOntology();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLAnnotationProperty seeAlso = factory
				.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI());
		
		Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
		Iterator<OWLNamedIndividual> it = individuals.iterator();
		
		//the OWLNamedIndividuals with query annotation are added to the result object
		while(it.hasNext())
		{
			OWLNamedIndividual oni = it.next();
			for(OWLAnnotation annotation : oni.getAnnotations(ontology, seeAlso))
			{
				if (annotation.getValue() instanceof OWLLiteral) 
				{
        			OWLLiteral val = (OWLLiteral) annotation.getValue();
        			if(val.getLiteral().equals(query))
        			{
        				res.addIndividual(oni);
        				break;
        			}
				}
			}
		}
		
		request.setAttribute("result", res);
		request.setAttribute("ont", q);
		request.setAttribute("query", query);
		
		RequestDispatcher view = request.getRequestDispatcher("anno_search.jsp");
		view.forward(request, response); 
	}
}
