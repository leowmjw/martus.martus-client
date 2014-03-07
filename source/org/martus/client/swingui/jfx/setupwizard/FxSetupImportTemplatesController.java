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
package org.martus.client.swingui.jfx.setupwizard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Vector;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.util.StringConverter;

import org.martus.client.swingui.UiMainWindow;
import org.martus.client.swingui.jfx.FxController;
import org.martus.common.MartusLogger;
import org.martus.common.fieldspec.CustomFieldTemplate;
import org.martus.common.fieldspec.CustomFieldTemplate.FutureVersionException;
import org.martus.util.TokenReplacement;
import org.martus.util.inputstreamwithseek.ByteArrayInputStreamWithSeek;
import org.martus.util.inputstreamwithseek.InputStreamWithSeek;

public class FxSetupImportTemplatesController extends AbstractFxSetupWizardContentController implements Initializable
{
	public FxSetupImportTemplatesController(UiMainWindow mainWindowToUse)
	{
		super(mainWindowToUse);
	}

	@Override
	public String getFxmlLocation()
	{
		return "setupwizard/SetupImportTemplate.fxml";
	}

	@Override
	public FxController getNextControllerClassName()
	{
		return new FxSetupBackupYourKeyController(getMainWindow());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		genericTemplatesComboBox.setConverter(new FormTemplateToStringConverter());
		genericTemplatesComboBox.setItems(FXCollections.observableArrayList(getDefaultFormTemplateChoices()));

		customTemplatesComboBox.setItems(FXCollections.observableArrayList(getImportTemplateChoices()));
		customTemplatesComboBox.setConverter(new ControllerToStringConverter());
		
		genericTemplatesComboBox.setVisible(false);
		customTemplatesComboBox.setVisible(false);
		
		selectedTemplateLabel.setVisible(false);
		switchFormsLaterLabel.setVisible(false);
		
		getWizardNavigationHandler().getNextButton().addEventHandler(ActionEvent.ACTION, new NextButtonHandler());
	} 
	
	private ObservableList<AbstractFxImportFormTemplateController> getImportTemplateChoices()
	{
		try
		{
			Vector<AbstractFxImportFormTemplateController> choices = new Vector<AbstractFxImportFormTemplateController>();
			choices.add(new FxSetupFormTemplateFromNewContactPopupController(getMainWindow()));
			choices.add(new FxImportFormTemplateFromMyContactsPopupController(getMainWindow()));

			return FXCollections.observableArrayList(choices);
		}
		catch (Exception e)
		{
			MartusLogger.logException(e);
			return FXCollections.observableArrayList();
		}
	}

	private ObservableList<CustomFieldTemplate> getDefaultFormTemplateChoices()
	{
		try
		{
			Vector<CustomFieldTemplate> customTemplates = loadFormTemplates();

			return FXCollections.observableArrayList(customTemplates);
		}
		catch (Exception e)
		{
			MartusLogger.logException(e);
			return FXCollections.observableArrayList();
		}
	}
	
	private Vector<CustomFieldTemplate> loadFormTemplates() throws Exception
	{
		String[] formTemplateFileNames = new String[]{"formtemplates/sampleTemplate.mct", };
		Vector<CustomFieldTemplate> formTemplates = new Vector<CustomFieldTemplate>();
		for (String formTemplateFileName : formTemplateFileNames)
		{
			InputStream resourceAsStream = getClass().getResourceAsStream(formTemplateFileName);
			CustomFieldTemplate formTemplate = importFormTemplate(resourceAsStream);
			formTemplates.add(formTemplate);
		}
		
		return formTemplates;
	}

	private CustomFieldTemplate importFormTemplate(InputStream resourceAsStream) throws Exception, FutureVersionException, IOException
	{
		InputStreamWithSeek withSeek = new ByteArrayInputStreamWithSeek(convertToInputStreamWithSeek(resourceAsStream));
		try
		{
			CustomFieldTemplate formTemplate = new CustomFieldTemplate();
			formTemplate.importTemplate(getApp().getSecurity(), withSeek);

			return formTemplate;
		}
		finally
		{
			withSeek.close();
		}
	}

	private byte[] convertToInputStreamWithSeek(InputStream resourceAsStream) throws Exception
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try
		{
			int readBytes = -1;
			while ((readBytes = resourceAsStream.read()) != -1)
			{
				outputStream.write(readBytes);
			}

			return outputStream.toByteArray();
		}
		finally
		{
			outputStream.close();
		}
	}
	
	@FXML
	private void genericComboBoxSelectionChanged() throws Exception
	{
		CustomFieldTemplate genericCustomFieldTemplate = genericTemplatesComboBox.getSelectionModel().getSelectedItem();
		if (genericCustomFieldTemplate == null)
			return;
		
		updateSelectedCustomFieldTemplateComponents(genericCustomFieldTemplate);
	}

	@FXML
	private void customDropDownSelectionChanged() throws Exception
	{
		AbstractFxImportFormTemplateController selectedController = customTemplatesComboBox.getSelectionModel().getSelectedItem();
		importFromContacts(selectedController);
	}
	
	@FXML
	private void radioButtonSelectionChanged()
	{
		genericTemplatesComboBox.setVisible(false);
		customTemplatesComboBox.setVisible(false);
		if (genericRadioButton.isSelected())
			genericTemplatesComboBox.setVisible(true);
		
		if (downloadCustomRadioButton.isSelected())
			customTemplatesComboBox.setVisible(true);
	}
	
	private void importFromContacts(AbstractFxImportFormTemplateController controller) throws Exception
	{
		showControllerInsideModalDialog(controller);
		CustomFieldTemplate selectedTemplate = controller.getSelectedFormTemplate();
		updateSelectedCustomFieldTemplateComponents(selectedTemplate);
	}
	
	private void updateSelectedCustomFieldTemplateComponents(CustomFieldTemplate customFieldTemplate) throws Exception
	{
		boolean shouldAllowFormTemplate = false;
		String loadFormTemplateMessage = "";
		if (customFieldTemplate != null)
		{
			loadFormTemplateMessage = TokenReplacement.replaceToken(">Import the #templateName Form", "#templateName", customFieldTemplate.getTitle());
			shouldAllowFormTemplate = true;
		}
		
		selectedTemplateLabel.setText(loadFormTemplateMessage);
		selectedTemplateLabel.setVisible(shouldAllowFormTemplate);
		switchFormsLaterLabel.setVisible(shouldAllowFormTemplate);
	}
	
	private void saveCustomFieldTemplate(CustomFieldTemplate customFieldTemplate)
	{
		try
		{
			getApp().updateCustomFieldTemplate(customFieldTemplate);
		}
		catch (Exception e)
		{
			MartusLogger.logException(e);
		}
	}
	
	private class NextButtonHandler implements EventHandler<ActionEvent>
	{
		public NextButtonHandler()
		{
		}

		@Override
		public void handle(ActionEvent event)
		{
			if (genericRadioButton.isSelected())
			{
				CustomFieldTemplate genericCustomFieldTemplate = genericTemplatesComboBox.getSelectionModel().getSelectedItem();
				saveCustomFieldTemplate(genericCustomFieldTemplate);
			}
			if (downloadCustomRadioButton.isSelected())
			{
				AbstractFxImportFormTemplateController controller = customTemplatesComboBox.getSelectionModel().getSelectedItem();
				CustomFieldTemplate formTemplateFromContact = controller.getSelectedFormTemplate();
				saveCustomFieldTemplate(formTemplateFromContact);
			}
		}
	}
	
	private class ControllerToStringConverter extends StringConverter<AbstractFxImportFormTemplateController>
	{
		@Override
		public String toString(AbstractFxImportFormTemplateController object)
		{
			return object.getLabel();
		}

		@Override
		public AbstractFxImportFormTemplateController fromString(String string)
		{
			return null;
		}
	}
	
	@FXML
	private ComboBox<CustomFieldTemplate> genericTemplatesComboBox;
	
	@FXML
	private ComboBox<AbstractFxImportFormTemplateController> customTemplatesComboBox;
	
	@FXML
	private RadioButton genericRadioButton;
	
	@FXML
	private RadioButton downloadCustomRadioButton;
	
	@FXML
	private Label switchFormsLaterLabel;
	
	@FXML
	private Label selectedTemplateLabel;
}
