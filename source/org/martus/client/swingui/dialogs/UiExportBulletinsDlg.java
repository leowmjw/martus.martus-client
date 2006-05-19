/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2006, Beneficent
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import org.martus.client.core.BulletinXmlExporter;
import org.martus.client.core.MartusApp;
import org.martus.client.swingui.UiMainWindow;
import org.martus.clientside.UiLocalization;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.swing.UiButton;
import org.martus.swing.UiCheckBox;
import org.martus.swing.UiFileChooser;
import org.martus.swing.UiList;
import org.martus.swing.UiScrollPane;
import org.martus.swing.UiVBox;
import org.martus.swing.UiWrappedTextArea;
import org.martus.swing.Utilities;
import org.martus.util.UnicodeWriter;

public class UiExportBulletinsDlg extends JDialog implements ActionListener
{
	public UiExportBulletinsDlg(UiMainWindow mainWindowToUse, Vector bulletinsToExport, String defaultName)
	{
		super(mainWindowToUse, "", true);
		mainWindow = mainWindowToUse;
		bulletins = bulletinsToExport;
		defaultFileName = defaultName;
		constructDialog();
	}

	private void constructDialog()
	{
		UiLocalization localization = mainWindow.getLocalization();
		setTitle(localization.getWindowTitle("ExportBulletins"));
		
		includePrivate = new UiCheckBox(localization.getFieldLabel("ExportPrivateData"));
		includeAttachments = new UiCheckBox(localization.getFieldLabel("ExportAttachments"));
		ok = new UiButton(localization.getButtonLabel("Continue"));
		ok.addActionListener(this);
		
		cancel = new UiButton(localization.getButtonLabel("cancel"));
		cancel.addActionListener(this);
		
		
		String[] titles = extractTitles(mainWindow, bulletins);
		UiList bulletinList = new UiList(titles);
		UiScrollPane tocMsgAreaScrollPane = new UiScrollPane(bulletinList,
				UiScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				UiScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		tocMsgAreaScrollPane.setPreferredSize(new Dimension(580, 100));
		
		UiVBox upperStuff = new UiVBox();
		upperStuff.addSpace();
		upperStuff.addCentered(new UiWrappedTextArea(localization.getFieldLabel("ExportBulletinDetails")));
		upperStuff.addSpace();
		upperStuff.addCentered(tocMsgAreaScrollPane);
		upperStuff.addSpace();
		upperStuff.add(includePrivate);
		upperStuff.addSpace();
		upperStuff.add(includeAttachments);
		upperStuff.addSpace();
		
		UiVBox vBoxAll = new UiVBox();
		vBoxAll.add(upperStuff);
		
		vBoxAll.add(new Component[]{ok, cancel});
		getContentPane().add(vBoxAll);
		
		Utilities.centerDlg(this);
		setResizable(true);
		setVisible(true);
	}

	public static String[] extractTitles(UiMainWindow window, Vector bulletins)
	{
		String[] titles = new String[bulletins.size()];
		for (int i = 0; i < titles.length; i++)
		{
			Bulletin b = (Bulletin)bulletins.get(i);
			String bulletinTitle = b.get(BulletinConstants.TAGTITLE);
			if(bulletinTitle == null || bulletinTitle.length() == 0)
				bulletinTitle = window.getLocalization().getFieldLabel("UntitledBulletin");
			titles[i] = bulletinTitle;
		}
		return titles;
	}

	File askForDestinationFile()
	{
		String windowTitle = mainWindow.getLocalization().getWindowTitle("ExportBulletinsSaveAs");
		if(defaultFileName != null && defaultFileName.length() > 0)
			defaultFileName += MartusApp.MARTUS_IMPORT_EXPORT_EXTENSION;
		else
			defaultFileName = null;
	
		UiFileChooser.FileDialogResults results = UiFileChooser.displayFileSaveDialog(UiExportBulletinsDlg.this, windowTitle, defaultFileName);
		if (results.wasCancelChoosen())
			return null;

		File destFile = results.getChosenFile();
		if(destFile.exists())
			if(!mainWindow.confirmDlg("OverWriteExistingFile"))
				return null;

		return destFile;
	}

	boolean userWantsToExportPrivate()
	{
		return includePrivate.isSelected();
	}

	boolean userWantsToExportAttachments()
	{
		return includeAttachments.isSelected();
	}

	void doExport(File destFile)
	{
		BulletinXmlExporter exporter = new BulletinXmlExporter(mainWindow.getApp(), mainWindow.getLocalization());
		try
		{
			mainWindow.setWaitingCursor();
			UnicodeWriter writer = new UnicodeWriter(destFile);
			boolean userWantsToExportPrivate = userWantsToExportPrivate();
			boolean userWantsToExportAttachments = userWantsToExportAttachments();
			exporter.exportBulletins(writer, bulletins, userWantsToExportPrivate, userWantsToExportAttachments, destFile.getParentFile());
			writer.close();
			mainWindow.resetCursor();
			int numberOfMissingAttachment = exporter.getNumberOfFailingAttachments();
			if(numberOfMissingAttachment > 0)
			{
				mainWindow.notifyDlg("ExportCompleteMissingAttachments", getTokenReplacementImporter(numberOfMissingAttachment));
			}
			else
				mainWindow.notifyDlg("ExportComplete");
		}
		catch (IOException e)
		{
			mainWindow.resetCursor();
			mainWindow.notifyDlg("ErrorWritingFile");
		}
	}

	Map getTokenReplacementImporter(int numberOfMissingAttachment) 
	{
		HashMap map = new HashMap();
		map.put("#AttachmentsNotExported#", Integer.toString(numberOfMissingAttachment));
		return map;
	}
	
	
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource().equals(ok))
		{
			boolean hasUnknown = false;
			for (int i = 0; i < bulletins.size(); i++)
			{
				Bulletin b = (Bulletin)bulletins.get(i);
				if(b.hasUnknownTags() || b.hasUnknownCustomField())
					hasUnknown = true;
			}
			if(hasUnknown)
			{
				if(!mainWindow.confirmDlg("ExportUnknownTags"))
					return;
			}
			
			if(userWantsToExportPrivate())
			{
				if(!mainWindow.confirmDlg("ExportPrivateData"))
					return;
			}

			File destFile = askForDestinationFile();
			if(destFile == null)
				return;

			doExport(destFile);
		}

		dispose();
	}


	UiMainWindow mainWindow;
	Vector bulletins;
	JCheckBox includePrivate;
	JCheckBox includeAttachments;
	JButton ok;
	JButton cancel;
	String defaultFileName;
}
