package de.haas.searchandfind.backend.filesource;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.contentobjects.jnotify.JNotify;
import net.contentobjects.jnotify.JNotifyListener;

/**
 *
 * This class watches a directory for changes.
 * 
 * Be careful: only 8192 files can be watched on default linux systems.
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class FileWatcher extends Thread {

    BlockingQueue<FileWrapper> q;
    File dir;

    public FileWatcher(BlockingQueue<FileWrapper> queue, File directory) throws IOException {
        this.q = queue;
        this.dir = directory;
    }

    @Override
    public void run() {
        JNotifyListener listener = new MyListener(this.q);
        boolean watchSubTree = true;
        // TODO: we probably do not need FILE_CREATED as we will only see empty files at that time
        // not listening for FILE_RENAME as of now. We're getting too many events.
        // TODO: are we missing out on events if we do not listen for FILE_RENAME?
        // i assume that FILE_CREATED and FILE_MODIFIED will also cover FILE_RENAMED
        int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED;
        try {
            // TODO: needs correct JNOTIFY.FILE behavior. With JNotify.ANY, we seem
            // to be receiving too many events
            JNotify.addWatch(this.dir.getCanonicalPath(), mask, watchSubTree, listener);
        } catch (IOException ex) {
            Logger.getLogger(FileWatcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Listener for changes in file system.
     *
     * TODO: find out if second parameter to methods is fully qualified path name.
     */
    private static class MyListener implements JNotifyListener {

        private BlockingQueue<FileWrapper> q;

        public MyListener(BlockingQueue<FileWrapper> queue) {
            this.q = queue;
        }

        @Override
        public void fileCreated(int i, String rootPath, String name) {
            File f = new File(rootPath, name);
            FileWrapper fw = new FileWrapper(f, FileWrapper.FileState.FILE_NEW);
            try {
                this.q.put(fw);
            } catch (InterruptedException ex) {
                Logger.getLogger(FileWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void fileDeleted(int i, String rootPath, String name) {
            File f = new File(rootPath, name);
            FileWrapper fw = new FileWrapper(f, FileWrapper.FileState.FILE_DELETED);
            try {
                this.q.put(fw);
            } catch (InterruptedException ex) {
                Logger.getLogger(FileWatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void fileModified(int i, String rootPath, String name) {
            // trigger re-indexing
            this.fileDeleted(i, rootPath, name);
            this.fileCreated(i, rootPath, name);
        }

        @Override
        public void fileRenamed(int i, String rootPath, String oldName, String newName) {
            this.fileDeleted(i, rootPath, oldName);
            this.fileCreated(i, rootPath, newName);
        }
    }
}
