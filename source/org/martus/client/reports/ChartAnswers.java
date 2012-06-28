/*

The Martus(tm) free, social justice documentation and
monitoring software. Copyright (C) 2012, Beneficent
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
package org.martus.client.reports;

import org.json.JSONObject;
import org.martus.common.MiniLocalization;
import org.martus.common.fieldspec.MiniFieldSpec;

public class ChartAnswers
{
	public ChartAnswers(MiniFieldSpec miniSpecOfFieldToCount, MiniLocalization localizationToUse)
	{
		version = EXPECTED_VERSION;
		fieldToCount = miniSpecOfFieldToCount;
		languageCode = localizationToUse.getCurrentLanguageCode();
	}
	
	public ChartAnswers(JSONObject json)
	{
		version = json.getInt(TAG_VERSION);
		languageCode = json.optString(TAG_LANGUAGE, MiniLocalization.LANGUAGE_OTHER);
//		type = ReportType.createFromString(json.getString(TAG_TYPE));
//		JSONArray jsonSpecs = json.getJSONArray(TAG_SPECS);
//		specs = new MiniFieldSpec[jsonSpecs.length()];
//		for(int i = 0; i < specs.length; ++i)
//			specs[i] = new MiniFieldSpec(jsonSpecs.getJSONObject(i));
		fieldToCount = new MiniFieldSpec(json.getJSONObject(TAG_SPEC_OF_FIELD_TO_COUNT));
	}
	
	public int getVersion()
	{
		return version;
	}
	
	private Object getLanguageCode()
	{
		return languageCode;
	}

	public MiniFieldSpec getFieldToCount()
	{
		return fieldToCount;
	}

	public JSONObject toJson()
	{
		JSONObject json = new JSONObject();
		json.put(TAG_JSON_TYPE, JSON_TYPE_CHART_ANSWERS);
		json.put(TAG_VERSION, EXPECTED_VERSION);
		json.put(TAG_LANGUAGE, getLanguageCode());
//		json.put(TAG_TYPE, type.toString());
//		JSONArray jsonSpecs = new JSONArray();
//		for(int i = 0; i < specs.length; ++i)
//			jsonSpecs.put(specs[i].toJson());
//		json.put(TAG_SPECS, jsonSpecs);
		json.put(TAG_SPEC_OF_FIELD_TO_COUNT, fieldToCount.toJson());
		return json;
	}
	
	public final static String TAG_JSON_TYPE = "JsonType";
	public final static String TAG_LANGUAGE = "Language";
	public final static String TAG_CHART_TYPE = "ChartType";
	public final static String TAG_VERSION = "Version";
	public final static String TAG_SPEC_OF_FIELD_TO_COUNT = "SpecOfFieldToCount";
	
	public final static String JSON_TYPE_CHART_ANSWERS = "ChartAnswers";
	private final static int EXPECTED_VERSION = 1;


	private int version;
	private String languageCode;
	private MiniFieldSpec fieldToCount;
}
