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

package org.martus.client.swingui.dialogs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.martus.client.swingui.UiLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.UiScrollPane;
import org.martus.common.HQKey;
import org.martus.common.HQKeys;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.packet.BulletinHistory;
import org.martus.common.packet.UniversalId;
import org.martus.swing.ParagraphLayout;
import org.martus.swing.UiTable;
import org.martus.swing.UiTextArea;
import org.martus.swing.Utilities;
import org.martus.util.Base64.InvalidBase64Exception;


public class UiBulletinDetailsDialog extends JDialog
{
	public UiBulletinDetailsDialog(UiMainWindow mainWindowToUse, Bulletin bulletinToShow, String tagQualifierToUse)
	{
		super(mainWindowToUse.getCurrentActiveFrame());
		
		mainWindow = mainWindowToUse;
		bulletin = bulletinToShow;
		tagQualifier = tagQualifierToUse;
		
		setTitle(getLocalization().getWindowTitle("BulletinDetailsDialog"));

		JPanel panel = new JPanel();
		panel.setLayout(new ParagraphLayout());
		panel.add(new JLabel(getLabel("AuthorPublicCode")), ParagraphLayout.NEW_PARAGRAPH);
		panel.add(createField(getPublicCode()));
		panel.add(new JLabel(getLabel("BulletinId")), ParagraphLayout.NEW_PARAGRAPH);
		panel.add(createField(bulletin.getLocalId()));

		HQKeys hqKeys = bulletin.getAuthorizedToReadKeys();
		if(hqKeys.size() > 0)
		{
			DefaultTableModel hqModel = new DefaultTableModel();
			hqModel.addColumn(getLabel("HQLabel"));
			hqModel.addColumn(getLabel("HQPublicCode"));
			hqModel.setRowCount(hqKeys.size());
			
			for(int i=0; i < hqKeys.size(); ++i)
			{
				HQKey key = hqKeys.get(i);
				String publicCode = key.getPublicKey();
				try
				{
					publicCode = key.getPublicCode();
				}
				catch (InvalidBase64Exception e)
				{
					e.printStackTrace();
				}
				
				hqModel.setValueAt(mainWindow.getApp().getHQLabelIfPresent(key), i, 0);
				hqModel.setValueAt(publicCode, i, 1);
			}
			UiTable hqTable = new UiTable(hqModel);
			hqTable.setColumnSelectionAllowed(false);
			hqTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			hqTable.setShowGrid(true);
			hqTable.resizeTable();
			hqTable.setEnabled(false);
			UiScrollPane hqScroller = new UiScrollPane(hqTable, getLocalization().getComponentOrientation());
	
			String hqText = getLabel("HQInfoFor" + tagQualifier); 
			JComponent hqInfo = createField(hqText);
	
			panel.add(new JLabel(""), ParagraphLayout.NEW_PARAGRAPH);
			panel.add(hqInfo);
			panel.add(new JLabel(getLabel("Headquarters")), ParagraphLayout.NEW_PARAGRAPH);
			panel.add(hqScroller);
		}
		
		BulletinHistory history = bulletin.getHistory();
		DefaultTableModel versionModel = new DefaultTableModel(); 
		versionModel.addColumn(getLabel("VersionNumber"));
		versionModel.addColumn(getLabel("VersionDate"));
		versionModel.addColumn(getLabel("VersionId"));
		versionModel.addColumn(getLabel("VersionTitle"));
		versionModel.setRowCount(history.size() + 1);

		for(int i=0; i < history.size(); ++i)
		{
			String localId = history.get(i);
			UniversalId uid = UniversalId.createFromAccountAndLocalId(bulletin.getAccount(), localId);
			populateVersionRow(versionModel, i, uid);
		}
		populateVersionRow(versionModel, history.size(), bulletin.getUniversalId());
		UiTable versionTable = new UiTable(versionModel);
		versionTable.setColumnSelectionAllowed(false);
		versionTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		versionTable.setShowGrid(true);
		versionTable.resizeTable();
		versionTable.setEnabled(false);
		UiScrollPane versionScroller = new UiScrollPane(versionTable, getLocalization().getComponentOrientation());

		panel.add(new JLabel(getLabel("History")), ParagraphLayout.NEW_PARAGRAPH);
		panel.add(versionScroller);
		
		JButton closeButton = new JButton(getLocalization().getButtonLabel("close"));
		closeButton.addActionListener(new CloseHandler());
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(closeButton);
		buttonBox.add(Box.createHorizontalGlue());

		getContentPane().add(panel);
		getContentPane().add(buttonBox, BorderLayout.SOUTH);
		
		Utilities.centerDlg(this);
		setResizable(true);
		
//		JFrame parent = getMainWindow().getCurrentActiveFrame();
		//getMainWindow().notifyDlg(parent, tagQualifier + "ViewBulletinDetails", map);
	}
	
	private void populateVersionRow(DefaultTableModel versionModel, int i, UniversalId uid)
	{
		String date = getLabel("UnknownDate");
		String title = getLabel("UnknownTitle");
		Bulletin b = mainWindow.getStore().getBulletinRevision(uid);
		if(b != null)
		{
			date = b.getLastSavedDateTime();
			title = b.get(Bulletin.TAGTITLE);
		}
		versionModel.setValueAt(new Integer(1+i), i, 0);
		versionModel.setValueAt(date, i, 1);
		versionModel.setValueAt(uid.getLocalId(), i, 2);
		versionModel.setValueAt(title, i, 3);
	}

	private JComponent createField(String text)
	{
		UiTextArea component = new UiTextArea(text, getLocalization().getComponentOrientation());
		component.setEditable(false);
		return component;
	}
	
	private UiLocalization getLocalization()
	{
		return mainWindow.getLocalization();
	}

	private String getPublicCode()
	{
		try
		{
			return MartusCrypto.computeFormattedPublicCode(bulletin.getAccount());
		}
		catch (InvalidBase64Exception e)
		{
			e.printStackTrace();
			return "";
		}		
	}

	private String getLabel(String tag)
	{
		return getLocalization().getFieldLabel("BulletinDetails" + tag);
	}
	
	class CloseHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			UiBulletinDetailsDialog.this.dispose();
		}
	}
	
	UiMainWindow mainWindow;
	Bulletin bulletin;
	String tagQualifier;

}
