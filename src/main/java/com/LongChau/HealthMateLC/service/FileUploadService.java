package com.LongChau.HealthMateLC.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Path uploadPath = Paths.get("uploads");

    public FileUploadService() {
        try {
            // Tạo thư mục uploads nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục uploads", e);
        }
    }

    public String uploadImage(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // Kiểm tra định dạng file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh");
        }

        // Tạo tên file unique
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(filename);

        // Lưu file
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về đường link để truy cập
        return "/uploads/" + filename;
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
            String filename = imageUrl.substring("/uploads/".length());
            Path filePath = uploadPath.resolve(filename);
            
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log lỗi nhưng không throw exception
                System.err.println("Không thể xóa file: " + filename);
            }
        }
    }

    public String convertBase64ToFile(String base64Data, String originalFilename) throws IOException {
        if (base64Data == null || base64Data.isEmpty()) {
            return null;
        }

        // Xóa prefix "data:image/...;base64," nếu có
        String base64Image = base64Data;
        if (base64Data.contains(",")) {
            base64Image = base64Data.substring(base64Data.indexOf(",") + 1);
        }

        // Decode base64
        byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);

        // Tạo tên file unique
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        } else {
            // Mặc định là .jpg nếu không có extension
            fileExtension = ".jpg";
        }
        
        String filename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(filename);

        // Lưu file
        Files.write(filePath, imageBytes);

        // Trả về đường link
        return "/uploads/" + filename;
    }
} 