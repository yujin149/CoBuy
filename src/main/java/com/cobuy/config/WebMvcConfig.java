package com.cobuy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

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
                
        // 외부 저장소의 이미지 파일 접근 설정
        // /upload/** URL 요청이 들어오면 사용자 홈 디렉토리의 cobuy 폴더에서 파일을 찾음
        // 로고 이미지 등 업로드된 파일을 서비스하기 위한 설정
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:" + System.getProperty("user.home") + "/cobuy/");
    }
}