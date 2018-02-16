package dataprocessors;

import ui.AppUI;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.nio.file.Path;

import static settings.AppPropertyTypes.DISPLAY_ERROR_MSG;
import static settings.AppPropertyTypes.DISPLAY_ERROR_TITLE;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;

    public AppData(ApplicationTemplate applicationTemplate) {
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
    }

    public void loadData(String dataString) {
        // TODO for homework 1
        try {
            processor.processString(dataString);
        } catch (Exception e) {
            //invalid input pop-up dialog box
            PropertyManager manager = applicationTemplate.manager;
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(DISPLAY_ERROR_TITLE.name()), manager.getPropertyValue(DISPLAY_ERROR_MSG.name()));
        }

        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        this.displayData();
    }

    @Override
    public void saveData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void clear() {
        processor.clear();
    }

    public void displayData() {
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
    }
}
