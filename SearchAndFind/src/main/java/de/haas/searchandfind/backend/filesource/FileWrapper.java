package de.haas.searchandfind.backend.filesource;

import java.io.File;

/**
 * Wrapper class for file objects. Indicates state of the file,
 * e.g. whether the file has been deleted or updated.
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class FileWrapper {

    /** wrapped file object */
    private File file;
    /** state of the file */
    private FileState state;

    /**
     * Constructor. Sets state to FileState.FILE_NEW
     * 
     * @param wrappedFile File descriptor to be wrapped
     */
    public FileWrapper(File wrappedFile) {
        this(wrappedFile, FileState.FILE_NEW);
    }

    /**
     * Constructor.
     *
     * @param wrappedFile File descriptor to be wrapped
     * @param fileState State of the file
     */
    public FileWrapper(File wrappedFile, FileState fileState) {
        this.file = wrappedFile;
        this.state = fileState;
    }

    public static enum FileState {
        FILE_NEW, FILE_DELETED, FILE_MODIFIED
    }

    /**
     * Getter for file field.
     * @return
     */
    public File getFile() {
        return this.file;
    }

    /**
     * Getter for state field.
     * @return
     */
    public FileState getState() {
        return this.state;
    }
}
