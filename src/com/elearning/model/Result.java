package com.elearning.model;

import java.util.ArrayList;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

public class Result 
{
	private ArrayList<OWLClass> superclasses = new ArrayList<OWLClass>();
	private ArrayList<OWLClass> subclasses = new ArrayList<OWLClass>();
	private ArrayList<OWLNamedIndividual> individuals = new ArrayList<OWLNamedIndividual>();
			
	public void addSuperClass(OWLClass superclass)
	{
		superclasses.add(superclass);
	}
	
	public void addSubClass(OWLClass subclass)
	{
		subclasses.add(subclass);
	}
	
	public void addIndividual(OWLNamedIndividual individual)
	{
		individuals.add(individual);
	}
	
	public ArrayList<OWLClass> getSuperClasses()
	{
		return superclasses;
	}
	
	public ArrayList<OWLClass> getSubClasses()
	{
		return subclasses;
	}
	
	public ArrayList<OWLNamedIndividual> getIndividuals()
	{
		return individuals;
	}
}
