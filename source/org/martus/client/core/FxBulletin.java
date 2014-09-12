/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2014, Beneficent
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

import java.util.HashMap;
import java.util.Vector;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import org.martus.common.FieldSpecCollection;
import org.martus.common.HeadquartersKeys;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.packet.UniversalId;

/**
 * This class wraps a Bulletin object, exposing each data member as a 
 * Property, to make life easier when working in JavaFX.
 */
public class FxBulletin
{
	public FxBulletin()
	{
		fieldProperties = new HashMap<String, SimpleStringProperty>();
	}

	public void copyDataFromBulletin(Bulletin b)
	{
		clear();
		
		universalIdProperty = new ReadOnlyObjectWrapper<UniversalId>(b.getUniversalId());
		versionProperty = new SimpleIntegerProperty(b.getVersion());
		authorizedToReadProperty = new ReadOnlyObjectWrapper<HeadquartersKeys>(b.getAuthorizedToReadKeys());

		setFieldPropertiesFromBulletinSection(b, b.getTopSectionFieldSpecs());
		setFieldPropertiesFromBulletinSection(b, b.getBottomSectionFieldSpecs());
	}

	public void copyDataToBulletin(Bulletin modified) throws Exception
	{
		modified.getFieldDataPacket().setFieldSpecs(fieldSpecs);
		modified.getPrivateFieldDataPacket().setFieldSpecs(new FieldSpecCollection());
		
		for(int i = 0; i < fieldSpecs.size(); ++i)
		{
			FieldSpec fieldSpec = fieldSpecs.get(i);
			String fieldTag = fieldSpec.getTag();
			String value = getFieldProperty(fieldTag).getValue();
			modified.set(fieldTag, value);
//			System.out.println("copyDataToBulletin " + fieldTag + ":" + value);
		}
	}

	public ReadOnlyObjectWrapper<UniversalId> universalIdProperty()
	{
		return universalIdProperty;
	}
	
	public SimpleIntegerProperty getVersionProperty()
	{
		return versionProperty;
	}

	public ReadOnlyObjectWrapper<HeadquartersKeys> authorizedToReadProperty()
	{
		return authorizedToReadProperty;
	}

	public Vector<FieldSpec> getFieldSpecs()
	{
		Vector<FieldSpec> specs = new Vector<FieldSpec>();
		for(int i = 0; i < fieldSpecs.size(); ++i)
		{
			FieldSpec fieldSpec = fieldSpecs.get(i);
			specs.add(fieldSpec);
		}
		
		return specs;
	}

	public SimpleStringProperty getFieldProperty(String tag)
	{
		return fieldProperties.get(tag);
	}
	
	private void clear()
	{
		if(universalIdProperty != null)
		{
			universalIdProperty.setValue(null);
			universalIdProperty = null;
		}
		
		if(authorizedToReadProperty != null)
		{
			authorizedToReadProperty.setValue(null);
			authorizedToReadProperty = null;
		}

		if(versionProperty != null)
		{
			versionProperty.setValue(null);
			versionProperty = null;
		}
		
		fieldProperties.forEach((key, property) -> property.setValue(null));
		fieldProperties.clear();
		
		fieldSpecs = new FieldSpecCollection();
	}
	
	private void setFieldPropertiesFromBulletinSection(Bulletin b, FieldSpecCollection bulletinFieldSpecs)
	{
		for(int i = 0; i < bulletinFieldSpecs.size(); ++i)
		{
			FieldSpec fieldSpec = bulletinFieldSpecs.get(i);
			fieldSpecs.add(fieldSpec);
			String fieldTag = fieldSpec.getTag();
			String value = b.get(fieldTag);
			setField(fieldTag, value);
//			System.out.println("copyDataFromBulletin " + fieldTag + ":" + value);
		}
	}

	private void setField(String fieldTag, String value)
	{
		SimpleStringProperty property = new SimpleStringProperty(value);
		fieldProperties.put(fieldTag, property);
	}

	private ReadOnlyObjectWrapper<UniversalId> universalIdProperty;
	private HashMap<String, SimpleStringProperty> fieldProperties;
	private FieldSpecCollection fieldSpecs;
	private SimpleIntegerProperty versionProperty;
	private ReadOnlyObjectWrapper<HeadquartersKeys> authorizedToReadProperty;

}