<%@page import="org.semanticweb.owlapi.model.OWLAnnotation"%>
<%@page import="org.semanticweb.owlapi.vocab.OWLRDFVocabulary"%>
<%@page import="org.semanticweb.owlapi.model.OWLAnnotationProperty"%>
<%@page import="org.semanticweb.owlapi.model.OWLLiteral"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.semanticweb.owlapi.io.OWLObjectRenderer"%>
<%@page import="org.semanticweb.owlapi.model.OWLDataProperty"%>
<%@page import="org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat"%>
<%@page import="org.semanticweb.owlapi.reasoner.OWLReasoner"%>
<%@page import="org.semanticweb.owlapi.model.OWLDataFactory"%>
<%@page import="org.semanticweb.owlapi.model.OWLOntology"%>
<%@page import="org.semanticweb.owlapi.model.OWLOntologyManager"%>
<%@page import="com.elearning.model.OntologyQuery"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.elearning.model.Result"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Annotation Search Results</title>

<script type="text/javascript">
function validate_anno_search()
{
	var str = document.getElementById("aq").value;
			
	if(str === "")
	{
		alert("The search query is empty!!");
		return false;
	}
	
	return true;
}

</script>
</head>
<body style="background-color: #f2f2f2;">
	<form method="get" action="AnnotationQuery" id="anno_search" name="anno_search" onsubmit="return validate_anno_search()">
		<br /> <br />
		<strong style="font-family:segoe ui light;font-size: 20px;">Enter Annotation Link:&nbsp</strong>
		 <input type="text" name="aq" id="aq" style="WIDTH:350px;height:25px;border-radius: 5px 5px 5px 5px;" placeholder="Enter link to open data cloud..." autocomplete="off" />
		<br /><br />
		<button style="background-color:#555555;border:none;display:block;cursor:pointer;-webkit-border-radius: 4px;-moz-border-radius: 4px;border-radius: 4px;width:100px;height:35px" type="submit" class="button white" name="submit" value="submit"><span style="color:#ffffff">Search</span></button>
		<!--  Enter the Annotation: <input type="text" name="aq" id="aq" autocomplete="off" />
		<input type="submit" value="search">-->
	</form>
	
	<hr>
	
	<h3 style="font-family:segoe ui light;text-align:left;font-size: 20px;">Search Query: </h3>
	<%
		String query = (String)request.getAttribute("query");
	%>
	
	<a style="text-decoration:none" href="<%out.print(query); %>"><%out.print(query); %></a>
	<br>
	
	<h3 style="font-family:segoe ui light;text-align:left;font-size: 20px;">Search Results: </h3>

	
	<table border="0" cellpadding="40">
	<%
		Result res = (Result)request.getAttribute("result");
		OntologyQuery q = (OntologyQuery)request.getAttribute("ont");
		
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
		
		ArrayList<OWLNamedIndividual> individuals = res.getIndividuals();
		
		OWLNamedIndividual oni = null;
		String v_name = null;
		String v_link = null;
		String anno = null;
		int len = individuals.size();
		
		int a=-3;
		for(int i=0;i<len;i++)
		{
			if(i == a+3)
			{
		%>
		
		<tr>
		<%
				a = a + 3;
			}
			anno = "";
			oni = individuals.get(i);
			
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
	
	<%			if((i+1) % 3 == 0)
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