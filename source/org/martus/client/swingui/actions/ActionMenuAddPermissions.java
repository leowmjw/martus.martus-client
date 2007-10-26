/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2007, Beneficent
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
package org.martus.client.swingui.actions;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JDialog;

import org.martus.client.swingui.UiBulletinTitleListComponent;
import org.martus.client.swingui.UiHeadquartersTable;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.bulletincomponent.HeadQuartersTableModelEdit;
import org.martus.clientside.UiLocalization;
import org.martus.common.HQKeys;
import org.martus.common.MartusLogger;
import org.martus.common.HQKeys.HQsException;
import org.martus.common.bulletin.Bulletin;
import org.martus.swing.UiButton;
import org.martus.swing.UiLabel;
import org.martus.swing.UiScrollPane;
import org.martus.swing.UiWrappedTextArea;
import org.martus.swing.Utilities;

import com.jhlabs.awt.GridLayoutPlus;

public class ActionMenuAddPermissions extends UiMenuAction
{
	public ActionMenuAddPermissions(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse, "AddPermissions");
	}

	public boolean isEnabled()
	{
		if(!mainWindow.isAnyBulletinSelected())
			return false;
		
		try
		{
			return (mainWindow.getApp().getAllHQKeys().size() > 0);
		} 
		catch (HQsException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void actionPerformed(ActionEvent ae)
	{
		addPermissionsToBulletins(mainWindow.getSelectedBulletins("UnexpectedError"));
	}
	
	private void addPermissionsToBulletins(Vector selectedBulletins)
	{
		Vector ourBulletins = extractOurBulletins(selectedBulletins, mainWindow.getApp().getAccountId());
		if(ourBulletins.size() == 0)
		{
			mainWindow.notifyDlg("AddPermissionsZeroBulletinsOurs");
			return;
		}
		try
		{
			HQKeys hqKeys = mainWindow.getApp().getAllHQKeys();
			AddPermissionsDialog dlg = new AddPermissionsDialog(mainWindow, selectedBulletins, ourBulletins, hqKeys);
			dlg.setVisible(true);
		} 
		catch (HQsException e)
		{
			MartusLogger.logException(e);
			mainWindow.unexpectedErrorDlg();
		}
	}

	private Vector extractOurBulletins(Vector allBulletins, String ourAccountId)
	{
		Vector ourBulletins = new Vector();
		
		for(int i = 0; i < allBulletins.size(); ++i)
		{
			Bulletin b = (Bulletin)allBulletins.get(i);
			if(b.getAccount().equals(ourAccountId))
				ourBulletins.add(b);
		}
		
		return ourBulletins;
	}
	static class AddPermissionsDialog extends JDialog
	{
		public AddPermissionsDialog(UiMainWindow mainWindow, Vector allBulletins, Vector ourBulletins, HQKeys hqKeys)
		{
			super(mainWindow);
			setModal(true);
			Container contentPane = getContentPane();
			contentPane.setLayout(new GridLayoutPlus(0, 1, 2, 2, 2, 2));
			UiLocalization localization = mainWindow.getLocalization();
			setTitle(localization.getWindowTitle("AddPermissions"));

			String overview = localization.getFieldLabel("AddPermissionsOverview");
			contentPane.add(new UiWrappedTextArea(overview));

			
			UiBulletinTitleListComponent list = new UiBulletinTitleListComponent(mainWindow, ourBulletins);
			contentPane.add(new UiScrollPane(list));
			
			// if any bulletins are not ours, tell user why they are not listed
			if(ourBulletins.size() != allBulletins.size())
			{
				String skippingBulletinsNotOurs = localization.getFieldLabel("SkippingBulletinsNotOurs");
				contentPane.add(new UiWrappedTextArea(skippingBulletinsNotOurs));
			}
			
			contentPane.add(blankLine());
			String chooseHeadquartersToAdd = localization.getFieldLabel("ChooseHeadquartersToAdd");
			contentPane.add(new UiWrappedTextArea(chooseHeadquartersToAdd));
			
			HeadQuartersTableModelEdit model = new HeadQuartersTableModelEdit(localization);
			model.addKeys(hqKeys);
			UiHeadquartersTable hqTable = new UiHeadquartersTable(model);
			hqTable.setMaxColumnWidthToHeaderWidth(0);
			UiScrollPane hqScroller = new UiScrollPane(hqTable);
			contentPane.add(hqScroller);
			
			contentPane.add(blankLine());

			Box buttonBox = Box.createHorizontalBox();
			UiButton okButton = new UiButton(localization.getButtonLabel("AddPermissions"));
			okButton.addActionListener(new OkButtonHandler());
			UiButton cancelButton = new UiButton(localization.getCancelButtonLabel());
			cancelButton.addActionListener(new CancelButtonHandler());
			Component[] buttons = new Component[] {
					Box.createHorizontalGlue(),
					okButton,
					cancelButton,
			};
			Utilities.addComponentsRespectingOrientation(buttonBox, buttons);
			contentPane.add(buttonBox);
			
			Utilities.centerDlg(this);
			setResizable(true);
		}

		private UiLabel blankLine()
		{
			return new UiLabel(" ");
		}
		
		void doOk()
		{
			dispose();
		}
		
		void doCancel()
		{
			dispose();
		}
		
		class OkButtonHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				doOk();
			}
		}
		
		class CancelButtonHandler implements ActionListener
		{
			public void actionPerformed(ActionEvent e)
			{
				doCancel();
			}
			
		}
	}
}
