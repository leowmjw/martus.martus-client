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
package org.martus.client.swingui.fields.attachments;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.tablemodels.AttachmentTableModel;
import org.martus.common.MartusLogger;
import org.martus.common.bulletin.AttachmentProxy;

class ViewSingleAttachmentPanel extends JPanel
{
	public ViewSingleAttachmentPanel(UiMainWindow mainWindowToUse, AttachmentTableModel modelToUse, AttachmentProxy proxyToUse)
	{
		super(new BorderLayout());
		mainWindow = mainWindowToUse;
		model = modelToUse;
		proxy = proxyToUse;

		setBorder(BorderFactory.createLineBorder(Color.BLACK));

		addHeader();

		DragSource dragSource = DragSource.getDefaultDragSource();
		dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, 
				new AttachmentDragHandler(mainWindow.getStore(), proxy));
	}

	private void addHeader()
	{
		header = new ViewAttachmentSummaryRow(mainWindow, model, this);
		add(header, BorderLayout.BEFORE_FIRST_LINE);
	}
	
	public AttachmentProxy getAttachmentProxy()
	{
		return proxy;
	}
	
	public void showImageInline()
	{
		if(!addInlineImage())
			return;
		isImageInline = true;
		header.showHideButton();
		validateParent();
		repaint();
	}

	private void validateParent()
	{
		Container top = getTopLevelAncestor();
		if(top != null)
			top.validate();
	}
	
	public void hideImage()
	{
		isImageInline = false;
		JLabel emptySpace = new JLabel();
		emptySpace.setVisible(false);
		add(emptySpace, BorderLayout.CENTER);
		header.showViewButton();
		validateParent();
		repaint();
	}

	private boolean addInlineImage()
	{
		try
		{
			InlineAttachmentComponent image = new InlineAttachmentComponent(mainWindow.getStore(), proxy);
			image.validate();
			if(!image.isValid())
				return false;
			add(image, BorderLayout.CENTER);
			return true;
		} 
		catch (Exception e)
		{
			MartusLogger.logException(e);
			return false;
		}
	}
	
	UiMainWindow mainWindow;
	AttachmentTableModel model;
	AttachmentProxy proxy;
	boolean isImageInline;
	ViewAttachmentSummaryRow header;
}