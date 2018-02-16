package actions;

import javafx.application.Platform;

import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static settings.AppPropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /** The application to which this class of actions belongs. */
    private ApplicationTemplate applicationTemplate;

    /** Path to the data file currently active. */
    Path dataFilePath;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        //this.applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION).show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));

        try{
            if(promptToSave()){
                applicationTemplate.getUIComponent().clear();
                ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
            }
        } catch (IOException e) {
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_MSG.name()));
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        Platform.exit();
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
    }

    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    private boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method

        PropertyManager manager = applicationTemplate.manager;
        this.applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION).show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        ConfirmationDialog.Option x = ConfirmationDialog.getDialog().getSelectedOption();

            if (x == ConfirmationDialog.Option.YES) {
                //            System.out.println("yes was selected");

                FileChooser fileChooser = new FileChooser();
                //          fileChooser.setTitle("Open Resource File");
                fileChooser.setInitialDirectory(new File(manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
                //          fileChooser.setInitialDirectory(new File("/Users/Kristy/IdeaProjects/cse219homework/hw1/data-vilij/resources/data"));

                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT.name()), manager.getPropertyValue(DATA_FILE_EXT_DESC.name())));

                File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                if(file != null) {
                    FileWriter writing = new FileWriter(file);
                    writing.write(((AppUI) applicationTemplate.getUIComponent()).getTextArea());
                    dataFilePath = fileChooser.getInitialDirectory().toPath();
                    writing.close();
                }else{
                    this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(SPECIFIED_FILE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
                }
                return true;
            } if (x == ConfirmationDialog.Option.NO) {
                //System.out.println("no was selected");
                return true;
            }

        return false;
    }
}
