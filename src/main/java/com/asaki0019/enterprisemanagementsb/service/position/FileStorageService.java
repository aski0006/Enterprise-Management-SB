package com.asaki0019.enterprisemanagementsb.service.position;

import com.asaki0019.enterprisemanagementsb.core.model.Result;
import com.asaki0019.enterprisemanagementsb.enums.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class FileStorageService {
    public String uploadFile(MultipartFile file, String type) {
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if (type.equals("id_card") && !List.of("jpg", "png").contains(ext)) {
            throw new IllegalArgumentException("身份证照片仅支持JPG或PNG格式");
        }
        if (type.equals("contract") && !ext.equals("pdf")) {
            throw new IllegalArgumentException("劳动合同仅支持PDF格式");
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("文件大小不能超过10MB");
        }
        // Placeholder: Implement actual storage (e.g., S3, local disk)
        return "https://storage.example.com/" + type + "/" + file.getOriginalFilename();
    }

    public Result<?> upload(MultipartFile file) {
        try {
            String type = file.getOriginalFilename().endsWith(".pdf") ? "contract" : "id_card";
            String url = uploadFile(file, type);
            return Result.success(Map.of("url", url));
        } catch (IllegalArgumentException e) {
            return Result.failure(ErrorCode.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            return Result.failure(ErrorCode.INTERNAL_SERVER_ERROR, "文件上传失败");
        }
    }
}
