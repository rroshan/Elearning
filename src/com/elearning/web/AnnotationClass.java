package com.elearning.web;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import com.elearning.model.OntologyQuery;
import com.elearning.model.OntologyQueryFactory;
import java.io.File;

public class AnnotationClass extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	private OntologyQuery q;
	
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
		response.setContentType("text/html");
		String vid_name = request.getParameter("video").replaceAll(" ", "_");
		vid_name = vid_name.trim();
		
		String ldata_link = request.getParameter("ldata_link");
		ldata_link = ldata_link.trim();
		
		OWLOntologyManager manager = q.getManager();
		OWLOntology ontology = q.getOntology();
		OWLDataFactory factory = manager.getOWLDataFactory();
		OWLNamedIndividual oni = factory.getOWLNamedIndividual(IRI.create(ontology.getOntologyID().getOntologyIRI().toString() + "#" + vid_name));
		OWLAnnotation seeAlsoAnno = factory.getOWLAnnotation(factory.getRDFSSeeAlso(), factory.getOWLLiteral(ldata_link));
		OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(oni.getIRI(), seeAlsoAnno);
		manager.applyChange(new AddAxiom(ontology, ax));
		
		File file = new File("C:/Users/abhinav/Desktop/Project/ontologies/elearning 1/elearning.owl");
		try
		{
			manager.saveOntology(ontology, IRI.create(file.toURI()));
		}
		catch(OWLOntologyStorageException oose)
		{
			oose.printStackTrace();
		}
		
		RequestDispatcher view = request.getRequestDispatcher("annotate_successful.jsp");
		view.forward(request, response);
	}
}