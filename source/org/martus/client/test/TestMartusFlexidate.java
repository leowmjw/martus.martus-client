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
package org.martus.client.test;

import java.util.Date;

import org.martus.common.utilities.MartusFlexidate;
import org.martus.util.MultiCalendar;
import org.martus.util.TestCaseEnhanced;

public class TestMartusFlexidate extends TestCaseEnhanced
{
	
	public TestMartusFlexidate(String name)
	{
		super(name);
	}
	
	public void testFlexiDate()
	{
		MartusFlexidate mf = new MartusFlexidate("2003-01-05", 2);	
		assertEquals("20030105+2", mf.getMartusFlexidateString());
		
		assertEquals("2003-01-05", mf.getBeginDate().toIsoDateString());
		assertEquals("2003-01-07", mf.getEndDate().toIsoDateString());																
	}
		
	public void testFlexiDateOverMonths()
	{
		MartusFlexidate mf = new MartusFlexidate("2003-01-05", 120);		
		assertEquals("20030105+120", mf.getMartusFlexidateString());

		assertEquals("2003-01-05", mf.getBeginDate().toIsoDateString());
		assertEquals("2003-05-05", mf.getEndDate().toIsoDateString());
	
	}
	
	public void testFlexiDateOverYear()
	{
		MartusFlexidate mf = new MartusFlexidate("2002-01-05", 366);		
		assertEquals("20020105+366", mf.getMartusFlexidateString());

		assertEquals("2002-01-05", mf.getBeginDate().toIsoDateString());
		assertEquals("2003-01-06", mf.getEndDate().toIsoDateString());		
	}
	
	
	public void testExactDate()
	{
		MartusFlexidate mf = new MartusFlexidate("2003-01-05", 0);
		
		assertEquals("20030105+0", mf.getMartusFlexidateString());
		
		assertEquals("2003-01-05", mf.getBeginDate().toIsoDateString());
		assertEquals("2003-01-05", mf.getEndDate().toIsoDateString());			
	}	
	
	public void testDateRange()
	{
		MultiCalendar beginDate = getDate(2000,1,10);
		MultiCalendar endDate = getDate(2000,1, 15);
						
		MartusFlexidate mf = new MartusFlexidate(beginDate, endDate);
		
		assertEquals("20000110+5", mf.getMartusFlexidateString());	
		
		assertEquals("2000-01-10", mf.getBeginDate().toIsoDateString());
		assertEquals("2000-01-15", mf.getEndDate().toIsoDateString());			
	}	
	
	public void testSameDateRange()
	{
		MultiCalendar beginDate = getDate(2000,1,10);
		MultiCalendar endDate = getDate(2000,1, 10);
		
		MartusFlexidate mf = new MartusFlexidate(beginDate, endDate);

		assertEquals("20000110+0", mf.getMartusFlexidateString());	

		assertEquals("2000-01-10", mf.getBeginDate().toIsoDateString());
		assertEquals("2000-01-10", mf.getEndDate().toIsoDateString());
		
		mf = new MartusFlexidate("2003-01-05", 0);		
		assertEquals("20030105+0", mf.getMartusFlexidateString());

		assertEquals("2003-01-05", mf.getBeginDate().toIsoDateString());
		assertEquals("2003-01-05", mf.getEndDate().toIsoDateString());			
	}
	
	public void testDateRangeSwap()
	{
		MultiCalendar beginDate = getDate(2000, 1, 10);
		MultiCalendar endDate = new MultiCalendar();
		endDate.setTime(new Date(beginDate.getTime().getTime() - (360L*24*60*60*1000)));
					
		MartusFlexidate mf = new MartusFlexidate(beginDate, endDate);
	
		assertEquals("Initial date incorrect", "19990115+360", mf.getMartusFlexidateString());	
	
		assertEquals("1999-01-15", mf.getBeginDate().toIsoDateString());
		assertEquals("2000-01-10", mf.getEndDate().toIsoDateString());
	}
	
	public void testCreateMartusDateStringFromDateRange()
	{
		assertNull(MartusFlexidate.createMartusDateStringFromBeginAndEndDateString("invalidDate"));
		String standardDateRange = "1988-02-01,1988-02-05";
		assertEquals("1988-02-01,19880201+4", MartusFlexidate.createMartusDateStringFromBeginAndEndDateString(standardDateRange));

		String reversedDateRange = "1988-02-05,1988-02-01";
		assertEquals("1988-02-01,19880201+4", MartusFlexidate.createMartusDateStringFromBeginAndEndDateString(reversedDateRange));

		String noDateRange = "1988-02-05,1988-02-05";
		assertEquals("1988-02-05,19880205+0", MartusFlexidate.createMartusDateStringFromBeginAndEndDateString(noDateRange));
	}

	private MultiCalendar getDate(int year, int month, int day)
	{			
		MultiCalendar cal = MultiCalendar.createFromGregorianYearMonthDay(year, month, day);
		return cal;
	} 
}
