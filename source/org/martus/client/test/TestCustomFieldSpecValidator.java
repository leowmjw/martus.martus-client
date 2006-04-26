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

package org.martus.client.test;

import java.util.Vector;

import org.martus.common.FieldCollection;
import org.martus.common.LegacyCustomFields;
import org.martus.common.bulletin.BulletinConstants;
import org.martus.common.fieldspec.ChoiceItem;
import org.martus.common.fieldspec.CustomFieldError;
import org.martus.common.fieldspec.CustomFieldSpecValidator;
import org.martus.common.fieldspec.DropDownFieldSpec;
import org.martus.common.fieldspec.FieldSpec;
import org.martus.common.fieldspec.FieldTypeNormal;
import org.martus.common.fieldspec.GridFieldSpec;
import org.martus.common.fieldspec.StandardFieldSpecs;
import org.martus.util.TestCaseEnhanced;

public class TestCustomFieldSpecValidator extends TestCaseEnhanced
{
	public TestCustomFieldSpecValidator(String name)
	{
		super(name);
	}
	
	public void testAllValid() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		String tag = "_A.-_AllValid0123456789";
		String label = "my Label";
		specs = addFieldSpec(specs, LegacyCustomFields.createFromLegacy(tag+","+label));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertTrue("not valid?", checker.isValid());
	}

	public void testNull() throws Exception
	{
		FieldSpec[] specs = null;
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		CustomFieldError error = (CustomFieldError)checker.getAllErrors().get(0);
		assertEquals("Incorrect Error code?", CustomFieldError.CODE_NULL_SPECS, error.getCode());
	}
	
	public void testIllegalTagCharacters() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		String label = "anything";
		String[] variousIllegalTags = {"a tag", "a&amp;b", "a=b", "a'b", ".a"};
		for(int i=0; i < variousIllegalTags.length; ++i)
		{
			String thisTag = variousIllegalTags[i];
			FieldSpec thisSpec = FieldSpec.createCustomField(thisTag, label, new FieldTypeNormal());
			specs = addFieldSpec(specs, thisSpec);
		}
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("didn't catch all errors?", variousIllegalTags.length, errors.size());
		for(int i=0; i < errors.size(); ++i)
		{
			CustomFieldError error = (CustomFieldError)errors.get(i);
			assertEquals("wrong code?", CustomFieldError.CODE_ILLEGAL_TAG, error.getCode());
			assertEquals("wrong tag?", variousIllegalTags[i], error.getTag());
			assertEquals("wrong label?", label, error.getLabel());
		}
	}
	
	public void testMissingRequiredFields() throws Exception
	{
		FieldSpec[] specs = {};
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		int numberOfRequiredFields = 4;
		assertEquals("Should require 4 fields", numberOfRequiredFields, errors.size());
		assertEquals("Incorrect Error code required 1?", CustomFieldError.CODE_REQUIRED_FIELD, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect Error code required 2?", CustomFieldError.CODE_REQUIRED_FIELD, ((CustomFieldError)errors.get(1)).getCode());
		assertEquals("Incorrect Error code required 3?", CustomFieldError.CODE_REQUIRED_FIELD, ((CustomFieldError)errors.get(2)).getCode());
		assertEquals("Incorrect Error code required 4?", CustomFieldError.CODE_REQUIRED_FIELD, ((CustomFieldError)errors.get(3)).getCode());
		Vector errorFields = new Vector();
		for (int i = 0; i<numberOfRequiredFields; ++i)
		{
			errorFields.add(((CustomFieldError)errors.get(i)).getTag());
		}
		assertContains(BulletinConstants.TAGAUTHOR, errorFields);
		assertContains(BulletinConstants.TAGLANGUAGE, errorFields);
		assertContains(BulletinConstants.TAGENTRYDATE, errorFields);
		assertContains(BulletinConstants.TAGTITLE, errorFields);
	}

	public void testMissingTag() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		String label = "my Label";
		specs = addFieldSpec(specs, LegacyCustomFields.createFromLegacy(","+label));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code Missing Tags", CustomFieldError.CODE_MISSING_TAG, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect label for Missing Tags", label, ((CustomFieldError)errors.get(0)).getLabel());
		assertEquals("Incorrect type for Missing Tags", FieldSpec.getTypeString(new FieldTypeNormal()), ((CustomFieldError)errors.get(0)).getType());
	}

	public void testDuplicateTags() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		String tag = "a";
		String label ="b";
		specs = addFieldSpec(specs, LegacyCustomFields.createFromLegacy(tag+","+label));
		specs = addFieldSpec(specs, LegacyCustomFields.createFromLegacy(tag+","+label));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code Duplicate Tags", CustomFieldError.CODE_DUPLICATE_FIELD, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag Duplicate Tags", tag, ((CustomFieldError)errors.get(0)).getTag());
		assertEquals("Incorrect label Duplicate Tags", label, ((CustomFieldError)errors.get(0)).getLabel());
	}

	public void testDuplicateDropDownEntry() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		String tag = "dd";
		String label ="cc";

		ChoiceItem[] choicesNoDups = {new ChoiceItem("no Dup", "first item"), new ChoiceItem("second", "second item")};
		DropDownFieldSpec dropDownSpecNoDuplicates = new DropDownFieldSpec(choicesNoDups);
		dropDownSpecNoDuplicates.setTag(tag);
		dropDownSpecNoDuplicates.setLabel(label);
		specs = addFieldSpec(specs, dropDownSpecNoDuplicates);
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertTrue("invalid?", checker.isValid());
		
		specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		ChoiceItem[] choicesWithDuplicate = {new ChoiceItem("duplicate", "duplicate"), new ChoiceItem("duplicate", "duplicate")};
		DropDownFieldSpec dropDownSpecWithDuplicates = new DropDownFieldSpec(choicesWithDuplicate);
		dropDownSpecWithDuplicates.setTag(tag);
		dropDownSpecWithDuplicates.setLabel(label);
		specs = addFieldSpec(specs, dropDownSpecWithDuplicates);
		checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code Duplicate Dropdown Entry", CustomFieldError.CODE_DUPLICATE_DROPDOWN_ENTRY, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag Duplicate Tags", tag, ((CustomFieldError)errors.get(0)).getTag());
		assertEquals("Incorrect label Duplicate Tags", label, ((CustomFieldError)errors.get(0)).getLabel());
	}

	public void testDuplicateDropDownEntryInSideOfAGrid() throws Exception
	{
		String tag = "dd";
		String label ="cc";
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();

		ChoiceItem[] choicesNoDups = {new ChoiceItem("no Dup", "first item"), new ChoiceItem("second", "second item")};
		DropDownFieldSpec dropDownSpecNoDuplicates = new DropDownFieldSpec(choicesNoDups);
		GridFieldSpec gridWithNoDuplicateDropdownEntries = new GridFieldSpec();
		gridWithNoDuplicateDropdownEntries.setTag(tag);
		gridWithNoDuplicateDropdownEntries.setLabel(label);
		gridWithNoDuplicateDropdownEntries.addColumn(dropDownSpecNoDuplicates);
		
		specs = addFieldSpec(specs, gridWithNoDuplicateDropdownEntries);
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertTrue("invalid?", checker.isValid());
		
		specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		ChoiceItem[] choicesWithDuplicate = {new ChoiceItem("duplicate", "duplicate"), new ChoiceItem("duplicate", "duplicate")};
		DropDownFieldSpec dropDownSpecWithDuplicates = new DropDownFieldSpec(choicesWithDuplicate);
		GridFieldSpec gridWithDuplicateDropdownEntries = new GridFieldSpec();
		gridWithDuplicateDropdownEntries.setTag(tag);
		gridWithDuplicateDropdownEntries.setLabel(label);
		gridWithDuplicateDropdownEntries.addColumn(dropDownSpecWithDuplicates);
		specs = addFieldSpec(specs, gridWithDuplicateDropdownEntries);
		
		checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code Duplicate Dropdown Entry", CustomFieldError.CODE_DUPLICATE_DROPDOWN_ENTRY, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag Duplicate Tags", tag, ((CustomFieldError)errors.get(0)).getTag());
		assertEquals("Incorrect label Duplicate Tags", label, ((CustomFieldError)errors.get(0)).getLabel());
	}
	
	public void testNoDropDownEntries() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		String tag = "dd";
		String label ="cc";

		DropDownFieldSpec dropDownSpecNoEntries = new DropDownFieldSpec();
		dropDownSpecNoEntries.setTag(tag);
		dropDownSpecNoEntries.setLabel(label);
		specs = addFieldSpec(specs, dropDownSpecNoEntries);
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code No Dropdown Entries", CustomFieldError.CODE_NO_DROPDOWN_ENTRIES, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag Duplicate Tags", tag, ((CustomFieldError)errors.get(0)).getTag());
		assertEquals("Incorrect label Duplicate Tags", label, ((CustomFieldError)errors.get(0)).getLabel());
	}

	public void testNoDropDownEntriesInsideOfAGrid() throws Exception
	{
		String tag = "dd";
		String label ="cc";
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();

		DropDownFieldSpec dropDownSpecNoEntries = new DropDownFieldSpec();
		GridFieldSpec gridWithNoDropdownEntries = new GridFieldSpec();
		gridWithNoDropdownEntries.setTag(tag);
		gridWithNoDropdownEntries.setLabel(label);
		gridWithNoDropdownEntries.addColumn(dropDownSpecNoEntries);
		
		specs = addFieldSpec(specs, gridWithNoDropdownEntries);
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());
		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code No Dropdown Entries", CustomFieldError.CODE_NO_DROPDOWN_ENTRIES, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag Duplicate Tags", tag, ((CustomFieldError)errors.get(0)).getTag());
		assertEquals("Incorrect label Duplicate Tags", label, ((CustomFieldError)errors.get(0)).getLabel());
	}
	

	public void testMissingCustomLabel() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		specs = addFieldSpec(specs, LegacyCustomFields.createFromLegacy("a,label"));
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertTrue("not valid?", checker.isValid());
		String tag = "b";
		specs = addFieldSpec(specs, LegacyCustomFields.createFromLegacy(tag));
		CustomFieldSpecValidator checker2 = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker2.isValid());
		Vector errors = checker2.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code Duplicate Tags", CustomFieldError.CODE_MISSING_LABEL, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag Duplicate Tags", tag, ((CustomFieldError)errors.get(0)).getTag());
	}
	
	public void testUnknownType() throws Exception
	{
		String tag = "weirdTag";
		String label = "weird Label";
		String xmlFieldUnknownType = "<CustomFields><Field><Tag>"+tag+"</Tag>" +
			"<Label>" + label + "</Label><Type>xxx</Type>" +
			"</Field></CustomFields>";
		FieldSpec badSpec = FieldCollection.parseXml(xmlFieldUnknownType)[0]; 

		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		specs = addFieldSpec(specs, badSpec);
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("didn't detect unknown?", checker.isValid());

		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code Unknown Type", CustomFieldError.CODE_UNKNOWN_TYPE, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag Unknown Type", tag, ((CustomFieldError)errors.get(0)).getTag());
		assertEquals("Incorrect label Unknown Type", label, ((CustomFieldError)errors.get(0)).getLabel());
	}

	public void testStandardFieldWithLabel() throws Exception
	{
		FieldSpec[] specs = StandardFieldSpecs.getDefaultPublicFieldSpecs();
		String tag = specs[3].getTag();
		String illegal_label = "Some Label";
		specs[3] = LegacyCustomFields.createFromLegacy(tag + ","+ illegal_label);
		CustomFieldSpecValidator checker = new CustomFieldSpecValidator(specs);
		assertFalse("valid?", checker.isValid());

		Vector errors = checker.getAllErrors();
		assertEquals("Should have 1 error", 1, errors.size());
		assertEquals("Incorrect Error code StandardField with Label", CustomFieldError.CODE_LABEL_STANDARD_FIELD, ((CustomFieldError)errors.get(0)).getCode());
		assertEquals("Incorrect tag StandardField with Label", tag, ((CustomFieldError)errors.get(0)).getTag());
		assertEquals("Incorrect label StandardField with Label", illegal_label, ((CustomFieldError)errors.get(0)).getLabel());
	}

	public void testParseXmlError() throws Exception
	{
		CustomFieldError xmlError = CustomFieldError.errorParseXml();
		assertEquals("Incorrect Error code for parse XML error", CustomFieldError.CODE_PARSE_XML, xmlError.getCode());
	}

	public void testIOError() throws Exception
	{
		CustomFieldError xmlError = CustomFieldError.errorIO();
		assertEquals("Incorrect Error code for IO error", CustomFieldError.CODE_IO_ERROR, xmlError.getCode());
	}

	public void testSignatureError() throws Exception
	{
		CustomFieldError xmlError = CustomFieldError.errorSignature();
		assertEquals("Incorrect Error code for signature error", CustomFieldError.CODE_SIGNATURE_ERROR, xmlError.getCode());
	}

	public void testUnauthorizedKeyError() throws Exception
	{
		CustomFieldError xmlError = CustomFieldError.errorUnauthorizedKey();
		assertEquals("Incorrect Error code for parse XML error", CustomFieldError.CODE_UNAUTHORIZED_KEY, xmlError.getCode());
	}

	static public FieldSpec[] addFieldSpec(FieldSpec[] existingFieldSpecs, FieldSpec newFieldSpec)
	{
		int oldCount = existingFieldSpecs.length;
		FieldSpec[] tempFieldTags = new FieldSpec[oldCount + 1];
		System.arraycopy(existingFieldSpecs, 0, tempFieldTags, 0, oldCount);
		tempFieldTags[oldCount] = newFieldSpec;
		return tempFieldTags;
	}

}
