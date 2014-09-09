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
package org.martus.client.swingui;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.martus.client.core.FontSetter;
import org.martus.client.core.MartusApp;
import org.martus.client.core.MartusApp.MartusAppInitializationException;
import org.martus.clientside.CurrentUiState;
import org.martus.clientside.MtfAwareLocalization;
import org.martus.common.EnglishCommonStrings;
import org.martus.common.MiniLocalization;
import org.martus.swing.FontHandler;
import org.martus.util.DatePreference;


public class UiSession
{
	public UiSession() throws MartusAppInitializationException
	{
		MartusLocalization newLocalization = new MartusLocalization(MartusApp.getTranslationsDirectory(), UiSession.getAllEnglishStrings());
		
		setLocalization(newLocalization);
		app = new MartusApp(getLocalization());
		initializeCurrentLanguage();
	}

	public void initalizeUiState()
	{
		uiState = new CurrentUiState();
		
		if(uiState.getCurrentLanguage() == null)
		{
			getLocalization().setCurrentLanguageCode(MiniLocalization.ENGLISH);
			getLocalization().setCurrentDateFormatCode(new DatePreference().getDateTemplate());
			getLocalization().setCurrentCalendarSystem(MiniLocalization.GREGORIAN_SYSTEM);
		}

		File uiStateFile = getUiStateFile();
		if(!uiStateFile.exists())
		{
			copyLocalizationSettingsToUiState();
			getUiState().save(uiStateFile);
			return;
		}
		getUiState().load(uiStateFile);
		getLocalization().setLanguageSettingsProvider(uiState);
	}

	public File getUiStateFile()
	{
		if(getApp().isSignedIn())
			return getApp().getUiStateFileForAccount(getApp().getCurrentAccountDirectory());
		return new File(getApp().getMartusDataRootDirectory(), "UiState.dat");
	}
	
	public void saveCurrentUiState()
	{
		getUiState().save(getUiStateFile());
	}

	public void copyLocalizationSettingsToUiState()
	{
		getUiState().setCurrentLanguage(getLocalization().getCurrentLanguageCode());
		getUiState().setCurrentDateFormat(getLocalization().getCurrentDateFormatCode());
		getUiState().setCurrentCalendarSystem(getLocalization().getCurrentCalendarSystem());
	}

	private void initializeCurrentLanguage()
	{
		CurrentUiState previouslySavedState = new CurrentUiState();
		previouslySavedState.load(getUiStateFile());
		
		if(previouslySavedState.getCurrentLanguage() != null)
			MartusApp.setInitialUiDefaultsFromFileIfPresent(previouslySavedState, new File(getApp().getMartusDataRootDirectory(),"DefaultUi.txt"));
		
		if(previouslySavedState.getCurrentLanguage() == null)
		{
			previouslySavedState.setCurrentLanguage(MtfAwareLocalization.ENGLISH);
			previouslySavedState.setDateFormatFromLanguage();
		}

		if (MtfAwareLocalization.BURMESE.equals(previouslySavedState.getCurrentLanguage()))
			FontSetter.setUIFont(FontHandler.BURMESE_FONT);
		
		getLocalization().setLanguageSettingsProvider(previouslySavedState);

	}

	public static String[] getAllEnglishStrings()
	{
		String[] clientStrings = EnglishStrings.strings;
		int lenghtClient = clientStrings.length;
		String[] commonStrings = EnglishCommonStrings.strings;
		int lenghtCommon = commonStrings.length;
		String[] allEnglishStrings = new String[lenghtCommon+lenghtClient];
		System.arraycopy(clientStrings,0,allEnglishStrings,0,lenghtClient);
		System.arraycopy(commonStrings,0,allEnglishStrings,lenghtClient,lenghtCommon);
		return allEnglishStrings;
	}

	private void setLocalization(MartusLocalization martusLocalization)
	{
		localization = martusLocalization;
	}

	public MartusLocalization getLocalization()
	{
		return localization;
	}

	public MartusApp getApp()
	{
		return app;
	}

	CurrentUiState getUiState()
	{
		return uiState;
	}

	public static Map<String, File> getMemorizedFileOpenDirectories()
	{
		if(memorizedFileOpenDirectories == null)
			memorizedFileOpenDirectories = new HashMap<String, File>();
		return memorizedFileOpenDirectories;
	}

	private MartusLocalization localization;
	private MartusApp app;
	private CurrentUiState uiState;
	public static boolean isAlphaTester;
	public static boolean isJavaFx;
	
	public static boolean defaultFoldersUnsorted;
	private static Map<String, File> memorizedFileOpenDirectories;
}
