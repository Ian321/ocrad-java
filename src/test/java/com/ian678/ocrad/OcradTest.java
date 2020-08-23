package com.ian678.ocrad;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.File;

public class OcradTest {
    public static Ocrad m = new Ocrad();
    static {
        String name = "ocrad-java";
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("win")) {
            name += ".dll";
        } else {
            name = "lib" + name;
            if (osName.contains("mac")) {
                name += ".dylib";
            } else {
                name += ".so";
            }
        }

        String lib = new File(Ocrad.class.getClassLoader().getResource(name).getFile()).getAbsolutePath();
        System.loadLibrary(lib);
    }

    @Test public void version() {
        assertTrue("Version should be 0.27", m.OCRAD_version().equals("0.27"));
    }
    @Test public void open_close() {
        long ocrdes = m.OCRAD_open();
        assertTrue("Should open", m.OCRAD_get_errno(ocrdes) == 0);
        assertTrue("Should close", m.OCRAD_close(ocrdes) == 0);
    }
    @Test public void pbm() {
        int err = 0;
        long ocrdes = m.OCRAD_open();

        String image = new File(Ocrad.class.getClassLoader().getResource("test.pbm").getFile()).getAbsolutePath();
        err = m.OCRAD_set_image_from_file(ocrdes, image, false);
        assertTrue("Image: " + err, err == 0);
        
        err = m.OCRAD_set_utf8_format(ocrdes, true);
        assertTrue("UTF8: " + err, err == 0);

        err = m.OCRAD_recognize(ocrdes, false);
        assertTrue("Recognize: " + err, err == 0);

        // We will only check the second line...
        String s = m.OCRAD_result_line(ocrdes, 0, 1);
        System.out.println(s);
        assertTrue("Line #2: " + s, s.equals("ABCDEFGHIJKLMN\n"));
 
        err = m.OCRAD_close(ocrdes);
        assertTrue("Close: " + err, err == 0);
    }
}
