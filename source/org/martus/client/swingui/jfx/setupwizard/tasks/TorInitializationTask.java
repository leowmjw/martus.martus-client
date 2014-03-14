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
package org.martus.client.swingui.jfx.setupwizard.tasks;

import org.martus.client.core.MartusApp;

public class TorInitializationTask extends AbstractAppTask
{
	public TorInitializationTask(MartusApp appToUse)
	{
		super(appToUse);
	}

	@Override
	protected Void call() throws Exception
	{
		app.getTransport().setProgressMeter(progress);
		app.startOrStopTorAsRequested();
		//FIXME: remove this while loop/Sleep and adjust 	task.stateProperty().addListener(new TaskStateChangeHandler());
		//With some handleTaskComplete override instead of the task ending which causes the Successful state change to trigger
		while (!app.getTransport().isReady())
		{
			Thread.sleep(1000);
		}
		return null;
	}
}