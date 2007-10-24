/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2001-2007, Beneficent
Technology, Inc. (The Benetech Initiative).

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

package org.martus.client.swingui.fields;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.martus.client.core.TransferableAttachmentList;
import org.martus.client.swingui.MartusLocalization;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.tablemodels.AttachmentTableModel;
import org.martus.common.MartusLogger;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.bulletin.BulletinLoader;
import org.martus.common.crypto.MartusCrypto;
import org.martus.common.crypto.MartusCrypto.CryptoException;
import org.martus.common.database.DatabaseKey;
import org.martus.common.database.ReadableDatabase;
import org.martus.common.packet.UniversalId;
import org.martus.common.packet.Packet.InvalidPacketException;
import org.martus.common.packet.Packet.SignatureVerificationException;
import org.martus.common.packet.Packet.WrongPacketTypeException;
import org.martus.swing.UiButton;
import org.martus.swing.UiFileChooser;
import org.martus.swing.UiLabel;
import org.martus.swing.Utilities;
import org.martus.util.StreamableBase64.InvalidBase64Exception;

import com.jhlabs.awt.Alignment;
import com.jhlabs.awt.GridLayoutPlus;

public class UiAttachmentViewer extends JPanel
{
	public UiAttachmentViewer(UiMainWindow mainWindowToUse)
	{
		GridLayoutPlus layout = new GridLayoutPlus(0, 1, 0, 0, 0, 0);
		setLayout(layout);
		
		mainWindow = mainWindowToUse;
		model = new AttachmentTableModel(mainWindow);

		updateTable();
	}

	MartusLocalization getLocalization()
	{
		return mainWindow.getLocalization();
	}
	
	MartusCrypto getSecurity()
	{
		return mainWindow.getApp().getSecurity();
	}

	public void updateTable()
	{
		removeAll();
		for(int row = 0; row < model.getRowCount(); ++row)
		{
			add(new ViewSingleAttachmentPanel(model.getAttachment(row)));
		}
	}
	
	public void addAttachment(AttachmentProxy a)
	{
		model.add(a);
		updateTable();
	}

	public void clearAttachments()
	{
		model.clear();
		updateTable();
	}


	File extractAttachmentToTempFile(AttachmentProxy proxy) throws IOException, InvalidBase64Exception, InvalidPacketException, SignatureVerificationException, WrongPacketTypeException, CryptoException
	{
		String fileName = proxy.getLabel();
		File temp = File.createTempFile(extractFileNameOnly(fileName), extractExtentionOnly(fileName));
		temp.deleteOnExit();

		ReadableDatabase db = mainWindow.getApp().getStore().getDatabase();
		BulletinLoader.extractAttachmentToFile(db, proxy, getSecurity(), temp);
		return temp;
	}

	public static String extractFileNameOnly(String fullName)
	{
		int index = fullName.lastIndexOf('.');
		if(index == -1)
			index = fullName.length();
		String fileNameOnly = fullName.substring(0, index);
		while(fileNameOnly.length() < 3)
		{
			fileNameOnly += "_";	
		}
		return fileNameOnly;
	}

	public static String extractExtentionOnly(String fullName)
	{
		int index = fullName.lastIndexOf('.');
		if(index == -1)
			return null;
		return fullName.substring(index, fullName.length());
	}

	static void setLastAttachmentSaveDirectory(File lastAttachmentSaveDirectory)
	{
		UiAttachmentViewer.lastAttachmentSaveDirectory =
			lastAttachmentSaveDirectory;
	}

	static File getLastAttachmentSaveDirectory()
	{
		return lastAttachmentSaveDirectory;
	}

	class HideHandler implements ActionListener
	{
		public HideHandler(ViewSingleAttachmentPanel panelToUse)
		{
			panel = panelToUse;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			panel.hideImage();
		}

		ViewSingleAttachmentPanel panel;
	}

	class ViewHandler implements ActionListener
	{
		public ViewHandler(ViewSingleAttachmentPanel panelToUse)
		{
			panel = panelToUse;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			panel.showImageInline();
			if(panel.isImageInline)
				return;
			
			if(!Utilities.isMSWindows())
			{
				mainWindow.notifyDlg("ViewAttachmentNotAvailable");
				return;
			}
			
			AttachmentProxy proxy = panel.getAttachmentProxy();
			String author = proxy.getUniversalId().getAccountId();
			if(!author.equals(mainWindow.getApp().getAccountId()))
			{
				if(!mainWindow.confirmDlg("NotYourBulletinViewAttachmentAnyways"))
					return;
			}
			mainWindow.setWaitingCursor();
			try
			{
				File temp = extractAttachmentToTempFile(proxy);

				Runtime runtimeViewer = Runtime.getRuntime();
				String tempFileFullPathName = temp.getPath();
				Process processView=runtimeViewer.exec("rundll32"+" "+"url.dll,FileProtocolHandler"+" "+tempFileFullPathName);
				processView.waitFor();
			}
			catch(Exception e)
			{
				mainWindow.notifyDlg("UnableToViewAttachment");
				System.out.println("Unable to view file :" + e);
			}
			mainWindow.resetCursor();
		}
		
		ViewSingleAttachmentPanel panel;
	}
	
	class SaveHandler implements ActionListener
	{
		public SaveHandler(AttachmentProxy proxyToUse)
		{
			proxy = proxyToUse;
		}
		
		public void actionPerformed(ActionEvent ae)
		{
			String fileName = proxy.getLabel();

			File last = getLastAttachmentSaveDirectory();
			if(last == null)
				last = UiFileChooser.getHomeDirectoryFile();
			File attachmentFileToSave = new File(last, fileName);
			UiFileChooser.FileDialogResults results = UiFileChooser.displayFileSaveDialog(mainWindow, null, attachmentFileToSave);
			if(results.wasCancelChoosen())
				return;
			setLastAttachmentSaveDirectory(results.getCurrentDirectory());
			File outputFile = results.getChosenFile();
			if(outputFile.exists())
			{
				if(!mainWindow.confirmDlg("OverWriteExistingFile"))
					return;
			}
			mainWindow.setWaitingCursor();
			try
			{
				ReadableDatabase db = mainWindow.getApp().getStore().getDatabase();
				BulletinLoader.extractAttachmentToFile(db, proxy, getSecurity(), outputFile);
			}
			catch(Exception e)
			{
				mainWindow.notifyDlg("UnableToSaveAttachment");
				System.out.println("Unable to save file :" + e);
			}
			mainWindow.resetCursor();
		}

		AttachmentProxy proxy;
	}

	class AttachmentDragHandler implements DragGestureListener, DragSourceListener
	{
		public AttachmentDragHandler(AttachmentProxy proxyToUse)
		{
			proxy = proxyToUse;
		}
		
		public void dragGestureRecognized(DragGestureEvent dge)
		{
			MartusLogger.log("Dragging: " + proxy.getLabel());
			AttachmentProxy[] attachments = new AttachmentProxy[] {proxy};
			TransferableAttachmentList dragable = new TransferableAttachmentList(mainWindow.getStore().getDatabase(), mainWindow.getApp().getSecurity(), attachments);
			dge.startDrag(DragSource.DefaultCopyDrop, dragable, this);
		}
	
		public void dragEnter(DragSourceDragEvent dsde)
		{
		}
	
		public void dragOver(DragSourceDragEvent dsde)
		{
		}
	
		public void dropActionChanged(DragSourceDragEvent dsde)
		{
		}
	
		public void dragDropEnd(DragSourceDropEvent dsde)
		{
		}
	
		public void dragExit(DragSourceEvent dse)
		{
		}
		
		AttachmentProxy proxy;
	}
	
	class ViewSingleAttachmentPanel extends JPanel
	{
		public ViewSingleAttachmentPanel(AttachmentProxy proxyToUse)
		{
			super(new BorderLayout());
			proxy = proxyToUse;

			setBorder(BorderFactory.createLineBorder(Color.BLACK));

			addHeader();

			DragSource dragSource = DragSource.getDefaultDragSource();
			dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, 
					new AttachmentDragHandler(proxy));
		}

		private void addHeader()
		{
			header = new ViewAttachmentHeader(this);
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
				InlineAttachmentComponent image = new InlineAttachmentComponent(proxy);
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
		
		AttachmentProxy proxy;
		boolean isImageInline;
		ViewAttachmentHeader header;
	}
	
	class InlineAttachmentComponent extends UiLabel
	{
		public InlineAttachmentComponent(AttachmentProxy proxy) throws Exception
		{
			File tempFile = extractAttachmentToTempFile(proxy);
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image image = toolkit.getImage(tempFile.getAbsolutePath());
			ImageIcon icon = new ImageIcon(image);
			setIcon(icon);
		}
		
		public boolean isValid()
		{
			return (getIcon().getIconHeight() > 0);
		}
	}
	
	class ViewAttachmentHeader extends JPanel
	{
		public ViewAttachmentHeader(ViewSingleAttachmentPanel panel)
		{
			GridLayoutPlus layout = new GridLayoutPlus(1, 0, 0, 0, 0, 0);
			layout.setFill(Alignment.FILL_VERTICAL);
			setLayout(layout);

			AttachmentProxy proxy = panel.getAttachmentProxy();
			addCell(new UiLabel(proxy.getLabel()), 400);
			addCell(new UiLabel(model.getSize(proxy)), 80);

			viewButton = new UiButton(getLocalization().getButtonLabel("viewattachment"));
			if(isAttachmentAvailable(proxy))
				viewButton.addActionListener(new ViewHandler(panel));
			else
				viewButton.setEnabled(false);
			hideButton = new UiButton(getLocalization().getButtonLabel("hideattachment"));
			if(isAttachmentAvailable(proxy))
				hideButton.addActionListener(new HideHandler(panel));
			else
				hideButton.setEnabled(false);
			
			viewHideLayout = new CardLayout();
			viewHidePanel = new JPanel(viewHideLayout);
			viewHidePanel.add(viewButton, viewButton.getText());
			viewHidePanel.add(hideButton, hideButton.getText());
			addCell(viewHidePanel);

			UiButton saveButton = new UiButton(getLocalization().getButtonLabel("saveattachment"));
			if(isAttachmentAvailable(proxy))
				saveButton.addActionListener(new SaveHandler(proxy));
			else
				saveButton.setEnabled(false);
			addCell(saveButton);
		}
		
		public void showViewButton()
		{
			viewHideLayout.show(viewHidePanel, viewButton.getText());
		}
		
		public void showHideButton()
		{
			viewHideLayout.show(viewHidePanel, hideButton.getText());
		}
		
		boolean isAttachmentAvailable(AttachmentProxy proxy)
		{
			UniversalId uid = proxy.getUniversalId();
			DatabaseKey key = DatabaseKey.createLegacyKey(uid);
			return mainWindow.getStore().doesBulletinRevisionExist(key);
		}
		
		JPanel addCell(JComponent contents, int preferredWidth)
		{
			JPanel cell = addCell(contents);
			cell.setPreferredSize(new Dimension(preferredWidth, 1));
			return cell;
		}
		
		JPanel addCell(JComponent contents)
		{
			Border outsideBorder = BorderFactory.createLineBorder(Color.BLACK);
			Border insideBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
			JPanel cell = new JPanel();
			cell.setBorder(BorderFactory.createCompoundBorder(outsideBorder, insideBorder));
			cell.add(contents);
			add(cell);
			return cell;
		}
		
		CardLayout viewHideLayout;
		JPanel viewHidePanel;
		UiButton viewButton;
		UiButton hideButton;
	}
	
	UiMainWindow mainWindow;
	AttachmentTableModel model;

	private static File lastAttachmentSaveDirectory;
}
