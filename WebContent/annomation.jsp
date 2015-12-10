<%@page import="org.semanticweb.owlapi.model.OWLLiteral"%>
<%@page import="org.semanticweb.owlapi.model.OWLDataFactory"%>
<%@page import="org.semanticweb.owlapi.io.OWLObjectRenderer"%>
<%@page import="uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer"%>
<%@page import="org.semanticweb.owlapi.model.OWLDataProperty"%>
<%@page import="org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat"%>
<%@page import="org.semanticweb.owlapi.model.OWLOntologyManager"%>
<%@page import="org.semanticweb.owlapi.reasoner.OWLReasoner"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.Set"%>
<%@page import="org.semanticweb.owlapi.model.OWLNamedIndividual"%>
<%@page import="org.semanticweb.owlapi.model.OWLOntology"%>
<%@page import="com.elearning.model.OntologyQueryFactory"%>
<%@page import="com.elearning.model.OntologyQuery"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<script type="text/javascript">
	function validate()
	{
		var v_name = document.getElementById("video").value;
		var link = document.getElementById("ldata_link").value;
		
		if(v_name === "" || link === "")
		{
			alert("Please fill the textboxes!!");
			return false;
		}
		
		return true;
	}
	
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

<title>Annomation</title>
</head>
<body style="background-color: #f2f2f2;">
	<h3 style="font-family:segoe ui light;text-align:left;font-size: 35px;">Annomation</h3>
	
	<form method="get" action="Annotation" id="annotate" name="annotate" onsubmit="return validate()">
		<strong style="font-family:segoe ui light;font-size: 20px;">Video:&nbsp</strong> 
		<input list="videos" id="video" name="video" style="WIDTH:250px;height:25px;border-radius: 5px 5px 5px 5px;" placeholder="Video name...">
			<datalist id="videos">
	
	<%
		OntologyQuery q = OntologyQueryFactory.getOntologyReferences();
		OWLOntology ontology = q.getOntology();
		OWLReasoner reasoner = q.getReasoner();
		OWLOntologyManager manager = q.getManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		
		PrefixOWLOntologyFormat pm = (PrefixOWLOntologyFormat) manager.getOntologyFormat(ontology);
        String BASE_URL = "http://www.semanticweb.org/ontologies/2013/1/elearning.owl";
        pm.setDefaultPrefix(BASE_URL + "#");
		OWLDataProperty hasUrlProperty = factory.getOWLDataProperty(":hasUrl", pm);
		OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();
		
		Set<OWLNamedIndividual> individuals = ontology.getIndividualsInSignature();
		
		Iterator<OWLNamedIndividual> it = individuals.iterator();
		OWLNamedIndividual oni;
		String v_name;
		String v_link;
		
		while(it.hasNext())
		{
			oni = it.next();
			
			for (OWLLiteral url : reasoner.getDataPropertyValues(oni, hasUrlProperty))
			{
				v_name = renderer.render(oni);
				v_link = url.getLiteral();
				
				out.println("<option value=\"" + v_name.replaceAll("_", " ") + "\">" + "</option>");
			}	
		}
	%>
	
		</datalist>
		<strong style="font-family:segoe ui light;font-size: 20px;">&nbsp&nbspLink to open data cloud:&nbsp</strong> 
		<input type="text" id="ldata_link" name="ldata_link" style="WIDTH:350px;height:25px;border-radius: 5px 5px 5px 5px;" placeholder="open cloud data link...." autocomplete="off"/>
		<br /><br /> <!--<input type="submit" value="submit" />-->
		<button style="background-color:#555555;border:none;display:block;cursor:pointer;-webkit-border-radius: 4px;-moz-border-radius: 4px;border-radius: 4px;width:100px;height:35px" type="submit" class="button white" name="submit" value="submit"><span style="color:#ffffff">Submit</span></button>
	</form>
	
	<br />
	<hr>
	<br />
	<br />
	<form method="get" action="AnnotationQuery" id="anno_search" name="anno_search" onsubmit="return validate_anno_search()">
		<strong style="font-family:segoe ui light;font-size: 20px;">Enter Annotation Link:&nbsp</strong>
		 <input type="text" name="aq" id="aq" style="WIDTH:350px;height:25px;border-radius: 5px 5px 5px 5px;" placeholder="Enter link to open data cloud..." autocomplete="off" />
		<br /><br />
		<button style="background-color:#555555;border:none;display:block;cursor:pointer;-webkit-border-radius: 4px;-moz-border-radius: 4px;border-radius: 4px;width:100px;height:35px" type="submit" class="button white" name="submit" value="submit"><span style="color:#ffffff">Search</span></button>
		<!--  <input type="submit" value="search">-->
	</form>
</body>
</html>