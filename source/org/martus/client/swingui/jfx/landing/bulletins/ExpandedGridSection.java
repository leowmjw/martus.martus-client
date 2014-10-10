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
package org.martus.client.swingui.jfx.landing.bulletins;


import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.martus.client.core.FxBulletin;
import org.martus.client.core.FxBulletinField;
import org.martus.client.core.GridFieldData;
import org.martus.client.swingui.MartusLocalization;
import org.martus.common.MartusLogger;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.GridFieldSpec;

public class ExpandedGridSection extends TitledPane
{
	public ExpandedGridSection(FxBulletinField gridFieldToUse, MartusLocalization localizationToUse)
	{
		super();
		gridField = gridFieldToUse;
		localization = localizationToUse;
		
		setText(gridField.getLabel());
		
		createAndSetContent();
		
	}

	public void createAndSetContent()
	{
		itemBox = new VBox();
		GridFieldData gridData = gridField.gridDataProperty();
		gridData.forEach((rowData) -> addItemControls(rowData));
		
		HBox bottom = new HBox();
		Button appendItemButton = new Button("Add Item");
		appendItemButton.setOnAction((event) -> appendItem()); 
		bottom.getChildren().add(appendItemButton);

		mainBorderPane = new BorderPane();
		mainBorderPane.setCenter(itemBox);
		mainBorderPane.setBottom(bottom);
		setContent(mainBorderPane);
	}

	private void addItemControls(GridRowFields rowData)
	{
		itemBox.getChildren().add(createItem(rowData));
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(event -> deleteItem(rowData));
		itemBox.getChildren().add(deleteButton);
		itemBox.getChildren().add(new Separator(Orientation.HORIZONTAL));
	}
	
	private void deleteItem(GridRowFields rowData)
	{
		gridField.gridDataProperty().remove(rowData);
		createAndSetContent();
	}

	private Node createItem(GridRowFields rowData)
	{
		GridFieldSpec gridSpec = gridField.getGridFieldSpec();
		
		FxBulletin fxb = gridField.getBulletin();
		BulletinEditorSection section = new BulletinEditorSection(fxb, localization, "");
		for(int column = 0; column < gridSpec.getColumnCount(); ++column)
		{
			FieldSpec fieldSpec = gridSpec.getFieldSpec(column);
			String columnLabel = fieldSpec.getLabel();
			FxBulletinField field = rowData.get(columnLabel);
			try
			{
				section.addField(field);
			}
			catch(Exception e)
			{
				MartusLogger.logException(e);
				String errorMessage = getLocalization().getFieldLabel("notifyUnexpectedErrorcause");
				section.addErrorMessage(fieldSpec.getLabel(), errorMessage);
			}
		}
		
		return section;
	}

	private void appendItem()
	{
		gridField.appendEmptyGridRow();
		createAndSetContent();
	}
	
	public MartusLocalization getLocalization()
	{
		return localization;
	}

	private FxBulletinField gridField;
	private MartusLocalization localization;
	private BorderPane mainBorderPane;
	private VBox itemBox;
}
