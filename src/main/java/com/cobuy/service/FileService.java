package com.cobuy.service;

import com.cobuy.entity.Product;
import com.cobuy.entity.ProductImage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    // 상품 이미지 업로드
    public ProductImage uploadProductImage(MultipartFile file, Product product, boolean isRepImage) throws Exception {
        if (file.isEmpty()) {
            if (isRepImage) {
                throw new IllegalArgumentException("대표 이미지는 필수입니다.");
            }
            return null;
        }

        // 원본 파일명
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // 파일 확장자 추출
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 저장할 파일명 (UUID + 확장자)
        String savedFileName = UUID.randomUUID().toString() + extension;

        // 저장할 경로 생성
        String savedPath = uploadPath + "/products/" + product.getId();
        File uploadDir = new File(savedPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 파일 저장
        file.transferTo(new File(savedPath + "/" + savedFileName));

        // ProductImage 엔티티 생성 및 반환
        ProductImage productImage = new ProductImage();
        productImage.setProduct(product);
        productImage.setImageName(savedFileName);
        productImage.setImageUrl("/images/products/" + product.getId() + "/" + savedFileName);
        productImage.setImageType(file.getContentType());
        productImage.setRepImageYn(isRepImage);

        return productImage;
    }

    // 파일 삭제
    public void deleteFile(String filePath) {
        File file = new File(uploadPath + filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}