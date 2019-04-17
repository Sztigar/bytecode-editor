package app.save;

import app.javassist.Add;
import app.javassist.Javassist;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;


//tutaj mamy wszystkie operacje na plikach jak wypakowywanie, pakowanie, usuwanie i dodawanie jythona
public abstract class FileActions {


    //ze stacka
    static void unpack(File jarFile, File destinationFile) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry file = (JarEntry) enumEntries.nextElement();
            File f = new File(destinationFile.getPath() + File.separator + file.getName());
            if (f.getParentFile().mkdirs()) ;
            if (file.isDirectory()) {
                f.mkdir();
                continue;
            }
            InputStream is = jar.getInputStream(file);
            FileOutputStream fos = new FileOutputStream(f);
            while (is.available() > 0) {
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
        jar.close();
    }

    //ze stacka
    static void pack(File newFile) throws IOException {
        JarOutputStream target = new JarOutputStream(new FileOutputStream(newFile.getPath() + "\\..\\" + newFile.getName() + ".jar"));
        add(new File(newFile.getCanonicalPath()), target, newFile);
        target.close();
    }

    //ze stacka
    private static void add(File source, JarOutputStream target, File destinationFile) throws IOException {
        BufferedInputStream in = null;
        try {
            if (source.isDirectory()) {
                String name = source.getPath().replace("\\", "/");
                if (!name.isEmpty()) {
                    if (!name.endsWith("/"))
                        name += "/";
                    name = name.substring(destinationFile.getPath().length() + 1);
                    JarEntry entry = new JarEntry(name);
                    entry.setTime(source.lastModified());
                    target.putNextEntry(entry);
                    target.closeEntry();

                }
                for (File nestedFile : source.listFiles())
                    add(nestedFile, target, destinationFile);
                return;
            }
            String fileName = source.getPath().replace("\\", "/").substring(destinationFile.getPath().length() + 1);
            JarEntry entry = new JarEntry(fileName);
            entry.setTime(source.lastModified());
            target.putNextEntry(entry);
            in = new BufferedInputStream(new FileInputStream(source));
            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1)
                    break;
                target.write(buffer, 0, count);
            }
            target.closeEntry();

        } finally {
            if (in != null)
                in.close();
        }
    }
    //usuwa folder z cala zawartoscia
    static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }
    //tworzy foldery pod sciezkami ktore mamy w liscie addedPackages
    static void createPackages(File saveFile) {
        for (String p : Add.getAddedPackages()) {
            new File(saveFile.getPath() + "\\" + p).mkdirs();
        }
    }
    //zczytuje wszystkie klasy z naszego jara i zapisuje je by uwzglednic zmiany ktore wprowadzilismy w edytowanym programie
    static void saveClasses(File jarFile, File destinationFile) throws IOException, NotFoundException {
        ArrayList<String> classes = FileActions.getClasses(new JarInputStream(new FileInputStream(jarFile.getPath())));
        for (String c : classes) {
            CtClass writeClass = Javassist.getPool().get(c);
            writeClass.debugWriteFile(destinationFile.getPath());
        }
    }
    //zapisuje dodane przez nas klasy
    static void saveAddedClasses(File saveFile) throws NotFoundException {
        for (String c : Add.getAddedClasses()) {
            CtClass writeClass = Javassist.getPool().get(c);
            writeClass.debugWriteFile(saveFile.getPath());
        }
    }
    //dodaje do manifestu Class-Path:jython-standalone-2.5.4-rc1.jar\n
    static void addJython(File destinationFile) throws IOException {
        RandomAccessFile f = new RandomAccessFile(destinationFile + "\\META-INF\\MANIFEST.MF", "rw");
        long length = f.length() - 1;
        byte b;
        do {
            length -= 1;
            f.seek(length);
            b = f.readByte();
        } while (b != 10);
        f.setLength(length + 1);
        f.close();
        FileWriter fr = new FileWriter(destinationFile + "\\META-INF\\MANIFEST.MF", true);
        fr.write("Class-Path: jython-standalone-2.5.4-rc1.jar\n");
        fr.close();
    }
    //pobiera z jara wszystkie sciezki do klasy jakie tam sa
    private static ArrayList<String> getClasses(JarInputStream jarInputStream) throws IOException {
        JarEntry jarEntry;
        ArrayList<String> classList = new ArrayList<String>();
        while (true) {
            jarEntry = jarInputStream.getNextJarEntry();
            if (jarEntry == null) {
                break;
            }
            if (jarEntry.getName().endsWith(".class")) {
                String className = jarEntry.getName().replaceAll("/", "\\.");
                String myClass = className.substring(0, className.lastIndexOf('.'));
                classList.add(myClass);
            }
        }
        return classList;
    }
}
