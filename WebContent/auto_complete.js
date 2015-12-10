var req;

function validate()
{
	var str = document.getElementById("q").value;
	
	if(str === "")
	{
		alert("Search query is empty!!");
		return false;
	}
	
	return true;
}

function initialize()
{
	if (window.XMLHttpRequest)  
	{  
		req = new XMLHttpRequest();  
	}
		
	if(window.ActiveXObject)  
    {  
		return new ActiveXObject("Microsoft.XMLHTTP");  
	}  
}
	
function sendQuery(key)
{ 	
	if(key == "" ||  key == null)
	{
		hideDiv("autocomplete");	
	}
		
	else
	{
		initialize();
		if(req!=null)
		{
			req.onreadystatechange = process;
			req.open("GET", "autocomplete.jsp?k="+key, true);
			req.send(null);
		}
	}
}	
	
function auto_comp(val)
{
	document.getElementById("q").value = val;
}
			
function process()
{	
	if (req.readyState == 4)
	{
		if (req.status == 200)
		{
			if(req.responseText=="")
				hideDiv("autocomplete");
			else
			{
				showDiv("autocomplete");
			    document.getElementById("autocomplete").innerHTML = req.responseText;
			}
		}
		else
		{
			document.getElementById("autocomplete").innerHTML="There was a problem retrieving data:<br>"+ req.statusText;
		}
	}
}
		
function showDiv(divid)
{
   document.getElementById(divid).style.visibility="visible";
}
		
function hideDiv(divid)
{
   document.getElementById(divid).style.visibility="hidden";
}

function bodyLoad()
{
    hideDiv("autocomplete");
    document.search.q.focus();
}