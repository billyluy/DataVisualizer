package actions;

import com.sun.tools.javah.Util;
import components.ExitDialog;
import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import javax.imageio.ImageIO;
import java.io.*;
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
    Path dataFilePath = null;
    Boolean check = false;

    ConfirmationDialog.Option selectedOption;


    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
    }

    @Override
    public void handleNewRequest() {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
//
//        try{
//            if(promptToSave()){
//                System.out.println("hi");
//                while(!((AppUI) applicationTemplate.getUIComponent()).getTextArea().isEmpty()) {
//                applicationTemplate.getUIComponent().clear();
//                }
//                ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
//                dataFilePath = null;
//
//                ((AppUI) applicationTemplate.getUIComponent()).clearInfoandButton();
//            }
//        } catch (IOException e) {
//            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_MSG.name()));
//        }
//

        dataFilePath = null;
        applicationTemplate.getUIComponent().clear();
        ((AppUI) applicationTemplate.getUIComponent()).setTextArea("");
        ((AppData) applicationTemplate.getDataComponent()).clearlinesleft();
        ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
        ((AppUI) applicationTemplate.getUIComponent()).clearInfoandButton();
        ((AppUI) applicationTemplate.getUIComponent()).setToggleButton();
        ((AppUI) applicationTemplate.getUIComponent()).clearSelectingAlgorithmsBox();

    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
        String input1 = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
        TSDProcessor processor1 = new TSDProcessor();

        String input2 = "";

        try {
            String a = "";
            for (int i = 0; i < ((AppData) applicationTemplate.getDataComponent()).getLinesLeft().size(); i++) {
                a += ((AppData) applicationTemplate.getDataComponent()).getLinesLeft().get(i);
            }

            input2 = input1 + a;

            processor1.processString(input1 + a);
            processor1.checkForDuplicates(input1 + a);

            if(processor1.getDuplicates() || processor1.dup()){
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(UNABLE_TO_SAVE_DUPLICATE_TITLE.name()), manager.getPropertyValue(LINE_DUPLICATE.name()) + processor1.getNameOfDuplicate().get(0));
            }

            if(dataFilePath == null && !processor1.getDuplicates()){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
                fileChooser.getInitialDirectory();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT.name()), manager.getPropertyValue(DATA_FILE_EXT_DESC.name())));

                File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                dataFilePath = file.toPath();
                applicationTemplate.getDataComponent().saveData(dataFilePath);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            }
            else if(!processor1.getDuplicates()){
                applicationTemplate.getDataComponent().saveData(dataFilePath);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            }
        } catch (Exception e) {

            processor1.checkForDuplicates(input2);
            if(processor1.getDuplicates()){
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(UNABLE_TO_SAVE_DUPLICATE_TITLE.name()), manager.getPropertyValue(LINE_DUPLICATE.name()) + processor1.getNameOfDuplicate().get(0));
            }
            else if(processor1.getlineOfError() > 0){
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(SAVE_ERROR_TITLE.name()), manager.getPropertyValue(SAVE_ERROR_MSG.name()) + processor1.getlineOfError());
            }else{
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(SAVE_REGULAR_MSG.name()), manager.getPropertyValue(SAVE_REGULAR_MSG.name()));

            }
        }
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1

        PropertyManager manager = applicationTemplate.manager;
        FileChooser chooser1 = new FileChooser();
        chooser1.setTitle(applicationTemplate.manager.getPropertyValue(OPEN_FILE_TITLE.name()));
        chooser1.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT.name()), manager.getPropertyValue(DATA_FILE_EXT_DESC.name())));

        dataFilePath = null;
        File file = chooser1.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

        try {
            if(file!= null) {
                dataFilePath = file.toPath();
                applicationTemplate.getDataComponent().loadData(dataFilePath);
                check = true;
            }

//            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
//            ((AppUI) applicationTemplate.getUIComponent()).textAreaVisibility();

        }catch (NullPointerException e){
            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(LOAD_ERROR_TITLE.name()), manager.getPropertyValue(LOAD_ERROR_MSG.name()));
        }


    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        if(((AppUI) applicationTemplate.getUIComponent()).getThreadRunning()){
            ExitDialog.getDialog().show("Algorithm Running", manager.getPropertyValue(EXIT_WHILE_RUNNING_WARNING.name()));
            if(ExitDialog.getDialog().getSelectedOption() == ExitDialog.Option.YES){
                Platform.exit();
            }
        }else if(!((AppUI) applicationTemplate.getUIComponent()).getSaveButton().isDisabled()){
            ExitDialog.getDialog().show("Unsaved Work", "Do you want to save before exiting?");
            if(ExitDialog.getDialog().getSelectedOption() == ExitDialog.Option.YES){
                handleSaveRequest();
            }
        }else{
            Platform.exit();
        }
    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;

        try {
            WritableImage image1 = ((AppUI) applicationTemplate.getUIComponent()).getChart().snapshot(new SnapshotParameters(), null);

            FileChooser fileChooser1 = new FileChooser();

            fileChooser1.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(SCREENSHOT_FILE_EXT_DESC.name()), manager.getPropertyValue(SCREENSHOT_FILE_EXT.name())));

            File file = fileChooser1.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

            if(file != null){
                ImageIO.write(SwingFXUtils.fromFXImage(image1, null), "png", file);
            }else{
                this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(SNAPSHOT_ERROR_TITLE.name()), manager.getPropertyValue(SNAPSHOT_ERROR_MSG.name()));

            }
        }
        catch(IOException  e){
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(SPECIFIED_FILE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));

        }
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

        PropertyManager manager = applicationTemplate.manager;
        ConfirmationDialog.Option x = ConfirmationDialog.getDialog().getSelectedOption();
        selectedOption = x;

        TSDProcessor processor = new TSDProcessor();
        try {
            processor.processString(((AppUI) applicationTemplate.getUIComponent()).getTextArea());
            processor.checkForDuplicates(((AppUI) applicationTemplate.getUIComponent()).getTextArea());
            if(processor.getDuplicates()){
                applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(UNABLE_TO_SAVE_DUPLICATE_TITLE.name()), manager.getPropertyValue(UNABLE_TO_SAVE_DUPLICATE.name()));
            }else{
                this.applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION).show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
                x = ConfirmationDialog.getDialog().getSelectedOption();

                if (x == ConfirmationDialog.Option.YES) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setInitialDirectory(new File(manager.getPropertyValue(DATA_RESOURCE_PATH.name())));

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
                }
                else if (x == ConfirmationDialog.Option.NO){
                    return true;
                }
            }
        } catch (Exception e) {
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(SPECIFIED_FILE.name()), manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));

        }
        return false;
    }

    public Boolean getCheck(){
        return check;
    }

}
