package utilities.fileio;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import utilities.email.OneEmail;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileOptions {
    public final static String OS = System.getProperty("os.name").toLowerCase();

    public static final String SEPERATOR = System.getProperty("file.separator"),
            DEFAULT_DIR = System.getProperty("user.dir") + SEPERATOR;
    public static final String WEB_DRIVER_DIR = DEFAULT_DIR + "config" + SEPERATOR + "web_drivers" + SEPERATOR;

    public static void main(String[] args) throws IOException {
//		String path = System.getProperty("user.dir");
//		String s = System.getProperty("file.separator");
//		String out = path + s + ".." + s + "all_jars", in = path + s + ".." + s
//				+ "jars";
//		moveAllFiles(in, out);

        getAllFilesRegex(getBaseDirectories()[0].getAbsolutePath(), "mvn").forEach(System.out::println);
    }
    public static ExecutorService runConcurrentProcess(List<Callable> callables){
        return runConcurrentProcess(callables, 100, 5, TimeUnit.MINUTES);
    }
    public static ExecutorService runConcurrentProcess(List<Callable> callables, int threads){
        return runConcurrentProcess(callables, threads, 5, TimeUnit.MINUTES);
    }
    public static ExecutorService runConcurrentProcess(List<Callable> callables, int time, TimeUnit timeUnit){
        return runConcurrentProcess(callables, 100, time, timeUnit);
    }
    public static ExecutorService runConcurrentProcess(List<Callable> callables, int threads, int time, TimeUnit timeUnit){
        ExecutorService service = Executors.newFixedThreadPool(threads);
        callables.forEach(a->{
            service.submit(a);
        });

        try {
            System.out.println("attempt to shutdown executor");
            service.shutdown();
            service.awaitTermination(time, timeUnit);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!service.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            service.shutdownNow();
            System.out.println("shutdown finished");
        }
//        while (!service.isTerminated() && !service.isShutdown())
//            Thread.sleep(1000);
        return service;
    }

    public static HashMap<String,List<String>> getZipFileContents(String path) throws IOException {
        HashMap<String,List<String>> fileContents = new HashMap<>();
        ZipFile zip = new ZipFile(path);
        for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            if (!entry.isDirectory()) {
                //TODO add other types of files to process
//                if (FilenameUtils.getExtension(entry.getName()).equals("png")) {
//                    byte[] image = getImage(zip.getInputStream(entry));
//                    //do your thing
//                } else
                if (FilenameUtils.getExtension(entry.getName()).equals("txt")) {
                    List<String> fileSeperator = new ArrayList<>();
                    StringBuilder out = new StringBuilder();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zip.getInputStream(entry)));
                    String line;
                    try {
                        while ((line = reader.readLine()) != null)
                            fileSeperator.add(line);
//                            out.append(line);
                    } catch (IOException ee) {
                        // do something, probably not a text file
//                            ee.printStackTrace();
                    }
                    fileContents.put(entry.getName(), fileSeperator);
                }
            }
        }
        return fileContents;
    }

    public static File[] getBaseDirectories() {
        return File.listRoots();
    }

    public static List<File> findOnFileSystem(String regex) throws IOException {
        Pattern p = Pattern.compile(regex);
        List<File> files = new ArrayList<>();
        for (File f : getBaseDirectories())
            files.addAll(
//					Files.walk(f.toPath())
//							.filter(fp->)
//							.filter(fp->p.matcher(fp.toFile().getName()).find())

                    Files.find(f.toPath(), Integer.MAX_VALUE,
                            (fp, fa) -> Files.isReadable(fp) && Files.isWritable(fp) && p.matcher(fp.toFile().getName()).find()
                    )
                            .map(a -> a.toFile())
                            .collect(Collectors.toList()));

        return files;
    }

    public static String cleanFilePath(String filePath) {
        String regex = "\\[\\*replace_me\\*\\]";
        filePath = filePath.replaceAll("/", regex);
        filePath = filePath.replaceAll("\\\\", regex);
        return filePath.replaceAll(regex, Matcher.quoteReplacement(System.getProperty("file.separator")));
    }

    public static void copyFileUtil(File from, File to) throws IOException {
        FileUtils.copyFile(from, to);
    }

    public static void downloadFile(String link, String path) throws IOException {
        URL website = new URL(link);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(path);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
    }


    public static void deleteDirectory(String path) throws IOException {
        FileUtils.deleteDirectory(new File(path));
//		Files.delete(new File(path).toPath());
    }


    public static void writeToFileOverWrite(String filePath, String contents) throws IOException {
        FileOutputStream out = new FileOutputStream(filePath);
        out.write(contents.getBytes());
        out.close();
    }

    public static BufferedWriter writeToFileAppend(String file, String contents) throws IOException {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, true));
        } catch (FileNotFoundException fnfe) {
            String[] split = file.split("\\\\");
            new File(file.substring(0, file.indexOf(split[split.length - 1]))).mkdirs();
            return writeToFileAppend(file, contents);
        }
        bw.write(contents);
        bw.newLine();
        bw.flush();
        return bw;
    }

    public static String readFileIntoString(String path) throws IOException {
        return readFileIntoListString(path).stream().collect(Collectors.joining("\n"));
    }public static List<String> readFileIntoListString(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        List<String> result = new ArrayList<>();
        while ((line = br.readLine()) != null)
            result.add(line);
        br.close();
        return result;
    }

    public static List<File> getAllFilesRegex(String path, String regex) throws IOException {
        List<File> files = new ArrayList<>();
        _getAllFiles(path, files);
        return files.stream().filter(a->Pattern.compile(regex).matcher(a.getName()).find()).collect(Collectors.toList());
    }public static List<File> getAllFilesEndsWith(String path, String endsWith) throws IOException{
        List<File> files = new ArrayList<>();
        _getAllFiles(path,files);
        return files.stream().filter(a->a.getName().endsWith(endsWith)).collect(Collectors.toList());
    }public static List<File> getAllFiles(String path, String contains) throws IOException{
        List<File> files = new ArrayList<>();
        _getAllFiles(path,files);
        return files.stream().filter(a->a.getName().contains(contains)).collect(Collectors.toList());
    }public static List<File> getAllFiles(String path) throws IOException{
        List<File> files = new ArrayList<>();
        _getAllFiles(path,files);
        return files;
    }private static void _getAllFiles(String path,List<File> files) throws IOException{
        if(!Files.isReadable(Paths.get(path))) return;
        for(File f : new File(path).listFiles())
            if(f.isDirectory())
                _getAllFiles(f.getAbsolutePath(), files);
            else
                files.add(f);
    }

    public static List<File> findFile(String path, String name) throws IOException {
        final List<File> foundFiles = new ArrayList<>();
        Files.walk(Paths.get(path))
                .forEach(
                        filePath -> {
                            if (Files.isRegularFile(filePath)
                                    && filePath.getFileName().toString()
                                    .contains(name)) {
//								System.out.println(filePath);
                                foundFiles.add(filePath.toFile());
                            }
                        });
        return foundFiles;
    }public static List<File> findFileRegex(String path, String regex) throws IOException {
        List<File> files = new ArrayList<>();
        Files.walk(Paths.get(path))
                .forEach(
                        filePath -> {
                            if (Files.isRegularFile(filePath)
                                    && Pattern.compile(regex).matcher(filePath.getFileName().toString()).find()) {
                                files.add(filePath.toFile());
                            }
                        });
        return files;
    }


    public static void moveAllFiles(String in, String out) throws IOException {
        String s = System.getProperty("file.separator");
        for (File f : new File(in).listFiles())
            if (f.isFile() && f.getAbsolutePath().endsWith(".jar"))
                copyFile(f, new File(out + s + f.getName()));
            else if (f.isDirectory())
                moveAllFiles(f.getAbsolutePath(), out);
    }

    public static void copyFile(File source, File dest) throws IOException {
        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(source);
            output = new FileOutputStream(dest);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            input.close();
            output.close();
        }
    }

    public static void renameAllFiles(String path, String ext) {
        for (File f : new File(path).listFiles())
            if (f.isFile())
                f.renameTo(new File(f.getAbsolutePath() + ext));
    }

    private static int next = 0;
    public static int getNext() {
        return next++;
    }
}

