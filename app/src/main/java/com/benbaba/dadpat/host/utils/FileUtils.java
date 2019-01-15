package com.benbaba.dadpat.host.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Administrator on 2018/3/8.
 */
public class FileUtils {


    /**
     * 解压assets的zip压缩文件到指定目录
     *
     * @throws IOException
     */
    public static void unZip(Context context, String assetName,
                             String outputDirectory, boolean isReWrite) throws IOException {
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        if (getFileSize(outputDirectory) > 0) {
            return;
        }
        //删除原文件
//        deleteFile(new File(outputDirectory, "web"));
        // 打开压缩文件
        InputStream inputStream = context.getAssets().open(assetName);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        byte[] buffer = new byte[1024 * 1024];
        int count;
        while (zipEntry != null) {
            Log.i("TAG", zipEntry.getName());
            if (zipEntry.isDirectory()) {
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者是文件不存在
                if (isReWrite || !file.exists()) {
                    file.mkdir();
                }
            } else {
                // 如果是文件
                file = new File(outputDirectory + File.separator + zipEntry.getName());
                // 文件需要覆盖或者文件不存在，则解压文件
                if (isReWrite || !file.exists()) {
                    file.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    while ((count = zipInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, count);
                    }
                    fileOutputStream.close();
                }
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
        }
        zipInputStream.close();
    }

    /**
     * 删除文件或者文件夹
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file != null && file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            L.i("文件不存在！" + "\n");
        }
    }

    public static void deleteDirFiles(File file) {
        if (file != null && file.exists() && file.isDirectory()) { // 判断文件是否存在
            File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
            for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
            }
        }
    }

    // 存储文件
    public static boolean saveFile(String filePath, String fileName, InputStream inputStream) {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File saveFile = new File(filePath, fileName);
        if (saveFile.exists()) {
            saveFile.delete();
        }
        byte[] data = new byte[1024];
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filePath, fileName));
            int len;
            while ((len = inputStream.read(data)) != -1) {
                fos.write(data, 0, len);
            }
            fos.flush();
            L.i("文件存储成功");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    /**
     * 解压缩文件
     *
     * @param zipFilePath   压缩文件的路径
     * @param outputDirPath 解压输出目标文件的路径
     */
    public static void unZip(String zipFilePath, String outputDirPath) {
        File zipFile = new File(zipFilePath);
        if (!zipFile.exists())
            return;
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        FileInputStream is = null;
        ZipInputStream zipIs = null;
        File file;
        try {
            is = new FileInputStream(zipFile);
            zipIs = new ZipInputStream(is);
            ZipEntry zipEntry = zipIs.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.isDirectory()) {
                    file = new File(outputDirPath + File.separator + zipEntry.getName());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                } else {
                    file = new File(outputDirPath + File.separator + zipEntry.getName());
                    if (!file.exists()) {
                        file.createNewFile();
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        while ((count = zipIs.read(buffer)) > 0) {
                            fileOutputStream.write(buffer, 0, count);
                        }
                        fileOutputStream.close();
                    }
                }
                zipEntry = zipIs.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (zipIs != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否存在此文件
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 是否存在
     */
    public static boolean isHaveFile(String filePath, String fileName) {
        File file = new File(filePath, fileName);
        return file.exists();
    }

    /**
     * 获取文件得大小
     *
     * @param filePath
     * @return
     */
    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        long size = 0L;
        if (file.exists() && !file.isDirectory()) {
            size = getFileSize(file);
        }
        return size;
    }

    /**
     * 获取文件得尺寸
     *
     * @param file
     * @return
     */
    private static long getFileSize(File file) {
        long size = 0;
        FileInputStream fis = null;
        if (file.exists()) {
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return size;
    }

    /**
     * 按行读取txt	 * 	 * @param is	 * @return	 * @throws Exception
     */
    public static String readText(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /**
     * 将文件大小转换成字节
     */
    public static String formatFileSize(long fSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fSize == 0) {
            return wrongSize;
        }
        if (fSize < 1024) {
            fileSizeString = df.format((double) fSize) + "B";
        } else if (fSize < 1048576) {
            fileSizeString = df.format((double) fSize / 1024) + "KB";
        } else if (fSize < 1073741824) {
            fileSizeString = df.format((double) fSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fSize / 1073741824) + "GB";
        }
        return fileSizeString;

    }



}
