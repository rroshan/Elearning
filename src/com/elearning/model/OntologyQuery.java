package com.elearning.model;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

class DLQueryParser 
{
    private OWLOntology rootOntology;
    private BidirectionalShortFormProvider bidiShortFormProvider;

    /** Constructs a DLQueryParser using the specified ontology and short form
     * provider to map entity IRIs to short names.
     * 
     * @param rootOntology
     *            The root ontology. This essentially provides the domain
     *            vocabulary for the query.
     * @param shortFormProvider
     *            A short form provider to be used for mapping back and forth
     *            between entities and their short names (renderings). */
    public DLQueryParser(OWLOntology rootOntology, ShortFormProvider shortFormProvider) {
        this.rootOntology = rootOntology;
        OWLOntologyManager manager = rootOntology.getOWLOntologyManager();
        Set<OWLOntology> importsClosure = rootOntology.getImportsClosure();
        // Create a bidirectional short form provider to do the actual mapping.
        // It will generate names using the input
        // short form provider.
        bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(manager,
                importsClosure, shortFormProvider);
    }

    /** Parses a class expression string to obtain a class expression.
     * 
     * @param classExpressionString
     *            The class expression string
     * @return The corresponding class expression
     * @throws ParserException
     *             if the class expression string is malformed or contains
     *             unknown entity names. */
    public OWLClassExpression parseClassExpression(String classExpressionString)
            throws ParserException {
        OWLDataFactory dataFactory = rootOntology.getOWLOntologyManager()
                .getOWLDataFactory();
        // Set up the real parser
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(
                dataFactory, classExpressionString);
        parser.setDefaultOntology(rootOntology);
        // Specify an entity checker that wil be used to check a class
        // expression contains the correct names.
        OWLEntityChecker entityChecker = new ShortFormEntityChecker(bidiShortFormProvider);
        parser.setOWLEntityChecker(entityChecker);
        // Do the actual parsing
        return parser.parseClassExpression();
    }
}

class DLQueryEngine 
{
    private OWLReasoner reasoner;
    private DLQueryParser parser;

    /** Constructs a DLQueryEngine. This will answer "DL queries" using the
     * specified reasoner. A short form provider specifies how entities are
     * rendered.
     * 
     * @param reasoner
     *            The reasoner to be used for answering the queries.
     * @param shortFormProvider
     *            A short form provider. */
    public DLQueryEngine(OWLReasoner reasoner, ShortFormProvider shortFormProvider) {
        this.reasoner = reasoner;
        OWLOntology rootOntology = reasoner.getRootOntology();
        parser = new DLQueryParser(rootOntology, shortFormProvider);
    }

    /** Gets the superclasses of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @param direct
     *            Specifies whether direct superclasses should be returned or
     *            not.
     * @return The superclasses of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Set<OWLClass> getSuperClasses(String classExpressionString, boolean direct)
            throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> superClasses = reasoner
                .getSuperClasses(classExpression, direct);
        return superClasses.getFlattened();
    }

    /** Gets the equivalent classes of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @return The equivalent classes of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Set<OWLClass> getEquivalentClasses(String classExpressionString)
            throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        Node<OWLClass> equivalentClasses = reasoner.getEquivalentClasses(classExpression);
        Set<OWLClass> result;
        /*if (classExpression.isAnonymous()) {
            result = equivalentClasses.getEntities();
        } else {
            result = equivalentClasses.getEntitiesMinus(classExpression.asOWLClass());
        }*/
        result = equivalentClasses.getEntities();
        return result;
    }

    /** Gets the subclasses of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @param direct
     *            Specifies whether direct subclasses should be returned or not.
     * @return The subclasses of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Set<OWLClass> getSubClasses(String classExpressionString, boolean direct)
            throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLClass> subClasses = reasoner.getSubClasses(classExpression, direct);
        return subClasses.getFlattened();
    }

    /** Gets the instances of a class expression parsed from a string.
     * 
     * @param classExpressionString
     *            The string from which the class expression will be parsed.
     * @param direct
     *            Specifies whether direct instances should be returned or not.
     * @return The instances of the specified class expression
     * @throws ParserException
     *             If there was a problem parsing the class expression. */
    public Set<OWLNamedIndividual> getInstances(String classExpressionString,
            boolean direct) throws ParserException {
        if (classExpressionString.trim().length() == 0) {
            return Collections.emptySet();
        }
        OWLClassExpression classExpression = parser
                .parseClassExpression(classExpressionString);
        NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(classExpression,
                direct);
        return individuals.getFlattened();
    }
}

class DLQueryPrinter 
{
	private DLQueryEngine dlQueryEngine;
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private Result res;
    private Set<OWLClass> superClasses;
    private Set<OWLClass> subClasses;
    
    private static final String BASE_URL = "http://www.semanticweb.org/ontologies/2013/1/elearning.owl"; 

    /** @param engine
     *            the engine
     * @param shortFormProvider
     *            the short form provider */
    public DLQueryPrinter(DLQueryEngine engine, OWLOntologyManager manager, OWLOntology ontology) 
    {
    	this.manager = manager;
        dlQueryEngine = engine;
        this.ontology = ontology;
        superClasses = new HashSet<OWLClass>();
        subClasses = new HashSet<OWLClass>();
    }

    /** @param classExpression
     *            the class expression to use for interrogation */
    
    public void getSuperClasses(String queryExpression)
    {
    	try
    	{
    		superClasses.addAll(dlQueryEngine.getSuperClasses(queryExpression, false));
    	}
    	catch(ParserException pe)
    	{
    		pe.printStackTrace();
    	}
    }
    
    public void getSubClasses(String queryExpression)
    {
    	try
    	{
    		subClasses.addAll(dlQueryEngine.getSubClasses(queryExpression, false));
    	}
    	catch(ParserException pe)
    	{
    		pe.printStackTrace();
    	}
    }
    
    public void addIndividualsAndUrl(OWLDataProperty hasUrlProperty)
    {
    	for(OWLClass c:subClasses)
    	{
    		Set<OWLIndividual> i = c.getIndividuals(ontology); 
    		for (OWLIndividual ind : i) 
    		{ 
    			OWLNamedIndividual oni = ind.asOWLNamedIndividual();
    			res.addIndividual(oni);
            } 
    	}
    	
    	if (!subClasses.isEmpty()) 
        {
            for (OWLClass c : subClasses) 
            {
                res.addSubClass(c);
           }
        }
    }
    
    public Result askQuery(String classExpression) 
    {
    	res = new Result();
    	OWLDataFactory factory = manager.getOWLDataFactory(); 
        PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology); 
        pm.setDefaultPrefix(BASE_URL + "#");
        superClasses.clear();
        subClasses.clear();
        
        if (classExpression.length() == 0) 
        {
            System.out.println("No class expression specified");
        }
        else 
        {
            try 
            {
            	OWLClass subjectClass = factory.getOWLClass(":subject", pm);
            	OWLClass conceptClass = factory.getOWLClass(":concept", pm);
            	OWLClass algorithmsClass = factory.getOWLClass(":algorithms", pm);
            	OWLClass datastructuresClass = factory.getOWLClass(":data_structures", pm);
            	String queryExpression=null;
            	
            	superClasses = dlQueryEngine.getSuperClasses(classExpression, false);
                if(superClasses.contains(subjectClass))
                {
                	if(superClasses.contains(algorithmsClass))
                	{
                		queryExpression = "concept that isConceptOf some "+classExpression;
                		getSuperClasses(queryExpression);
                		getSubClasses(queryExpression);
                		
                		queryExpression = "data_structures that dsUsedIn some " +classExpression;
                		getSuperClasses(queryExpression);
                		getSubClasses(queryExpression);
                	}
                	
                	else if(superClasses.contains(datastructuresClass))
                	{
                		queryExpression = "algorithms that useDS some "+classExpression;
                		getSuperClasses(queryExpression);
                		getSubClasses(queryExpression);
                	}
                }
                
                else if(superClasses.contains(conceptClass))
                {
                	queryExpression = "algorithms that hasConcept some "+classExpression;
                	getSuperClasses(queryExpression);
                	getSubClasses(queryExpression);
                }
                 
                OWLDataProperty hasUrlProperty = factory.getOWLDataProperty(":hasUrl", pm); 
                addIndividualsAndUrl(hasUrlProperty);
                
                subClasses.clear();
                subClasses.addAll(dlQueryEngine.getSubClasses(classExpression, false));
                                	
                if(subClasses.size()-1 != 0)
                {
                	addIndividualsAndUrl(hasUrlProperty);
                }
                else if(subClasses.size()-1 == 0)
                {
                	Set<OWLClass> equivalentClasses = dlQueryEngine.getEquivalentClasses(classExpression);
                	for(OWLClass c:equivalentClasses)
                	{
                		Set<OWLIndividual> i = c.getIndividuals(ontology); 
                		for(OWLIndividual ind : i) 
                		{ 
                			OWLNamedIndividual oni = ind.asOWLNamedIndividual();
                			res.addIndividual(oni);
                			/*for(OWLLiteral url : reasoner.getDataPropertyValues(oni, hasUrlProperty)) 
                			{ 
                				res.addIndividuals(url.getLiteral()); 
                				res.addIndividualName(renderer.render(ind));
                			}*/	
                		}
                	}
                }
            }
            catch (ParserException e) 
            {
                System.out.println(e.getMessage());
            }
        }
        
        //adding super classes
        if (!superClasses.isEmpty()) 
        {
            for (OWLClass c : superClasses) 
            {
                res.addSuperClass(c);
            }
        }
        
        return res;
    }
}

public class OntologyQuery 
{
	private String user_query;
	private OWLOntologyManager manager;
	private OWLOntology localE;
	private OWLReasoner reasoner;
	private ShortFormProvider shortFormProvider;
	private DLQueryPrinter dlQueryPrinter;
	
	public OWLOntologyManager getManager()
	{
		return manager;
	}
	
	public OWLOntology getOntology()
	{
		return localE;
	}
	
	public OWLReasoner getReasoner()
	{
		return reasoner;
	}
	
	public void setUserQuery(String str)
	{
		user_query = str;
	}
	
	public void loadOntology() throws OWLOntologyCreationException, IOException
	{
		//Loading
		manager = OWLManager.createOWLOntologyManager();
		File file = new File("C:/Users/abhinav/Desktop/Project/ontologies/elearning 1/elearning.owl");
		localE = manager.loadOntologyFromOntologyDocument(file);
		System.out.println("Loaded ontology: " + localE);
		
		//Querying
		reasoner = createReasoner(localE);
		shortFormProvider = new SimpleShortFormProvider();
        // Create the DLQueryPrinter helper class. This will manage the
        // parsing of input and printing of results
		dlQueryPrinter = new DLQueryPrinter(new DLQueryEngine(reasoner, shortFormProvider), manager, localE);
	}

    public static OWLReasoner createReasoner(OWLOntology rootOntology) 
	{
        // We need to create an instance of OWLReasoner. An OWLReasoner provides
        // the basic query functionality that we need, for example the ability
        // obtain the subclasses of a class etc. To do this we use a reasoner
        // factory.
        // Create a reasoner factory.
        OWLReasonerFactory reasonerFactory = PelletReasonerFactory.getInstance();
        OWLReasoner reasoner = reasonerFactory.createReasoner(rootOntology, new SimpleConfiguration()); 
        return reasoner;
    }
	
	public Result search(String str)
	{
		setUserQuery(str);
		return dlQueryPrinter.askQuery(user_query.trim());
	}
}