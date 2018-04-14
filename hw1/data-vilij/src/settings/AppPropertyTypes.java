package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,

    DISPLAY_ERROR_TITLE,
    DISPLAY_ERROR_MSG,
    CHART_TITLE,
    DISPLAY_BUTTON_NAME,

    ERROR_TITLE,
    ERROR_MSG,

    AVERAGE_Y_VALUE,
    LINE_SERIES,
    STYLE_SHEET_PATH,

    SNAPSHOT_ERROR_TITLE,
    SNAPSHOT_ERROR_MSG,
    SCREENSHOT_FILE_EXT,
    SCREENSHOT_FILE_EXT_DESC,

    READ_ONLY_TITLE,
    OPEN_FILE_TITLE,

    MAX_LINES,
    LINE_COUNTER,
    LINE_FILLED,
    LINES_DISPLAY,
    TOTAL_LINES_ERROR,

    LOAD_ERROR_TITLE,
    LOAD_ERROR_MSG,

    SAVE_ERROR_MSG,
    SAVE_ERROR_TITLE,
    SAVE_REGULAR_MSG,

    UNABLE_TO_SAVE_DUPLICATE,
    UNABLE_TO_SAVE_DUPLICATE_TITLE,
    LINE_DUPLICATE,

    HOVER_NAME,
    UNABLE_TO_LOAD_DUPLICATE_TITLE,
    UNABLE_TO_LOAD_DUPLICATE,

    SOURCE_NAME,

    ALGO_TYPE_1,
    ALGO_TYPE_2,

    DONE_BUTTON_NAME,
    EDIT_BUTTON_NAME,

    ALGO_NAME,
    RANDO_CLASSIF,
    RANDO_CLUST,
    CLASSIFICATION_TITLE,
    CLUSTERING_TITLE,
    SETTINGS_TITLE,

    SOME_RAND_CLASS,
    SOME_RAND_CLUST,
    RUN_BUTTON_TITLE


}
