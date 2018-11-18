package wsq.study.quartz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局流
 *
 * @author weisq
 * @date 2018/11/15
 */
public class StreamGobbler extends Thread {
    private static final SimpleDateFormat SB_DATETIME_STANDARD_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    InputStream is;
    String type;
    String logFilePath;

    public StreamGobbler(InputStream is, String type, String logFilePath) {
        this.is = is;
        this.type = type;
        this.logFilePath = logFilePath;
    }

    public void run() {
        try {
            OutputStream outputStream = null;
            File file = new File(logFilePath);
            outputStream = new FileOutputStream(file, true);
            byte[] bytes = new byte[1024];
            if (type.equals("Error")) {
                outputStream.write("\r\n\r\n=================我是分割线===============我是分割线===============\r\n".getBytes());
                outputStream.write("=\r\n".getBytes());
                outputStream.write(
                    String.format("=                       日志记录时间：%s" + "\r\n",
                        SB_DATETIME_STANDARD_FORMAT.format(new Date())).getBytes());
                outputStream.write("=\r\n".getBytes());
                outputStream.write("=================我是分割线===============我是分割线===============\r\n\r\n".getBytes());
            }

            while ((is.read(bytes)) != -1) {
                outputStream.write(bytes);
            }

            outputStream.flush();
            is.close();
            outputStream.close();

            // InputStreamReader isr = new InputStreamReader(is);
            // BufferedReader br = new BufferedReader(isr);
            // String line = null;
            // while ((line = br.readLine()) != null) {
            // if (type.equals("Error")) {
            // // System.out.println("Error :" + line);
            // } else {
            // // System.out.println("Debug:" + line);
            // }
            // }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {

        }
    }
}