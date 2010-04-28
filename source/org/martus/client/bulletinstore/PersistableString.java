/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

package org.martus.client.bulletinstore;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;

public class PersistableString extends PersistableObject
{
	public PersistableString(String text)
	{
		value = text;
		logUtfExceptionsForDebuggingPurposes();
	}

	/**
	 * FIXME: This method should be deleted after the debugging is completed
	 **/
	private void logUtfExceptionsForDebuggingPurposes()
	{
		try
		{
			new DataOutputStream(new ByteArrayOutputStream()).writeUTF(value);
		}
		catch(UTFDataFormatException e)
		{
			System.out.println("UTF error in: ");
			System.out.println(value);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public PersistableString(DataInputStream dataIn) throws IOException
	{
		value = dataIn.readUTF();
	}
	
	public int getType()
	{
		return TYPE_STRING;
	}
	
	public String toString()
	{
		return value;
	}
	
	public boolean equals(Object rawOther)
	{
		if(! (rawOther instanceof PersistableString))
			return false;
		
		PersistableString other = (PersistableString)rawOther;
		return (value.equals(other.value));
	}
	
	public int hashCode()
	{
		return value.hashCode();
	}

	void internalWriteTo(DataOutputStream dataOut) throws IOException 
	{
		dataOut.writeUTF(value);
	}

	private String value;
}
