/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2004, Beneficent
Technology, Inc. (Benetech).

Martus is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either
version 2 of the License, or (at your option) any later
version with the additions and exceptions described in the
accompanying Martus license file entitled "license.txt".

It is distributed WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, including warranties of fitness of purpose or
merchantability.  See the accompanying Martus License and
GPL license for more details on the required license terms
for this software.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.

*/

package org.martus.client.search;

public class SearchTreeNode
{
	public final static int VALUE = 0;
	public final static int OR = 1;
	public final static int AND = 2;
	
	public final static int CONTAINS = 0;
	public final static int GREATER = 1;
	public final static int GREATER_EQUAL = 2; 
	public final static int LESS = 3; 
	public final static int LESS_EQUAL = 4;
	
	private final static String GREATER_EQUAL_STRING = ">=";
	private final static String LESS_EQUAL_STRING = "<=";
	private final static String GREATER_STRING = ">";
	private final static String LESS_STRING = "<";

	public SearchTreeNode(String value)
	{
		nodeOp = VALUE;
		compareOp = CONTAINS;
		
		int colonAt = value.indexOf(':');
		if(!value.startsWith("\"") && colonAt >= 0)
		{
			fieldTag = value.substring(0, colonAt);
			value = value.substring(colonAt + 1);
			String op  = getComparisonOp(value);
			value = value.substring(op.length());
			compareOp = convertComparisonOpStringToValue(op);
		}
		
		nodeValue = withoutQuotes(value);
	}
	
	public static String getComparisonOp(String value)
	{
		for(int i=0; i < comparisonOpsLongestFirst.length; ++i)
		{
			if(value.startsWith(comparisonOpsLongestFirst[i]))
				return comparisonOpsLongestFirst[i];
		}
		
		return "";
	}
	
	private static int convertComparisonOpStringToValue(String op)
	{
		if(op.equals(GREATER_STRING))
			return GREATER;
		if(op.equals(GREATER_EQUAL_STRING))
			return GREATER_EQUAL;
		if(op.equals(LESS_STRING))
			return LESS;
		if(op.equals(LESS_EQUAL_STRING))
			return LESS_EQUAL;
		
		return CONTAINS;
	}

	private String withoutQuotes(String rawValue)
	{
		if(rawValue.startsWith("\""))
			rawValue = rawValue.substring(1, rawValue.length() - 1);
		return rawValue;
	}
	
	public SearchTreeNode(int op, SearchTreeNode left, SearchTreeNode right)
	{
		nodeOp = op;
		nodeLeft = left;
		nodeRight = right;
	}
	
	public String getField()
	{
		return fieldTag;
	}

	public String getValue()
	{
		return nodeValue;
	}
	
	public int getComparisonOperator()
	{
		return compareOp;
	}

	public int getOperation()
	{
		return nodeOp;
	}

	public SearchTreeNode getLeft()
	{
		return nodeLeft;
	}

	public SearchTreeNode getRight()
	{
		return nodeRight;
	}

	private final static String[] comparisonOpsLongestFirst = 
	{
		GREATER_EQUAL_STRING, 
		LESS_EQUAL_STRING, 
		GREATER_STRING, 
		LESS_STRING, 
	};
	
	private String nodeValue;
	private String fieldTag;
	private int nodeOp;
	private int compareOp;
	private SearchTreeNode nodeLeft;
	private SearchTreeNode nodeRight;

}
