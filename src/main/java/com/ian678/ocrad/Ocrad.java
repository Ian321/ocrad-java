package com.ian678.ocrad;

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
}
