package com.elearning.model;

import java.io.IOException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class OntologyQueryFactory 
{
	private static OntologyQuery q;
	
	public static void makeOntologyReferences()
	{
		try
		{
			q = new OntologyQuery();
			q.loadOntology();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		catch(OWLOntologyCreationException ooce)
		{
			ooce.printStackTrace();
		}
	}
	
	public static OntologyQuery getOntologyReferences()
	{
		if(q == null)
		{
			makeOntologyReferences();
		}
		return q;
	}
}