package org.u8212u9732.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * 文件上传工具类
 *
 * @author wangn
 */
public class XiaoShuLuFileUtils {
    private static final Logger logger = Logger.getLogger(XiaoShuLuFileUtils.class);
    /**
     * 文件MIME类型
     */
    public static final String FILE_CONTENT_TYPE = "fileContentType";
    /**
     * 输出文件名
     */
    public static final String OUT_PUT_FILE_NAME = "outputFileName";
    /**
     * 构造工具类
     */
    private static final ServletFileUpload SERVLET_FILE_UPLOAD = new ServletFileUpload(new DiskFileItemFactory());
    /**
     * 单个上传文件的最大允许大小，
     */
    private long fileSizeMax = 30 * 1024 * 1024;
    /**
     * 完整请求的最大允许大小
     */
    private long sizeMax = 80 * 1024 * 1024;
    /**
     * 指定读取标题时要使用的字符编码
     */
    private String headerEncoding = "UTF-8";
    /**
     * 输出目录
     */
    private String outDir;
    /**
     * 自定义文件名
     */
    private String customFileName;
    /**
     * 文件名类型
     */
    private int fileNameType = 0;

    /**
     * 默认文件上传
     *
     * @param request HttpServletRequest
     */
    public Map<String, String> startUpload(HttpServletRequest request) {
        try {
            logger.info("开始上传文件");
            checkNotNull(request);
            checkState(ServletFileUpload.isMultipartContent(request));
            Map<String, String> formField = new HashMap<>(16);
            logger.info("上传文件最大大小:" + fileSizeMax + " 表单最大大小:" + sizeMax + " 编码:" + headerEncoding);
            SERVLET_FILE_UPLOAD.setFileSizeMax(fileSizeMax);
            SERVLET_FILE_UPLOAD.setSizeMax(sizeMax);
            SERVLET_FILE_UPLOAD.setHeaderEncoding(headerEncoding);
            List<FileItem> list = SERVLET_FILE_UPLOAD.parseRequest(request);
            logger.info("表单大小:" + list.size());
            for (FileItem item : list) {
                if (item.isFormField()) {
                    formField.put(item.getFieldName(), item.getString());
                } else {
                    formField.put(item.getFieldName(), item.getName());
                    formField.put("fileContentType", item.getContentType());
                    String name = item.getName();
                    logger.info("原始文件名:" + name);
                    String outputFileName = getFileName(name);
                    File file = new File(outDir, outputFileName);
                    formField.put("outputFileName", outputFileName);
                    logger.info("输出文件名:" + outputFileName);
                    item.write(file);
                    item.delete();
                }
            }
            logger.info("结束上传文件");
            return formField;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据设置的命名类型来设置
     *
     * @param name 原始文件名
     * @return 新文件名
     */
    private String getFileName(String name) {
        if (fileNameType == 0) {
            logger.info("使用原始文件名命名文件");
            return name;
        } else if (fileNameType == 1) {
            logger.info("使用UUID命名文件");
            return UUID.randomUUID().toString() + "." + name.substring(name.lastIndexOf(".") + 1);
        } else {
            logger.info("使用自定义命名文件:" + customFileName);
            return customFileName + "." + name.substring(name.lastIndexOf(".") + 1);
        }
    }

    /**
     * 设置文件名为UUID
     *
     * @return XiaoShuLuFileUtils
     */
    public XiaoShuLuFileUtils setUUIDFileName() {
        this.fileNameType = 1;
        return this;
    }

    public XiaoShuLuFileUtils setFileName(String fileName) {
        checkNotNull(fileName);
        this.customFileName = fileName;
        this.fileNameType = 2;
        return this;
    }


    /**
     * 设置输出文件夹路径
     *
     * @param dir 文件夹路径
     * @return XiaoShuLuFileUtils
     */
    public XiaoShuLuFileUtils setOutPutDir(String dir) {
        this.outDir = dir;
        return this;
    }

    /**
     * 指定读取标题时要使用的字符编码
     *
     * @param encoding 字符编码
     * @return XiaoShuLuFileUtils
     */
    public XiaoShuLuFileUtils setHeaderEncoding(String encoding) {
        checkNotNull(encoding);
        this.headerEncoding = encoding;
        return this;
    }

    /**
     * 设置单个上传文件的最大允许大小，
     *
     * @return XiaoShuLuFileUtils
     */
    public XiaoShuLuFileUtils setFileSizeMax(long size) {
        this.fileSizeMax = sizeMax;
        return this;
    }

    /**
     * 设置完整请求的最大允许大小
     *
     * @return XiaoShuLuFileUtils
     */
    public XiaoShuLuFileUtils setFormSizeMax(long size) {
        this.sizeMax = size;
        return this;
    }
}
