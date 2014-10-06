package info.faceland.loot.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public final class SmartTextFile {

    private File debugFile;

    public SmartTextFile(File file) {
        this.debugFile = file;
    }

    public static void writeToFile(InputStream stream, File output, boolean onlyIfNew) {
        if (onlyIfNew && output.exists()) {
            return;
        }
        SmartTextFile file = new SmartTextFile(output);
        List<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            String p;
            while ((p = bufferedReader.readLine()) != null) {
                if (!p.contains("#") && p.length() > 0) {
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        file.write(list.toArray(new String[list.size()]));
    }

    public void write(String... messages) {
        try {
            File saveTo = getDebugFile();
            if (!saveTo.getParentFile().exists() && !saveTo.getParentFile().createNewFile()) {
                return;
            }
            if (!saveTo.exists() && !saveTo.createNewFile()) {
                return;
            }
            FileWriter fw = new FileWriter(saveTo.getPath(), false);
            PrintWriter pw = new PrintWriter(fw);
            for (String message : messages) {
                pw.println(message);
            }
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> read() {
        List<String> list = new ArrayList<>();
        try {
            File readFile = getDebugFile();
            if (!readFile.getParentFile().exists() && !readFile.getParentFile().mkdirs()) {
                return list;
            }
            if (!readFile.exists() && !readFile.createNewFile()) {
                return list;
            }
            FileReader fileReader = new FileReader(readFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String p;
            while ((p = bufferedReader.readLine()) != null) {
                if (!p.contains("#") && p.length() > 0) {
                    list.add(p);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public File getDebugFile() {
        return debugFile;
    }

}
