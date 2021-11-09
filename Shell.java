package com.ys.apptv.utils;


public class Shell {

    private static final Runtime RUNTIME;
    static {
        RUNTIME = Runtime.getRuntime();
    }

    public static void exeCommand(String... command) {
        try {
            RUNTIME.exec(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exeSuCommand(String cmd) {
        exeCommand("su", "-c", cmd);
    }

    public static void exeInstallApp(String apkPath) {
        exeSuCommand("pm install -r " + apkPath);
    }

    public static Result exeCommandForResult(String command) {
        return getResultForCommand(command.split(" "));
    }

    public static Result exeCommandForResult(String[] command) {
        return getResultForCommand(command);
    }

    private static Result getResultForCommand(String... command) {
        try {
            Process process = RUNTIME.exec(command);
            int code = process.waitFor();
            int len;
            byte[] buf = new byte[1024];
            if (code == 0) {
                len = process.getInputStream().read(buf);
            } else {
                len = process.getErrorStream().read(buf);
            }
            return new Result(code, new String(buf, 0, len));
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(-1, e.getMessage());
        }
    }

    public static class Result{
        public int code;
        public String msg;

        private Result(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }
}
