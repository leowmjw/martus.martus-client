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
package org.martus.client.swingui.jfx.landing;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.actions.ActionMenuBackupMyKeyPair;
import org.martus.client.swingui.actions.ActionMenuChangeUserNamePassword;
import org.martus.client.swingui.actions.ActionMenuContactInfo;
import org.martus.client.swingui.actions.ActionMenuCreateNewBulletin;
import org.martus.client.swingui.actions.ActionMenuManageContacts;
import org.martus.client.swingui.actions.ActionMenuPreferences;
import org.martus.client.swingui.actions.ActionMenuQuickSearch;
import org.martus.client.swingui.actions.ActionMenuSelectServer;
import org.martus.client.swingui.actions.ActionMenuStopStartTor;
import org.martus.client.swingui.jfx.FxContentController;
import org.martus.client.swingui.jfx.FxInSwingFrameController;

public class FxLandingShellController extends FxInSwingFrameController
{
	public FxLandingShellController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}

	@Override
	public String getFxmlLocation()
	{
		return "landing/LandingShell.fxml";
	}
	
	@Override
	public void initializeMainContentPane()
	{
		updateTorStatus();
	}

	@Override
	public void setContentPane(FxContentController contentController) throws Exception
	{
		Parent createContents = contentController.createContents();
		mainContentPane.getChildren().addAll(createContents);
	}

	private void updateTorStatus()
	{
	}

	@FXML
	private void onPreferences(ActionEvent event)
	{
		getStage().doAction(new ActionMenuPreferences(getMainWindow()));
	}

	@FXML
	private void onManageContacts(ActionEvent event)
	{
		getStage().doAction(new ActionMenuManageContacts(getMainWindow()));
	}

	@FXML
	private void onConfigureServer(ActionEvent event)
	{
		getStage().doAction(new ActionMenuSelectServer(getMainWindow()));
	}
	
	@FXML
	private void onChangeUsernameAndPassword(ActionEvent event)
	{
		getStage().doAction(new ActionMenuChangeUserNamePassword(getMainWindow()));
	}
	
	@FXML
	private void onCreateNewAccount(ActionEvent event)
	{
		//TODO: add doAction
	}
	
	@FXML
	private void onQuickSearch(ActionEvent event)
	{
		getStage().doAction(new  ActionMenuQuickSearch(getMainWindow(), searchText.getText()));
	}

	@FXML
	private void onCreateNewBulletin(ActionEvent event)
	{
		getStage().doAction(new ActionMenuCreateNewBulletin(getMainWindow()));
	}
	
	@FXML
	private void onTor(ActionEvent event)
	{
		getStage().doAction(new ActionMenuStopStartTor(getMainWindow()));
	}

	@FXML
	private void onContactInformation(ActionEvent event)
	{
		getStage().doAction(new ActionMenuContactInfo(getMainWindow()));
	}
	
	@FXML
	private void onBackupKeypair(ActionEvent event)
	{
		getStage().doAction(new ActionMenuBackupMyKeyPair(getMainWindow()));
	}

	@FXML
	protected TextField searchText;
	
	@FXML
	protected ToggleButton toggleButtonTor;
	
	@FXML
	protected AnchorPane mainContentPane;

}