/******************************************************************************
 * Copyright (c) 2014, Richard Harrah                                         *
 *                                                                            *
 * Permission to use, copy, modify, and/or distribute this software for any   *
 * purpose with or without fee is hereby granted, provided that the above     *
 * copyright notice and this permission notice appear in all copies.          *
 *                                                                            *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES   *
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF           *
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR    *
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES     *
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN      *
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF    *
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.             *
 ******************************************************************************/

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
