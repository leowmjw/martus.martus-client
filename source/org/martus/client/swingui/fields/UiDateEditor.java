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

package org.martus.client.swingui.fields;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.martus.common.FieldSpec;
import org.martus.common.StandardFieldSpecs;
import org.martus.common.bulletin.Bulletin;
import org.martus.common.clientside.DateUtilities;
import org.martus.common.clientside.UiBasicLocalization;
import org.martus.swing.UiComboBox;
import org.martus.swing.Utilities;

public class UiDateEditor extends UiField
{
	public UiDateEditor(UiBasicLocalization localizationToUse, FieldSpec specToUse)
	{				
		component = new JPanel();
		Box box = Box.createHorizontalBox();
		dayCombo = new UiComboBox();
		monthCombo = new UiComboBox(localizationToUse.getMonthLabels());
		yearCombo = new UiComboBox();
		
		buildCustomDate(box, localizationToUse, yearCombo, monthCombo, dayCombo);
		spec = specToUse;
 				
		component.add(box);
	}
	
	public static void buildCustomDate(Box box, UiBasicLocalization localizationToUse,
			UiComboBox yCombo, UiComboBox mCombo, UiComboBox dCombo)
	{							
		buildDay(dCombo);
		buildCustomYear(yCombo);
		buildMonth(box,localizationToUse, yCombo, mCombo,dCombo);
	}
	
	public static void buildDate(Box box, UiBasicLocalization localizationToUse,
			UiComboBox yCombo, UiComboBox mCombo, UiComboBox dCombo)
	{							
		buildDay(dCombo);
		buildYear(yCombo);
		buildMonth(box,localizationToUse, yCombo, mCombo,dCombo);
	}
		
	private static void buildCustomYear(UiComboBox yCombo)	
	{
		Calendar cal = new GregorianCalendar();
		int thisYear = cal.get(Calendar.YEAR);			
		
		for(int year = 1900; year <= thisYear+10;++year)
			yCombo.addItem(new Integer(year).toString());
		
		yCombo.setSelectedItem(new Integer(thisYear).toString());	
	}		
	
	private static void buildYear(UiComboBox yCombo)	
	{
		Calendar cal = new GregorianCalendar();
		int thisYear = cal.get(Calendar.YEAR);			
		
		for(int year = 1900; year <= thisYear; ++year)
			yCombo.addItem(new Integer(year).toString());			
	}		
	
	private static void buildDay(UiComboBox dCombo)
	{
		for(int day=1; day <= 31; ++day)
			dCombo.addItem(new Integer(day).toString());	
	}
	
	private static void buildMonth(Box box, UiBasicLocalization localizationToUse,UiComboBox yCombo, UiComboBox mCombo, UiComboBox dCombo)
	{
		String mdyOrder = DateUtilities.getMdyOrder(localizationToUse.getCurrentDateFormatCode());
		JComponent[] dateInOrderLeftToRight = new JComponent[3];
		
		for(int i = 0; i < mdyOrder.length(); ++i)
		{
			switch(mdyOrder.charAt(i))
			{
				case 'd': dateInOrderLeftToRight[i]=dCombo;	break;
				case 'm': dateInOrderLeftToRight[i]=mCombo;	break;
				case 'y': dateInOrderLeftToRight[i]=yCombo;	break;
			}
		}	
		Utilities.addComponentsRespectingOrientation(box, dateInOrderLeftToRight);
	}

	public JComponent getComponent()
	{
		return component;
	}

	public JComponent[] getFocusableComponents()
	{
		return new JComponent[]{dayCombo, monthCombo, yearCombo};
	}

	public static class DateFutureException extends UiField.DataInvalidException
	{
		public DateFutureException()
		{
			super();
		}
		public DateFutureException(String tag)
		{
			super(tag);
		}
	}
	
	public void validate() throws UiField.DataInvalidException 
	{
		if(StandardFieldSpecs.isCustomFieldTag(spec.getTag()))
			return;
	
		Date value = getDate(yearCombo, monthCombo, dayCombo);
		Date today = new Date();
		if (value.after(today))
		{
			dayCombo.requestFocus();	
			throw new DateFutureException();
		}
	}

	public String getText()
	{
		Date date = getDate(yearCombo, monthCombo, dayCombo);
		DateFormat df = Bulletin.getStoredDateFormat();
		return df.format(date);
	}

	public static Date getDate(UiComboBox yCombo, UiComboBox mCombo, UiComboBox dCombo) 
	{
		Calendar cal = new GregorianCalendar();
		cal.set(yCombo.getSelectedIndex()+1900,
				mCombo.getSelectedIndex(),
				dCombo.getSelectedIndex()+1);
		
		return cal.getTime();
	}

	public void setText(String newText)
	{
		setDate(newText, yearCombo, monthCombo, dayCombo);			
	}
	
	public static void setDate(String dateText, UiComboBox yCombo, UiComboBox mCombo, UiComboBox dCombo)
	{
		DateFormat df = Bulletin.getStoredDateFormat();
		Date d = null;
		try
		{
			d = df.parse(dateText);
			Calendar cal = new GregorianCalendar();
			cal.setTime(d);
		
		yCombo.setSelectedItem( (new Integer(cal.get(Calendar.YEAR))).toString());
		mCombo.setSelectedIndex(cal.get(Calendar.MONTH));
		dCombo.setSelectedItem( (new Integer(cal.get(Calendar.DATE))).toString());

		}
		catch(ParseException e)
		{
			System.out.println(e);
		}
	}	

	JComponent component;
	UiComboBox monthCombo;
	UiComboBox dayCombo;
	UiComboBox yearCombo;	
	static FieldSpec spec;
	boolean isCustomField;
}

