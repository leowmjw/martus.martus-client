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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.UiProgressMeter;
import org.martus.clientside.UiLocalization;
import org.martus.swing.UiButton;
import org.martus.swing.UiLabel;

abstract public class UiProgressRetrieveDlg extends JDialog
{
	public UiProgressRetrieveDlg(UiMainWindow window, String tag)
	{
		super(window, window.getLocalization().getWindowTitle(tag), true);
		UiLocalization localization = window.getLocalization();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowEventHandler());
		cancel = new UiButton(localization.getButtonLabel("cancel"));
		cancel.addActionListener(new CancelHandler());
		cancel.setAlignmentX(JButton.CENTER_ALIGNMENT);
		bulletinCountMeter = new UiProgressMeter(this, localization);
		bulletinCountMeter.setStatusMessage(tag);
		updateBulletinCountMeter(0, 1);
		getContentPane().add(new UiLabel("    "), BorderLayout.EAST);
		getContentPane().add(new UiLabel("    "), BorderLayout.WEST);
	}

	class WindowEventHandler extends WindowAdapter
	{
		public void windowClosing(WindowEvent event)
		{
			requestExit();
		}
	}

	class CancelHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent ae)
		{
				requestExit();
		}
	}

	void requestExit()
	{
		isExitRequested = true;
		cancel.setEnabled(false);
	}

	public void finished()
	{
		dispose();
	}

	public boolean shouldExit()
	{
		return isExitRequested;
	}

	public void updateBulletinCountMeter(int currentValue, int maxValue)
	{
		bulletinCountMeter.updateProgressMeter(currentValue, maxValue);
	}


	public UiProgressMeter bulletinCountMeter;
	public JButton cancel;

	private boolean isExitRequested;
}
