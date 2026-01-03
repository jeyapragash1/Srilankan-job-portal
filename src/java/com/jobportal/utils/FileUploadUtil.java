package com.jobportal.utils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * File upload utility for handling resume uploads.
 */
public class FileUploadUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadUtil.class);
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;  // 3MB
    private static final long MAX_FILE_SIZE = ConfigManager.getIntProperty("app.upload.maxFileSize", 5242880); // 5MB
    private static final long MAX_REQUEST_SIZE = MAX_FILE_SIZE * 2;

    /**
     * Uploads a file and returns the saved file path.
     *
     * @param request the HTTP request containing the file
     * @param fieldName the form field name containing the file
     * @return the path where the file was saved, or null if upload failed
     * @throws Exception if upload fails
     */
    public static String uploadFile(HttpServletRequest request, String fieldName) throws Exception {
        
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart");
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        String uploadPath = getUploadDirectory(request);
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        try {
            List<FileItem> formItems = upload.parseRequest(request);

            for (FileItem item : formItems) {
                if (!item.isFormField() && item.getFieldName().equals(fieldName)) {
                    String fileName = item.getName();
                    
                    if (fileName == null || fileName.isEmpty()) {
                        continue;
                    }

                    // Validate file extension
                    String[] allowedExtensions = ConfigManager.getProperty("app.upload.allowedExtensions", "pdf,doc,docx")
                                                             .split(",");
                    if (!ValidationUtil.isValidFileExtension(fileName, allowedExtensions)) {
                        throw new IllegalArgumentException("File type not allowed. Allowed types: " + 
                                                          String.join(", ", allowedExtensions));
                    }

                    // Generate unique filename
                    String extension = FilenameUtils.getExtension(fileName);
                    String uniqueFileName = UUID.randomUUID().toString() + "." + extension;
                    String filePath = uploadPath + File.separator + uniqueFileName;

                    File storeFile = new File(filePath);
                    item.write(storeFile);

                    logger.info("File uploaded successfully: {}", uniqueFileName);
                    return "uploads/resumes/" + uniqueFileName; // Return relative path for database
                }
            }
        } catch (Exception e) {
            logger.error("Error uploading file", e);
            throw new Exception("File upload failed: " + e.getMessage());
        }

        return null;
    }

    /**
     * Gets the upload directory path.
     */
    private static String getUploadDirectory(HttpServletRequest request) {
        String uploadDir = ConfigManager.getProperty("app.upload.directory", "uploads/resumes");
        
        // Get the real path in the web application
        String realPath = request.getServletContext().getRealPath("/");
        return realPath + File.separator + uploadDir;
    }

    /**
     * Deletes a file.
     *
     * @param filePath the path of the file to delete
     * @return true if deleted successfully
     */
    public static boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        try {
            File file = new File(filePath);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (deleted) {
                    logger.info("File deleted: {}", filePath);
                }
                return deleted;
            }
        } catch (Exception e) {
            logger.error("Error deleting file: " + filePath, e);
        }
        return false;
    }

    /**
     * Formats file size for display.
     *
     * @param size size in bytes
     * @return formatted size string
     */
    public static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int z = (63 - Long.numberOfLeadingZeros(size)) / 10;
        return String.format("%.1f %sB", (double) size / (1L << (z * 10)), " KMGTPE".charAt(z));
    }
}
