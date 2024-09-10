package com.busanit501.springproject3.lcs.Controller;

import com.busanit501.springproject3.lcs.Dto.ClassificationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestController
public class ImageClassifyController {

    @PostMapping("/classify")
    public ResponseEntity<Map<String, String>> classifyImage(@RequestParam("image") MultipartFile image) {
        Map<String, String> result = new HashMap<>();

        if (image.isEmpty()) {
            result.put("error", "No file was submitted.");
            return ResponseEntity.badRequest().body(result);
        }

        String apiUrl = "http://localhost:8000/api/classify/";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + image.getOriginalFilename());
            image.transferTo(convFile);

            HttpPost uploadFile = new HttpPost(apiUrl);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("image", convFile);

            HttpEntity multipart = builder.build();
            uploadFile.setEntity(multipart);

            HttpResponse response = httpClient.execute(uploadFile);
            HttpEntity responseEntity = response.getEntity();

            log.info("API 연결확인:" + responseEntity);

            String apiResult = EntityUtils.toString(responseEntity, "UTF-8");

            log.info("API 결과: " + apiResult);

            // DTO로 변환
            ClassificationResponseDTO classificationResponseDTO = extractClassificationResponseDTO(apiResult);
            if (classificationResponseDTO == null) {
                log.error("classificationResponseDTO가 null입니다. JSON 파싱에 실패했습니다.");
                result.put("error", "DTO 변환에 실패했습니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }

            log.info("DTO 내용: " + classificationResponseDTO);

            String predictedLabel = classificationResponseDTO.getPredictedClassLabel();
            if (predictedLabel == null) {
                log.error("Predicted Label이 null입니다. JSON 응답에 문제가 있을 수 있습니다.");
                result.put("error", "Predicted Label이 null입니다.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
            log.info("Predicted Label: " + predictedLabel);

            String videoUrl = getVideoUrl(predictedLabel);

            // DTO에서 필요한 값만 반환
            result.put("predictedLabel", predictedLabel);
            result.put("videoUrl", videoUrl);

            if (!convFile.delete()) {
                System.err.println("Failed to delete the temporary file.");
            }

            log.info("응답 데이터: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            result.put("error", "File processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    private ClassificationResponseDTO extractClassificationResponseDTO(String apiResult) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(apiResult, ClassificationResponseDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 예외 발생 시 null 반환
        }
    }

    private String getVideoUrl(String predictedLabel) {
        switch (predictedLabel) {
            case "상리요":
                return "https://www.youtube.com/embed/lwB0xB1whyA?t=483";
            case "음림":
                return "https://www.youtube.com/embed/CowQ9rSOAmI";
            case "설지":
                return "https://www.youtube.com/embed/LxG6_qX2SBA?t=13";
            default:
                return "https://www.youtube.com/embed/82W7E20T6UQ";
        }
    }
}
