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
package org.martus.client.swingui.jfx.setupwizard;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

import org.martus.common.ContactKey;
import org.martus.common.fieldspec.CustomFieldTemplate;

public class ContactsWithTemplatesTableData
{
	public ContactsWithTemplatesTableData(ContactKey key, boolean rowSelectedToUse, CustomFieldTemplate selectedCustomFieldTemplateToUse, ObservableList<CustomFieldTemplate> customFieldTemplatesToChooseFromToUse) throws Exception
	{
		contactName = new SimpleStringProperty(key.getLabel());
		publicCode = new SimpleStringProperty(key.getPublicCode());
		rowSelected = new SimpleBooleanProperty(rowSelectedToUse);
		selectedCustomFieldTemplate = new SimpleObjectProperty(selectedCustomFieldTemplateToUse);
		customFieldTemplatesToChooseFrom = customFieldTemplatesToChooseFromToUse;
	}
	
	public String getContactName()
	{
		return contactName.get();
	}
	
	public void setContactName(String contactNameToUse)
	{
		contactName.set(contactNameToUse);
	}
	
	public String getPublicCode()
	{
		return publicCode.get();
	}
	
	public void setPublicCode(String publicCodeToUse)
	{
		this.publicCode.set(publicCodeToUse);
	}
	
	public boolean getRowSelected()
	{
		return rowSelected.get();
	}
	
	public SimpleBooleanProperty getRowSelectedProperty()
	{
		return rowSelected;
	}
	
	public void setRowSelected(boolean rowSelectedToUse)
	{
		rowSelected.set(rowSelectedToUse);
	}
	
	public CustomFieldTemplate getSelectedCustomFieldTemplate()
	{
		return selectedCustomFieldTemplate.get();
	}
	
	public void setSelectedCustomFieldTemplate(CustomFieldTemplate selectedTamplateNameToUse)
	{
		selectedCustomFieldTemplate.set(selectedTamplateNameToUse);
	}
	
	public void setCustomFieldTemplatesToChooseFrom(ObservableList<CustomFieldTemplate> customFieldTemplatesToChooseFromToUse)
	{
		customFieldTemplatesToChooseFrom.clear();
		customFieldTemplatesToChooseFrom.addAll(customFieldTemplatesToChooseFromToUse);
	}
	
	public ObservableList<CustomFieldTemplate> getCustomFieldTemplateChoices()
	{
		return customFieldTemplatesToChooseFrom;
	}
	
	private final SimpleStringProperty contactName;
	private final SimpleStringProperty publicCode;
	private final SimpleBooleanProperty rowSelected;
	private final SimpleObjectProperty<CustomFieldTemplate> selectedCustomFieldTemplate;
	private ObservableList<CustomFieldTemplate> customFieldTemplatesToChooseFrom;
}
