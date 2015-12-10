<%@page import="org.semanticweb.owlapi.reasoner.OWLReasoner"%>
<%@page import="org.semanticweb.owlapi.apibinding.OWLManager"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="org.semanticweb.owlapi.util.SimpleShortFormProvider"%>
<%@page import="org.semanticweb.owlapi.model.OWLClass"%>
<%@page import="java.util.Iterator"%>
<%@ page import="com.elearning.model.*, org.semanticweb.owlapi.model.OWLOntologyCreationException, java.io.IOException, java.util.ArrayList, org.semanticweb.owlapi.model.OWLEntity, org.semanticweb.owlapi.util.ShortFormProvider, org.semanticweb.owlapi.util.SimpleShortFormProvider, org.semanticweb.owlapi.model.OWLOntologyManager, org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat, org.semanticweb.owlapi.model.OWLDataFactory, org.semanticweb.owlapi.model.OWLDataProperty, org.semanticweb.owlapi.model.OWLOntology, org.semanticweb.owlapi.model.OWLLiteral, org.semanticweb.owlapi.io.OWLObjectRenderer, uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer, org.semanticweb.owlapi.model.OWLAnnotationProperty, org.semanticweb.owlapi.vocab.OWLRDFVocabulary, org.semanticweb.owlapi.model.OWLAnnotation" %>
<!DOCTYPE html>
<html>
<head>
	<script src="auto_complete.js">
	</script>
	<style>
	#demo-wrapper {
    background:#555555;
    margin: 1px auto 10px;
    overflow: visible;
    padding: 10px;
    width: 350px;
   // height:400px;
    border-radius: 30px 30px 30px 30px;
	border: 1px solid #444444;
    box-shadow: 0 0 10px rgba(0,0,0,0.5), 0 1px 3px rgba(255, 255, 255, 0.2) inset;
}

/* Dark Search Box */

#dark {
   height: 28px;
   width: 30px;
   padding: 20px 80px;
   background:#555;
   position:relative;
}

#dark #search {

}

#dark #search input[type="text"] {
    background: url(./css/search-dark.png) no-repeat 10px 6px #444;
    border: 0 none;
    font: bold 12px Arial,Helvetica,Sans-serif;
    color: #f2f2f2;
    width: 50px;
    padding: 6px 15px 6px 35px;
    -webkit-border-radius: 20px;
    -moz-border-radius: 20px;
    border-radius: 20px;
    text-shadow: 0 2px 2px rgba(0, 0, 0, 0.3);
    -webkit-box-shadow: 0 1px 0 rgba(255, 255, 255, 0.1), 0 1px 3px rgba(0, 0, 0, 0.2) inset;
    -moz-box-shadow: 0 1px 0 rgba(255, 255, 255, 0.1), 0 1px 3px rgba(0, 0, 0, 0.2) inset;
    box-shadow: 0 1px 0 rgba(255, 255, 255, 0.1), 0 1px 3px rgba(0, 0, 0, 0.2) inset;
    -webkit-transition: all 0.7s ease 0s;
    -moz-transition: all 0.7s ease 0s;
    -o-transition: all 0.7s ease 0s;
    transition: all 0.7s ease 0s;
    }

#dark #search input[type="text"]:focus {
    width: 100px;
    }
    
</style>
</head>

<body onload="bodyLoad()" style="background-color: #f2f2f2;">
		<h2 style="font-family:segoe ui light;text-align:left;font-size: 0px;">Search</h2>
		<!-- change -->
		<div id="demo-wrapper" align="left">
		<div id="dark">
		<form method="get" action="OntologyQuery" id="search" name="search">
  				<input id="q" name="q" type="text" style="WIDTH:150px;border-radius: 5px 5px 5px 5px;" placeholder="Semantic Search..." onkeyup="sendQuery(this.value)" autocomplete="off"/>
  				<div align="left" class="box" id="autocomplete" 
              	style="WIDTH:250px;BACKGROUND-COLOR:#f2f2f2;border-radius: 5px 5px 5px 5px;font-size:20px;border-style:solid;border-width:1px;"></div>
		</form>
		</div>
		</div>
		<hr />
		
		<h2 id="id1" style="font-family:segoe ui light;text-align:left;font-size: 30px;width: 100%;align:left;">Search Results</h2>
		<%
		Result res = null;
		boolean flag = false;
		OntologyQuery q = (OntologyQuery)request.getAttribute("ont_query");
		try
		{
			String str = request.getParameter("q");
			res = (Result)request.getAttribute("result");
		%>
		<h3 style="font-family:segoe ui light;text-align:left;font-size: 20px;">Search Query :- <%out.print(str + "<br >");%></h3>
		<%
			if(res != null)
			{
				flag = true;
			}
			else
			{
				ArrayList<String> results = (ArrayList<String>)request.getAttribute("auto_correct");
				
				if(results.size() > 0)
				{
					out.print("<h2 style=\"font-family:segoe ui light;text-align:left;font-size: 20px;\">Did you mean....</h2>");
					Iterator<String> it = results.iterator();
					String word;
					while(it.hasNext())
					{
						word = it.next();
						out.print("<a style=\"text-decoration:none;font-size:20px\" href=\"http://localhost:8080/Elearning/OntologyQuery?q="+word+"\">"+word+"</a> <br/ >");
					}
				}
				
				else
				{
					out.print("<br style=\"font-family:segoe ui light;text-align:left;font-size: 30px;\"> Please enter a valid query!!");
				}
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		%>
			
		<%
			if(flag)
			{
				OWLEntity entity;
				ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
				String word;
				int len;
		%>
		
		<h3 style="font-family:segoe ui light;text-align:left;font-size: 20px;">Super Classes :-</h3>
		<%
				ArrayList<OWLClass> superclasses = res.getSuperClasses();
				len = superclasses.size();
				for(int i=0;i<len;i++)
				{
					entity = superclasses.get(i);
					word = shortFormProvider.getShortForm(entity);
					
					if(!word.equalsIgnoreCase("nothing"))
					{
						out.print("<a style=\"text-decoration:none;font-size:20px\" href=\"http://localhost:8080/Elearning/OntologyQuery?q="+word.replace("_", " ")+"\">"+word.replace("_", " ")+"</a>" + "&nbsp&nbsp&nbsp&nbsp");
					}
				}
		%>
		
		<h3 style="font-family:segoe ui light;text-align:left;font-size: 20px;">Related Results :-</h3>
		<%
				OWLOntologyManager manager = q.getManager();
				OWLOntology ontology = q.getOntology();
				OWLDataFactory factory = manager.getOWLDataFactory(); 
				OWLReasoner reasoner = q.getReasoner();
		        PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
		        String BASE_URL = "http://www.semanticweb.org/ontologies/2013/1/elearning.owl";
		        pm.setDefaultPrefix(BASE_URL + "#");
				OWLDataProperty hasUrlProperty = factory.getOWLDataProperty(":hasUrl", pm);
				OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
				
				OWLAnnotationProperty seeAlso = factory
						.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_SEE_ALSO.getIRI());
				
				ArrayList<OWLClass> subclasses = res.getSubClasses();
				len = subclasses.size();
				
				String anno;
				
				for(int i=0;i<len;i++)
				{
					OWLClass c = subclasses.get(i);
					anno = "";
					
					for (OWLAnnotation annotation : c.getAnnotations(ontology, seeAlso))
					{
						if (annotation.getValue() instanceof OWLLiteral) 
						{
		                    OWLLiteral val = (OWLLiteral) annotation.getValue();
		                    anno = anno + "  <a style=\"text-decoration:none;font-size:15px\" href=\"" + val.getLiteral() + "\">" + val.getLiteral() + "</a>";
						}
					}
					
					word = shortFormProvider.getShortForm(c);
					
					if(!word.equalsIgnoreCase("nothing"))
					{
						out.print("<br>" + "<a style=\"text-decoration:none;font-size:20px\" href=\"http://localhost:8080/Elearning/OntologyQuery?q="+word.replace("_", " ")+"\">"+word.replace("_", " ")+"</a>");
						out.print("   <i>Annotation:" + anno + "</i>");
						
					}
				}
		%>
		
		<h3 style="font-family:segoe ui light;text-align:left;font-size: 20px;">Videos Results:-</h3>
		<a style="text-decoration:none;font-size:20px" href="http://localhost:8080/Elearning/annomation.jsp" target="_blank">Annotate videos</a>
		
		<table border="0" cellpadding="40">
		<%
				ArrayList<OWLNamedIndividual> individuals = res.getIndividuals();
				len = individuals.size();
				String v_name = null;
				String v_link = null;
				OWLNamedIndividual oni = null;
				
				int a=-3;
				for(int i=0;i<len;i++)
				{
					if(i == a+3)
					{
		%>
		<tr>
		<%
						a = a+3;
					}
					oni = individuals.get(i);
					anno = "";
					
					for (OWLLiteral url : reasoner.getDataPropertyValues(oni, hasUrlProperty))
					{
						v_name = renderer.render(oni);
						v_link = url.getLiteral();
		%>
			<td>
					<h5 style="font-family:segoe ui light;text-align:left;font-size: 15px;"><%out.print(v_name.replace("_", " ")); %></h5>
					<video width="320" height="240" controls>
					<source src="<%out.print(v_link);%>" type="video/mp4"></source>
					</video>
					<br />
		<%	
					}
		
					for (OWLAnnotation annotation : oni.getAnnotations(ontology, seeAlso))
					{
						if (annotation.getValue() instanceof OWLLiteral) 
						{
                			OWLLiteral val = (OWLLiteral) annotation.getValue();
                			anno = anno + "  <a style=\"text-decoration:none;font-size:15px\" href=\"" + val.getLiteral() + "\">" + val.getLiteral() + "</a> <br />";
						}
					}
				
					out.print("   <i>Annotation:" + anno + "</i>");
		%>
			</td>
			
		<%
		        	
					if((i+1) % 3 == 0)
					{
		%>			
			
			</tr>
		<%
					}
				}
			}
		%>
		</table>
</body>
</html>