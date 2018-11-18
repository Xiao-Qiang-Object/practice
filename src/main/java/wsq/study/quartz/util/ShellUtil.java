package wsq.study.quartz.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellUtil
{
    private final static Logger logger = LoggerFactory.getLogger(ShellUtil.class);

    /**
     * 执行命令
     * 
     * @param scriptCommandLine
     */
    public static void executeScript(String scriptCommandLine)
    {
        CommandLine commandLine = CommandLine.parse(scriptCommandLine);
        try
        {
            new DefaultExecutor().execute(commandLine);
        } catch (IOException ex)
        {
            logger.error("execute command failed：{}", ExceptionUtils.getFullStackTrace(ex));
            throw new RuntimeException("execute command failed.");
        }
    }

    /**
     * 启动独立线程执行命令
     * 
     * @param command 命令
     * @return
     */
    public static boolean executeScriptByProcess(String command, String logFilePath) throws Exception
    {
        boolean flag = true;

        // Runtime.getRuntime().exec(command);// Runtime.getRuntime()返回当前应用程序的Runtime对象

        Process ps = Runtime.getRuntime().exec(command);// Runtime.getRuntime()返回当前应用程序的Runtime对象

        StreamGobbler errorGobbler = new StreamGobbler(ps.getErrorStream(), "Error", logFilePath);
        errorGobbler.start();
        StreamGobbler outputGobbler = new StreamGobbler(ps.getInputStream(), "Output", logFilePath);
        outputGobbler.start();

        // Runtime rt = Runtime.getRuntime(); // Runtime.getRuntime()返回当前应用程序的Runtime对象
        // final Process ps = rt.exec("sudo su hive");
        //
        // DataOutputStream os = new DataOutputStream(ps.getOutputStream());
        // os.writeBytes("source /etc/profile\n");
        // os.writeBytes(command + "\n");
        // os.flush();

        return flag;
    }

    /**
     * 启动独立线程执行命令 并等待结果
     * 
     * @param command 命令
     * @return
     */
    public static boolean executeScriptByProcessAndWaitFor(String command) throws Exception {
        boolean flag = false;
        Runtime rt = Runtime.getRuntime(); // Runtime.getRuntime()返回当前应用程序的Runtime对象
        final Process ps = rt.exec(command);

        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
                try {
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        // 测试开启
                        logger.info(line);
                    } ;
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        BufferedReader br = null;
        br = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
        String line = null;
        while ((line = br.readLine()) != null) {
            logger.error(line);
        }

        ps.waitFor();
        int result = ps.exitValue(); // 接收执行完毕的返回值
        if (result == 0) {
            flag = true;
            logger.debug("执行完成,command:{}.", command);
        } else {
            logger.error("执行失败，command:{}，返回值：{}.", command, result);
        }

        ps.destroy(); // 销毁子进程

        return flag;
    }


    public static void main(String[] args)
    {
        // executeScript("D:\\projects\\git pub\\standard_elastic_job\\package\\package.bat");
    }
}
