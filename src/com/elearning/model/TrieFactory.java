package com.elearning.model;

public class TrieFactory 
{
	private static Trie trie;
	
	public static void makeTrie()
	{
		trie = new Trie();
		trie.createTrie();
	}
	public static Trie getTrie()
	{
		if(trie == null)
		{
			makeTrie();
		}
		return trie;
	}
}
