package com.elearning.model;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

class Node 
{
	private char content;
	private boolean marker; 
	private Collection<Node> child;
	private String word;
	public int[] currentRow;
		  
	public Node(char c)
	{
		child = new LinkedList<Node>();
		word = null;
		marker = false;
		content = c;
	}
	  
	 public Node subNode(char c)
	 {
		 if(child!=null)
		 {
			 for(Node eachChild:child)
			 {
				 if(eachChild.content == c)
				 {
					 return eachChild;
				 }
			 }
		 }
	  
		 return null;
	 }
	 
	 public void setMarker(boolean b)
	 {
		 marker = b;
	 }
	 
	 public boolean getMarker()
	 {
		 return marker;
	 }
	 
	 public Collection<Node> getCollection()
	 {
		 return child;
	 }
	 
	 public char getContent()
	 {
		 return content;
	 }
	 
	 public void setWord(String word)
	 {
		 this.word = word;
	 }
	 
	 public String getWord()
	 {
		 return word;
	 }
}

public class Trie
{
	private Node root;
	
	public Node createTree()
	{
		return new Node('\0');
	}
	
	public void traverse(Node node, String str, ArrayList<String> ac)
	{
		Collection<Node> children = node.getCollection();			
		for(Node eachChild:children)
		{
			if(eachChild.getMarker() != true)
			{
				traverse(eachChild, str + eachChild.getContent(), ac);
			}
			else
			{
				ac.add(str + eachChild.getContent());
			}
		}
	}
	
	public ArrayList<String> autocomplete(String str, ArrayList<String> ac)
	{
		int l = str.length();
		Node current = root;
		int i;
		boolean flag = true;
		
		for(i=0;i<l;i++)
		{
			if(current != null)
			{
				current = current.subNode(str.charAt(i));
			}
			else
			{
				flag = false;
				break;
			}
		}
		
		if(flag)
		{
			if(current != null)
			{
				traverse(current,str,ac);
			}
		}
		
		return ac;
	}
	
	public boolean find(String word)
	{
		Node current = root;
		int i;
		int l = word.length();
		
		for(i=0;i<l;i++)
		{
			if(current == null)
			{
				return false;
			}
			else
			{
				if(current.subNode(word.charAt(i)) == null)
				{
					return false;
				}
				else
				{
					current = current.subNode(word.charAt(i));
				}
			}
		}
		
		if(current.getMarker() == true)
		{
			return true;
		}
		
		return false;
	}
	
	public void searchRecursive(Node node, char letter, String word, int[] arr, ArrayList<String> results, int maxCost)
	{
		//arr is the previousRow[]
		int i;
		int len = word.length();
		node.currentRow = new int[len+1];
		
		//to account for the empty character
		node.currentRow[0] = arr[0] + 1;
		
		//dp part for one row
		int insertCost, deleteCost, replaceCost;
		for(i=1;i<=len;i++)
		{
			insertCost = node.currentRow[i-1] + 1;
			deleteCost = arr[i] + 1;
			
			if(word.charAt(i-1) != letter)
			{
				replaceCost = arr[i-1] + 1;
			}
			else
			{
				replaceCost = arr[i-1];
			}
			
			node.currentRow[i] = min(insertCost, deleteCost, replaceCost);
		}
		
		if(node.currentRow[len] <= maxCost && node.getMarker() == true)
		{
			results.add(node.getWord());
		}
		
		if(min(node.currentRow) <= maxCost)
		{
			Collection<Node> children = node.getCollection();
			for(Node eachChild: children)
			{
				searchRecursive(eachChild, eachChild.getContent(), word, node.currentRow, results, maxCost);
			}
		}
	}
	
	public int min(int a, int b, int c)
	{
		int t_min;
		if(a <= b)
		{
			t_min = a; 
		}
		else
		{
			t_min = b;
		}
		
		if(t_min <= c)
		{
			return t_min;
		}
		
		return c;
	}
	
	public int min(int[] arr)
	{
		int min = 9999;
		for(int i=1;i<arr.length;i++)
		{
			if(arr[i] < min)
			{
				min = arr[i];
			}
		}
		
		return min;
	}
	
	public ArrayList<String> search(String word, int maxCost)
	{
		int len = word.length();
		
		ArrayList<String> results = new ArrayList<String>();
		
		Node currNode = root;
		
		currNode.currentRow = new int[len+1];
				
		for(int i=0;i<=len;i++)
		{
			currNode.currentRow[i] = i;
		}
		
		Collection<Node> children = currNode.getCollection();
		for(Node eachChild:children)
		{
			searchRecursive(eachChild, eachChild.getContent(), word, currNode.currentRow, results, maxCost);
		}
		
		return results;
	}
	
	public void insertWord(String word)
	{
		Node current = root;
		int l = word.length();
		
		if(word.length() == 0)
		{
			current.setMarker(true);
		}
		
		for(int i=0;i<l;i++)
		{
			Node c = current.subNode(word.charAt(i));
			if(c!=null)
			{
			   current = c;
			}
			else
			{
				current.getCollection().add(new Node(word.charAt(i)));
			    current = current.subNode(word.charAt(i));
			}
			
			if(i==word.length()-1)
			{
				current.setMarker(true);
				current.setWord(word);
			}
		} 
	}
		
	public void createTrie()
	{
		//Create Tree
		root = createTree();
		
		File f = new File("C:/Users/roshan/Desktop/Project/wordlist.txt");
		try
		{
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			String str;
			while((str=dis.readLine()) != null)
			{
				insertWord(str);
			}
			dis.close();
		}
		catch(IOException io)
		{
			io.printStackTrace();
		}
	}
}