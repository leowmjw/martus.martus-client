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

package org.martus.client.swingui.bulletincomponent;

import java.io.IOException;

import javax.swing.event.ChangeEvent;

import org.martus.client.core.EncryptionChangeListener;
import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.fields.UiField;
import org.martus.common.bulletin.AttachmentProxy;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.crypto.MartusCrypto;

public class UiBulletinEditor extends UiBulletinComponent
{
	public UiBulletinEditor(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
		// ensure that attachmentEditor gets initialized
	}

	public UiBulletinComponentSection createBulletinComponentSection()
	{
		return new UiBulletinComponentEditorSection(mainWindow);
	}

	public void validateData() throws UiField.DataInvalidException 
	{
		publicStuff.validateData();
		privateStuff.validateData();
	}
	
	public boolean isBulletinModified() throws
			IOException,
			MartusCrypto.EncryptionException
	{		
		
		Bulletin tempBulletin = new Bulletin(mainWindow.getApp().getSecurity());					
		copyDataToBulletin(tempBulletin);
		
		if(tempBulletin.isAllPrivate() != currentBulletin.isAllPrivate())
			return true;
		
		if(publicStuff.isAnyFieldModified(currentBulletin, tempBulletin))
			return true;
			
		if(privateStuff.isAnyFieldModified(currentBulletin, tempBulletin))
			return true;

		if (isPublicAttachmentModified())	
			return true;						
		
		if (isPrivateAttachmentModified())
			return true;
			
		return false;			
	}	
	
	
	private boolean isPublicAttachmentModified()
	{
		UiBulletinComponentEditorSection section = (UiBulletinComponentEditorSection)publicStuff;
		AttachmentProxy[] publicAttachments = section.attachmentEditor.getAttachments();
		AttachmentProxy[] currentAttachments = currentBulletin.getPublicAttachments();
		
		if (isAnyAttachmentModified(currentAttachments, publicAttachments))
			return true;
		return false;
	}

	private boolean isPrivateAttachmentModified()
	{
		UiBulletinComponentEditorSection section = (UiBulletinComponentEditorSection)privateStuff;
		AttachmentProxy[] currentAttachments = currentBulletin.getPrivateAttachments();
		AttachmentProxy[] privateAttachments = section.attachmentEditor.getAttachments();	
			
		if (isAnyAttachmentModified(currentAttachments, privateAttachments))
			return true;
		
		return false;
	}

	private boolean isAnyAttachmentModified(AttachmentProxy[] oldProxies, AttachmentProxy[] newProxies)
	{					
		if (oldProxies.length != newProxies.length)						
			return true;
		
		for(int aIndex = 0; aIndex < oldProxies.length; ++aIndex)
		{									
			String newLocalId = newProxies[aIndex].getUniversalId().getLocalId();
			String oldLocalId = oldProxies[aIndex].getUniversalId().getLocalId();			
						
			if (!newLocalId.equals(oldLocalId))
				return true;														
		}		
		return false;	
	}		

	public void copyDataToBulletin(Bulletin bulletin) throws
		IOException,
		MartusCrypto.EncryptionException
	{				
		bulletin.clear();
			
		boolean isAllPrivate = false;
		if(allPrivateField.getText().equals(UiField.TRUESTRING))
			isAllPrivate = true;
		bulletin.setAllPrivate(isAllPrivate);
		
		publicStuff.copyDataToBulletin(bulletin);
		privateStuff.copyDataToBulletin(bulletin);

		UiBulletinComponentEditorSection publicSection = (UiBulletinComponentEditorSection)publicStuff;
		AttachmentProxy[] publicAttachments = publicSection.attachmentEditor.getAttachments();
		for(int aIndex = 0; aIndex < publicAttachments.length; ++aIndex)
		{
			AttachmentProxy a = publicAttachments[aIndex];
			bulletin.addPublicAttachment(a);
		}

		UiBulletinComponentEditorSection privateSection = (UiBulletinComponentEditorSection)privateStuff;
		AttachmentProxy[] privateAttachments = privateSection.attachmentEditor.getAttachments();
		for(int aIndex = 0; aIndex < privateAttachments.length; ++aIndex)
		{
			AttachmentProxy a = privateAttachments[aIndex];
			bulletin.addPrivateAttachment(a);
		}

	}	

	public void setEncryptionChangeListener(EncryptionChangeListener listener)
	{
		encryptionListener = listener;
	}

	protected void fireEncryptionChange(boolean newState)
	{
		if(encryptionListener != null)
			encryptionListener.encryptionChanged(newState);
	}

	// ChangeListener interface
	public void stateChanged(ChangeEvent event)
	{
		String flagString = allPrivateField.getText();
		boolean nowEncrypted = (flagString.equals(UiField.TRUESTRING));
		if(wasEncrypted != nowEncrypted)
		{
			wasEncrypted = nowEncrypted;
			fireEncryptionChange(nowEncrypted);
		}
	}

	boolean wasEncrypted;
	EncryptionChangeListener encryptionListener;
}
