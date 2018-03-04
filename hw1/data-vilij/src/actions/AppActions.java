package actions;

import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.application.Platform;

import javafx.beans.property.Property;
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
import java.util.ArrayList;
import java.util.Scanner;

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
//    ArrayList<String> linesLeft;
    Boolean check = false;

    ConfirmationDialog.Option selectedOption;

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
                while(!((AppUI) applicationTemplate.getUIComponent()).getTextArea().isEmpty()) {
                    applicationTemplate.getUIComponent().clear();
                }
                ((AppUI) applicationTemplate.getUIComponent()).getChart().getData().clear();
                dataFilePath = null;
            }
        } catch (IOException e) {
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(ERROR_TITLE.name()), manager.getPropertyValue(ERROR_MSG.name()));
        }
    }

    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;
//        String input1 = ((AppUI)applicationTemplate.getUIComponent()).getTextArea();
//        TSDProcessor processor1 = new TSDProcessor();

        try {
          //  processor1.processString(input1);

            if(dataFilePath == null){
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialDirectory(new File(manager.getPropertyValue(DATA_RESOURCE_PATH.name())));
                fileChooser.getInitialDirectory();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT.name()), manager.getPropertyValue(DATA_FILE_EXT_DESC.name())));

                File file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
                dataFilePath = file.toPath();
                applicationTemplate.getDataComponent().saveData(dataFilePath);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            }
            else{
                applicationTemplate.getDataComponent().saveData(dataFilePath);
                ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);
            }
        } catch (Exception e) {
            this.applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(DISPLAY_ERROR_TITLE.name()), manager.getPropertyValue(DISPLAY_ERROR_MSG.name()));
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
            ((AppUI) applicationTemplate.getUIComponent()).getSaveButton().setDisable(true);


        }catch (NullPointerException e){

            applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(manager.getPropertyValue(LOAD_ERROR_TITLE.name()), manager.getPropertyValue(LOAD_ERROR_MSG.name()));
        }
//        PropertyManager manager = applicationTemplate.manager;
//        FileChooser chooser1 = new FileChooser();
//        chooser1.setTitle(applicationTemplate.manager.getPropertyValue(OPEN_FILE_TITLE.name()));
//
//        try{
//            File file = chooser1.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
//            if(file != null) {
//                Scanner sc = new Scanner(file);
//                int lineCount = Integer.parseInt(applicationTemplate.manager.getPropertyValue(LINE_COUNTER.name()));
//                int totalLines = 0;
//                String y = "";
//
//                linesLeft = new ArrayList<String>();
//
//                while (sc.hasNextLine()) {
//                    if (lineCount < Integer.parseInt(applicationTemplate.manager.getPropertyValue(MAX_LINES.name()))) {
//                        y += sc.nextLine() + "\n";
//                        lineCount++;
//                        totalLines++;
//                    }else {
//                        linesLeft.add(sc.nextLine() + "\n");
//                        totalLines++;
//                    }
//                }
//                ((AppUI) applicationTemplate.getUIComponent()).setTextArea(y);
//                if (totalLines > 10) {
//                    String x = manager.getPropertyValue(LINE_FILLED.name()) + totalLines + manager.getPropertyValue(LINES_DISPLAY.name());
//                    applicationTemplate.getDialog(Dialog.DialogType.ERROR).show(TOTAL_LINES_ERROR.name(), x);
//                }
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            System.out.println("File Not Found Exception");
//        }
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
        PropertyManager manager = applicationTemplate.manager;

        try {
            WritableImage image1 = ((AppUI) applicationTemplate.getUIComponent()).getChart().snapshot(new SnapshotParameters(), null);

            FileChooser fileChooser1 = new FileChooser();

            fileChooser1.getExtensionFilters().add(new FileChooser.ExtensionFilter(manager.getPropertyValue(SCREENSHOT_FILE_EXT_DESC.name()), manager.getPropertyValue(SCREENSHOT_FILE_EXT.name())));

            File file = fileChooser1.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

            if(file != null){
                ImageIO.write(SwingFXUtils.fromFXImage(image1, null), "png", file);
            }else{
                //error dialog
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
        this.applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION).show(manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()), manager.getPropertyValue(SAVE_UNSAVED_WORK.name()));
        ConfirmationDialog.Option x = ConfirmationDialog.getDialog().getSelectedOption();
        selectedOption = x;

            if (x == ConfirmationDialog.Option.YES) {
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
                return true;
            }

        return false;
    }

//    public ArrayList<String> getLinesLeft(){
//        return linesLeft;
//    }

    public Boolean getCheck(){
        return check;
    }

}
