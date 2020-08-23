package com.ian678.ocrad;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Ocrad {
    public native String OCRAD_version();
    public native long OCRAD_open();
    public native int OCRAD_close(long ocrdes);
    public native int OCRAD_get_errno(long ocrdes);
    public native int OCRAD_set_image(long ocrdes, long image, boolean invert);
    public native int OCRAD_set_image_from_file(long ocrdes, String filename, boolean invert);
    public native int OCRAD_set_utf8_format(long ocrdes, boolean utf8); // 0 = byte, 1 = utf8
    public native int OCRAD_set_threshold(long ocrdes, int threshold); // 0..255, -1 = auto
    public native int OCRAD_scale(long ocrdes, int value);
    public native int OCRAD_recognize(long ocrdes, boolean layout);
    public native int OCRAD_result_blocks(long ocrdes);
    public native int OCRAD_result_lines(long ocrdes, int blocknum); // 0..blocks-1
    public native int OCRAD_result_chars_total(long ocrdes);
    public native int OCRAD_result_chars_block(long ocrdes, int blocknum); // 0..blocks-1
    public native int OCRAD_result_chars_line(long ocrdes, int blocknum, int linenum); // 0..blocks-1, 0..lines(block)-1
    public native String OCRAD_result_line(long ocrdes, int blocknum, int linenum); // 0..blocks-1, 0..lines(block)-1
    public native int OCRAD_result_first_character(long ocrdes);

    private static boolean hasSetUp = false;
    public static void setUp() {
        if (hasSetUp) return;
        hasSetUp = true;

        String name = "ocrad-java";
        String osName = System.getProperty("os.name").toLowerCase();
        
        String suffix = "";
        if (osName.contains("win")) {
            suffix = ".dll";
        } else {
            name = "lib" + name;
            if (osName.contains("mac")) {
                suffix = ".dylib";
            } else {
                suffix = ".so";
            }
        }

        URL resource = Ocrad.class.getClassLoader().getResource(name + suffix);
        if (resource == null) {
            File a = new File(System.getProperty("user.dir") + File.separator + name + suffix);
            File b = new File(System.getProperty("user.dir") + File.separator + "build" + File.separator + "resources" + File.separator + "main" + File.separator + name + suffix);

            if (a.exists()) System.load(a.getAbsolutePath());
            else if (b.exists()) System.load(b.getAbsolutePath());
        } else {
            if (resource.getProtocol() == "jar") {
                // Inside a jar
                try {
                    InputStream in = Ocrad.class.getClassLoader().getResourceAsStream(name + suffix);
                    File tmp = File.createTempFile(name, suffix);
                    OutputStream out = new FileOutputStream(tmp);
                    
                    int c;
                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                    in.close();
                    out.close();
                    
                    System.load(tmp.getAbsolutePath());
                } catch (IOException e) {
                    System.err.println("Failed to load ocrad-java from inside the jar.");
                }
            } else {
                System.load(new File(resource.getFile()).getAbsolutePath());
            }
        }
    }
    public static String recognize(File file, boolean utf8) {
        Ocrad m = new Ocrad();
        long ocrdes = m.OCRAD_open();
        m.OCRAD_set_image_from_file(ocrdes, file.getAbsolutePath(), false);
        m.OCRAD_set_utf8_format(ocrdes, utf8);
        m.OCRAD_set_threshold(ocrdes, -1);
        m.OCRAD_recognize(ocrdes, false);

        String out = "";
        int blocks = m.OCRAD_result_blocks(ocrdes);
        for (int block = 0; block < blocks; block++) {
            int lines = m.OCRAD_result_lines(ocrdes, block);
            for (int line = 0; line < lines; line++) {
                String s = m.OCRAD_result_line(ocrdes, block, line);
                if (s != null) {
                    out += s;
                }
            }
            out += "\n";
        }

        m.OCRAD_close(ocrdes);
        return out;
    }
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar ocrad.java file [utf8]");
            return;
        }
        Ocrad.setUp();
        System.out.print(recognize(new File(args[0]), args.length > 1));
    }
}
