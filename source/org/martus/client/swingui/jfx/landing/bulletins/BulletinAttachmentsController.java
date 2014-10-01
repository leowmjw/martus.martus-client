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

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;

import org.martus.client.core.FxBulletin;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.generic.FxController;
import org.martus.client.swingui.jfx.generic.controls.FxButtonTableCellFactory;


public class BulletinAttachmentsController extends FxController
{
	public BulletinAttachmentsController(UiMainWindow mainWindowToUse, FxBulletin bulletinToUse)
	{
		super(mainWindowToUse);
		bulletin = bulletinToUse;
	}

	@Override
	public void initialize(URL location, ResourceBundle bundle)
	{
		super.initialize(location, bundle);
		initalizeColumns();
		initalizeItemsTable();
	}
	
	private void initalizeItemsTable()
	{
		attachmentsProvider = new AttachmentListProvider(bulletin.getAttachments(), getMainWindow().getStore().getDatabase());
		attachmentsTable.setItems(attachmentsProvider);
		attachmentsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		BooleanBinding nonEmptyTableBinding = Bindings.isNotEmpty(attachmentsTable.getItems());
		attachmentsTable.visibleProperty().bind(nonEmptyTableBinding);
	}	
	
	private void initalizeColumns()
	{
		nameColumn.setCellValueFactory(new PropertyValueFactory<AttachmentTableRowData, String>(AttachmentTableRowData.ATTACHMENT_NAME_PROPERTY_NAME));
		nameColumn.setCellFactory(TextFieldTableCell.<AttachmentTableRowData>forTableColumn());

		sizeColumn.setCellValueFactory(new PropertyValueFactory<AttachmentTableRowData, String>(AttachmentTableRowData.ATTACHMENT_SIZE_PROPERTY_NAME));
		sizeColumn.setCellFactory(TextFieldTableCell.<AttachmentTableRowData>forTableColumn());

		Image removeImage = new Image(REMOVE_ATTACHMENT_IMAGE_PATH);
		removeColumn.setCellFactory(new FxButtonTableCellFactory(removeImage, () -> removeSelectedAttachment()));
		removeColumn.setCellValueFactory(new PropertyValueFactory<Object, Boolean>(AttachmentTableRowData.ATTACHMENT_REMOVE_PROPERTY_NAME));
	}
	
	
	private void removeSelectedAttachment()
	{
	}


	@FXML
	private void onAddAttachment(ActionEvent event) 
	{
	}
	
	@Override
	public String getFxmlLocation()
	{
		return "landing/bulletins/FxAttachments.fxml";
	}
	
	final private String REMOVE_ATTACHMENT_IMAGE_PATH = "/org/martus/client/swingui/jfx/images/trash.png";

	@FXML 
	private TableView attachmentsTable;
	
	@FXML
	protected TableColumn<AttachmentTableRowData, String> nameColumn;

	@FXML
	protected TableColumn<AttachmentTableRowData, String> sizeColumn;	
	
	@FXML
	protected TableColumn<Object, Boolean> removeColumn;

	private FxBulletin bulletin;
	private AttachmentListProvider attachmentsProvider;
}
