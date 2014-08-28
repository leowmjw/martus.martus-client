/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2006, Beneficent
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

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Vector;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.filefilters.MartusBulletinArchiveFileFilter;
import org.martus.clientside.FormatFilter;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.bulletin.BulletinZipUtilities;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;

public class ActionMenuExportMba extends UiMenuAction implements ActionDoer
{
	public ActionMenuExportMba(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse, "ExportMBA");
	}
	public ActionMenuExportMba(UiMainWindow mainWindowToUse, Bulletin bulletinToUse)
	{
		this(mainWindowToUse);
		bulletinToExport = bulletinToUse;
	}

	public void actionPerformed(ActionEvent event)
	{
		doExportBulletinAsMba();
	}

	public void doExportBulletinAsMba()
	{
		try
		{
			Vector bulletins = mainWindow.getSelectedBulletins("ExportZeroBulletins");
			if(bulletins == null)
				return;
			if(bulletins.size()!=1)
				mainWindow.notifyDlg("ExportMbaSingleBulletinOnly");
			exportMbaBulletin((Bulletin)bulletins.get(0));
		} 
		catch (Exception e)
		{
			mainWindow.unexpectedErrorDlg(e);
		}
	}

	private void exportMbaBulletin(Bulletin bulletin) throws Exception
	{
		String defaultFilename = bulletin.toFileName();
		FormatFilter filter = new MartusBulletinArchiveFileFilter(getLocalization());
		File destination = getMainWindow().showFileSaveDialog("ExportMBA", defaultFilename, filter);
		if(destination == null)
			return;
		exportBulletinToMba(bulletin, destination);
	}

	private void exportBulletinToMba(Bulletin bulletin, File destination) throws Exception
	{
		ReadableDatabase db = mainWindow.getApp().getStore().getDatabase();
		DatabaseKey headerKey = DatabaseKey.createKey(bulletin.getUniversalId(), bulletin.getStatus()); 
		BulletinZipUtilities.exportBulletinPacketsFromDatabaseToZipFile(db, headerKey, destination, mainWindow.getApp().getSecurity());
	}

	@Override
	public void doAction()
	{
		try
		{
			exportMbaBulletin(bulletinToExport);
		} 
		catch (Exception e)
		{
			mainWindow.unexpectedErrorDlg(e);
		}
	}
	Bulletin bulletinToExport;
}
