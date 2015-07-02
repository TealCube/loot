/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.loot.io;

import java.io.*;
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

    public File getDebugFile() {
        return debugFile;
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

}
