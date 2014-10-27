package android_serialport_api;

import android.util.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * android.os.FileUtils 代理类
 * Created by zwq00000 on 2014/7/1.
 */
public class FileUtilsProxy {
    public static final int S_IRWXU = 00700;
    public static final int S_IRUSR = 00400;
    public static final int S_IWUSR = 00200;
    public static final int S_IXUSR = 00100;

    public static final int S_IRWXG = 00070;
    public static final int S_IRGRP = 00040;
    public static final int S_IWGRP = 00020;
    public static final int S_IXGRP = 00010;

    public static final int S_IRWXO = 00007;
    public static final int S_IROTH = 00004;
    public static final int S_IWOTH = 00002;
    public static final int S_IXOTH = 00001;

    @Nullable
    private static Method method_setPermissions;

    private static final String TAG = "FileUtilsProxy";

    private static final String proxy_class_name = "android.os.FileUtils";
    private static final String method_setPermissions_name = "setPermissions";

    static{
        Class<?> hideClass = null;
        try {
            hideClass = Class.forName(proxy_class_name);
            try {
                method_setPermissions = hideClass.getMethod(method_setPermissions_name,String.class,int.class,int.class,int.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            Log.d(TAG,e.getMessage());
        }
    }

    /**
     * 设置文件权限
     * @param file
     * @param mode
     * @param uid
     * @param gid
     * @return
     * @throws java.io.FileNotFoundException
     */
    private static int setPermissions(@NotNull File file, int mode, int uid, int gid) throws FileNotFoundException {
         if(method_setPermissions == null){
             throw new NullPointerException("setPermissions method is null");
         }
        try {
            if(!file.exists()){
                throw new FileNotFoundException("文件 "+file.getAbsolutePath() + " 不存在");
            }
            Integer result = (Integer) method_setPermissions.invoke(null, file.getAbsolutePath(), mode, uid, gid);
            Log.d(TAG,"FileUtils.setPermissions return "+result);
            return result.intValue();
        } catch (IllegalAccessException e) {
            Log.d(TAG,e.getMessage());
        } catch (InvocationTargetException e) {
            Log.d(TAG,e.getMessage());
        }
        return 1;
    }

    /**
     * 设置文件权限
     * @param file
     * @param mode
     * @throws IOException
     */
    public static int setPermissions(@NotNull File file, int mode) throws IOException {
        if(file == null){
            throw new NullPointerException("file is not been null");
        }
        if(!file.exists()){
            throw new FileNotFoundException("文件 "+file.getAbsolutePath() + " 不存在");
        }
        if(setPermissions(file,mode,-1,-1)!=0){
            chmod(file,mode);
        }
        return  0;
    }

    private static void chmod(@NotNull File file, int mode) throws IOException {
        Process su = Runtime.getRuntime().exec("/system/bin/su");
        String cmd = String.format("chmod %d %s \nexit\n",mode,file.getAbsolutePath());
        OutputStream output = null;
        try {
            output = su.getOutputStream();
            output.write(cmd.getBytes());
            output.flush();
        }finally {
            if(output!=null) {
                output.close();
            }
        }
    }
}
