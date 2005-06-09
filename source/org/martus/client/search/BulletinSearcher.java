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

import org.martus.client.core.SafeReadableBulletin;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.utilities.MartusFlexidate;

public class BulletinSearcher
{
	public BulletinSearcher(SearchTreeNode nodeToMatch)
	{
		node = nodeToMatch;
	}

	public boolean doesMatch(SafeReadableBulletin b)
	{
		if(node.getOperation() == SearchTreeNode.VALUE)
			return doesValueMatch(b);

		BulletinSearcher left = new BulletinSearcher(node.getLeft());
		BulletinSearcher right = new BulletinSearcher(node.getRight());

		if(node.getOperation() == SearchTreeNode.AND)
			return left.doesMatch(b) && right.doesMatch(b);

		if(node.getOperation() == SearchTreeNode.OR)
			return left.doesMatch(b) || right.doesMatch(b);

		return false;
	}

	private boolean doesValueMatch(SafeReadableBulletin b)
	{
		String fieldToSearch = node.getField();
		String searchForValue = node.getValue();
		if(fieldToSearch == null)
			return b.contains(searchForValue);

		int compareOp = node.getComparisonOperator();
		String thisValue = b.get(fieldToSearch);
		
		if(b.getFieldType(fieldToSearch) == FieldSpec.TYPE_DATERANGE)
			return doDateComparison(thisValue, compareOp, searchForValue);
		
		switch(compareOp)
		{
			case SearchTreeNode.CONTAINS:
				return b.doesFieldContain(fieldToSearch, searchForValue);
			case SearchTreeNode.LESS: 
				return (thisValue.compareTo(searchForValue) < 0);
			case SearchTreeNode.LESS_EQUAL: 
				return (thisValue.compareTo(searchForValue) <= 0);
			case SearchTreeNode.GREATER: 
				return (thisValue.compareTo(searchForValue) > 0);
			case SearchTreeNode.GREATER_EQUAL: 
				return (thisValue.compareTo(searchForValue) >= 0);
			case SearchTreeNode.OVERLAPS:
				return false;
		}
		
		System.out.println("BulletinSearcher.doesValueMatch: Unknown op: " + compareOp);
		return false;
	}
	
	private boolean doDateComparison(String thisValue, int compareOp, String searchForValue)
	{
		MartusFlexidate searchFor = MartusFlexidate.createFromMartusDateString(searchForValue);
		MartusFlexidate thisRange = MartusFlexidate.createFromMartusDateString(thisValue);
		
		long thisBegin = thisRange.getBeginDate().getTime();
		long thisEnd = thisRange.getEndDate().getTime();
		long searchBegin = searchFor.getBeginDate().getTime();
		long searchEnd = searchFor.getEndDate().getTime();

		if(compareOp == SearchTreeNode.CONTAINS)
		{
			boolean isBeginningInRange = (searchBegin >= thisBegin && searchBegin <= thisEnd);
			boolean isEndInRange = (searchEnd >= thisBegin && searchEnd <= thisEnd);
			
			return (isBeginningInRange && isEndInRange);
		}

		if(compareOp == SearchTreeNode.OVERLAPS)
		{
			boolean isBeginningInRange = (searchBegin <= thisEnd);
			boolean isEndInRange = (searchEnd >= thisBegin);
			
			return (isBeginningInRange && isEndInRange);
		}

		return false;
	}

	SearchTreeNode node;
}
