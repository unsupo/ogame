package ogame.utility;

import utilities.Utility;
import utilities.filesystem.FileOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Created by jarndt on 9/22/16.
 */
public class WatchServiceCreator {
    static {
        try {
            fileChanges = new PriorityBlockingQueue<>();
            getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static PriorityBlockingQueue<File> fileChanges;

    private static WatchServiceCreator instance;

    private Thread thread;
    public static WatchServiceCreator getInstance() throws IOException {
        if(instance == null)
            instance = new WatchServiceCreator();
        return instance;
    }
    public static void start() throws IOException {
        if(!getInstance().thread.isAlive())
            getInstance().thread.start();
    }

    private WatchServiceCreator() throws IOException {
        List<File> f = FileOptions.getAllFilesEndsWith(Utility.RESOURCE_DIR + "runner_scripts/", ".script");
        fileChanges.addAll(f);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    WatchServiceCreator.watchDirectoryPath(Paths.get(Utility.RESOURCE_DIR+"runner_scripts"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void watchDirectoryPath(Path path) throws IOException {
        fileChanges.addAll(FileOptions.getAllFilesEndsWith(path.toFile().getAbsolutePath(),".script"));
        // Sanity check - Check if path is a folder
        try {
            Boolean isFolder = (Boolean) Files.getAttribute(path,
                    "basic:isDirectory", NOFOLLOW_LINKS);
            if (!isFolder) {
                throw new IllegalArgumentException("Path: " + path + " is not a folder");
            }
        } catch (IOException ioe) {
            // Folder does not exists
            ioe.printStackTrace();
        }

        System.out.println("Watching path: " + path);

        // We obtain the file system of the Path
        FileSystem fs = path.getFileSystem ();

        // We create the new WatchServiceCreator using the new try() block
        try(WatchService service = fs.newWatchService()) {

            // We register the path to the service
            // We watch for creation events
            path.register(service, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

            // Start the infinite polling loop
            WatchKey key = null;
            while(true) {
                key = service.take();

                // Dequeueing events
                WatchEvent.Kind<?> kind = null;
                for(WatchEvent<?> watchEvent : key.pollEvents()) {
                    // Get the type of the event
                    kind = watchEvent.kind();
                    if (OVERFLOW == kind) {
                        continue; //loop
                    } else if (ENTRY_CREATE == kind || ENTRY_MODIFY == kind) {
                        // A new Path was created
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        // Output
                        if(fileChanges.contains(newPath.toFile()))
                            continue;
                        fileChanges.add(newPath.toFile());
                    }else if (ENTRY_DELETE == kind) {
                        // A new Path was created
                        Path newPath = ((WatchEvent<Path>) watchEvent).context();
                        // Output
                        if(fileChanges.contains(newPath.toFile())) {
                            fileChanges.remove(newPath.toFile());
                            QueueManager.getFileContents().remove(newPath.toFile());
                        }
                    }
                }

                if(!key.reset()) {
                    break; //loop
                }
            }

        } catch(IOException ioe) {
            ioe.printStackTrace();
        } catch(InterruptedException ie) {
            ie.printStackTrace();
        }

    }


    public static void main(String[] args) throws IOException,
            InterruptedException {
        // Folder we are going to watch
        Path folder = Paths.get(Utility.RESOURCE_DIR+"runner_scripts");
        watchDirectoryPath(folder);
    }
}
