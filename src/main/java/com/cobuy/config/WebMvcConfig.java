package com.cobuy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${upload.editor.dir}")
    private String editorUploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스 경로 매핑 설정
        // /css/** URL 요청이 들어오면 classpath:/static/css/ 경로에서 파일을 찾음
        registry.addResourceHandler("/css/**")
            .addResourceLocations("classpath:/static/css/");

        // /js/** URL 요청이 들어오면 classpath:/static/js/ 경로에서 파일을 찾음
        registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/js/");

        // /images/** URL 요청이 들어오면 classpath:/static/images/ 경로에서 파일을 찾음
        registry.addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/images/");

        // 업로드된 상품 이미지 파일 접근 설정
        registry.addResourceHandler("/images/products/**")
            .addResourceLocations("file:" + uploadPath + "/products/");

        // 에디터 이미지 업로드 파일 접근 설정
        registry.addResourceHandler("/uploads/editor/**")
            .addResourceLocations("file:" + editorUploadDir + "/");
    }
}