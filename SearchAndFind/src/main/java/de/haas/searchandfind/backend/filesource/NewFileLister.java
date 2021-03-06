

package de.haas.searchandfind.backend.filesource;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Lists all files in a directory recursively and enqueues so
 * they can be added to the database as documents.
 *
 *
 * @author Michael Haas <haas@cl.uni-heidelberg.de>
 */
public class NewFileLister extends Thread {

    private BlockingQueue<FileWrapper> queue;
    private File dir;
    private final static int QUEUE_CAPACITY = 500;
    /**
     * Constructor. Use this to pass in an external queue.
     *
     * @param fileQueue Queue where files are inserted
     * @param directory Directory for which files are to be listed
     */
    public NewFileLister(BlockingQueue<FileWrapper> fileQueue, File directory) {
        this.queue = fileQueue;
        this.dir = directory;
    }

    /**
     * Constructor. Use this to let the NewFileLister create its own queue.
     * 
     * @param directory Directory for which files are to be listed
     */
    public NewFileLister(File directory) {
        this(new LinkedBlockingQueue<FileWrapper>(QUEUE_CAPACITY), directory);
    }

    /**
     * Starts file listing.
     */
   @Override
   public void run() {
        if (this.dir.isDirectory()) {
            try {
                this.listFiles(this.dir);
            } catch (InterruptedException ex) {
                // TODO: do something about this.
                Logger.getLogger(NewFileLister.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            throw new RuntimeException("dir field is not a directory.");
        }


   }

   /**
    * Lists files recursively. Enqueues normal files into queue.
    *
    * @param directory directory to be listed recursively
    * @throws InterruptedException if something interrupts enqueue operation
    */
   private void listFiles(final File directory) throws InterruptedException {

       File[] children = directory.listFiles();
       for (int ii = 0; ii < children.length; ii++) {
           File currentFile = children[ii];
           // if directory, recurse. if file, enqueue
           if (currentFile.isDirectory()) {
               this.listFiles(currentFile);
           } else if (currentFile.isFile()) {
               // enqueue
               this.queue.put(new FileWrapper(currentFile));
           }
       }

   }

}
