/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2003, Beneficent
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

package org.martus.client.core;

public class QuickEraseOptions
{	
	public boolean isScrubSelected() {return scrubOption;}
	public boolean isDeleteKeyPairSelected(){return delKeyPairOption;}
	public boolean isExitWhenCompleteSelected() {return exitWhenCompleteOption;}
	public boolean isDonotPromptSelected() {return donotPromptOption;}

	
	public void setScrubOption(boolean option) 
	{
		scrubOption=option;
	}
	
	public void setDeleteKeyPairOption(boolean option) 
	{
		delKeyPairOption=option;
	}
	
	public void setExitWhenCompleteOption(boolean option)
	{
		exitWhenCompleteOption = option;
	}	
	
	public void setDonotPromptOption(boolean option)
	{
		donotPromptOption = option;
	}	
	
	public QuickEraseOptions(){}

	boolean scrubOption;
	boolean delKeyPairOption;
	boolean exitWhenCompleteOption;
	boolean donotPromptOption;
}
