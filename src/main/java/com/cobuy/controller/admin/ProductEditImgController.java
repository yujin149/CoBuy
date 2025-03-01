package com.cobuy.controller.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class ProductEditImgController {

    @Value("${upload.editor.dir}")
    private String uploadDir;

    @PostMapping("/upload/editor/image")
    public ResponseEntity<?> uploadEditorImage(@RequestParam("file") MultipartFile file) {
        try {
            // 원본 파일명에서 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // UUID를 사용하여 고유한 파일명 생성
            String newFilename = UUID.randomUUID().toString() + extension;

            // 업로드 디렉토리가 없으면 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            Path path = Paths.get(uploadDir + File.separator + newFilename);
            Files.copy(file.getInputStream(), path);

            // TinyMCE에서 요구하는 응답 형식
            Map<String, String> response = new HashMap<>();
            response.put("location", "/uploads/editor/" + newFilename);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "파일 업로드에 실패했습니다.");
            return ResponseEntity.badRequest().body(error);
        }
    }
}