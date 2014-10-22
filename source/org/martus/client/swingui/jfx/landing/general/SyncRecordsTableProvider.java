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
package org.martus.client.swingui.jfx.landing.general;

import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.martus.client.bulletinstore.ClientBulletinStore;
import org.martus.client.swingui.UiMainWindowInterface;
import org.martus.client.swingui.jfx.generic.data.ArrayObservableList;
import org.martus.client.swingui.tablemodels.RetrieveTableModel;
import org.martus.common.BulletinSummary;
import org.martus.common.MartusUtilities;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.packet.UniversalId;

public class SyncRecordsTableProvider extends ArrayObservableList<ServerSyncTableRowData>
{
	public SyncRecordsTableProvider(UiMainWindowInterface mainWindowToUse)
	{
		super(INITIAL_CAPACITY);
		mainWindow = mainWindowToUse;
		allRows = new ArrayObservableList(INITIAL_CAPACITY);
	}
	
	public void show(int location)
	{
		clear();
		if(location != ServerSyncTableRowData.LOCATION_ANY)
		{
			for (Iterator iterator = allRows.iterator(); iterator.hasNext();)
			{
				ServerSyncTableRowData rowData = (ServerSyncTableRowData) iterator.next();
				if(rowData.getRawLocation() == location)
					add(rowData);
			}
			return;
		}
		addAll(allRows);
	}
		
	public void addServerMyDrafts(Vector summaries) throws Exception
	{
		for (Iterator iterator = summaries.iterator(); iterator.hasNext();)
		{
			BulletinSummary summary = (BulletinSummary) iterator.next();
			ServerSyncTableRowData bulletinData = new ServerSyncTableRowData(summary, mainWindow.getApp());
			allRows.add(bulletinData);		
		}
	}
	
	public void addLocalBulletin(Set localUids) throws Exception
	{
		for(Iterator iter = localUids.iterator(); iter.hasNext();)
		{
			UniversalId leafBulletinUid = (UniversalId) iter.next();
			ServerSyncTableRowData bulletinData = getLocalBulletinData(leafBulletinUid);
			allRows.add(bulletinData);		
		}
	}

	protected ServerSyncTableRowData getLocalBulletinData(UniversalId leafBulletinUid) throws Exception
	{
		ClientBulletinStore clientBulletinStore = mainWindow.getStore();
		Bulletin bulletin = clientBulletinStore.getBulletinRevision(leafBulletinUid);
		int bulletinSizeBytes = MartusUtilities.getBulletinSize(clientBulletinStore.getDatabase(), bulletin.getBulletinHeaderPacket());
		Integer size = new Integer(RetrieveTableModel.getSizeInKbytes(bulletinSizeBytes));
		int location = ServerSyncTableRowData.LOCATION_LOCAL;  //TODO compare with whats on server first.
		ServerSyncTableRowData bulletinData = new ServerSyncTableRowData(bulletin, size, location, mainWindow.getApp());
		return bulletinData;
	}

	private UiMainWindowInterface mainWindow;
	private static final int INITIAL_CAPACITY = 500;
	private ArrayObservableList<ServerSyncTableRowData> allRows;
}