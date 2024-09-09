//package com.busanit501.springproject3.lcs.Controller;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.util.EntityUtils;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//public class ImageClassifyController {
//
//    @PostMapping("/classify")
//    public ResponseEntity<Map<String, String>> classifyImage(@RequestParam("image") MultipartFile image) {
//        Map<String, String> result = new HashMap<>();
//
//        if (image.isEmpty()) {
//            result.put("error", "No file was submitted.");
//            return ResponseEntity.badRequest().body(result);
//        }
//
//        String apiUrl = "http://localhost:8000/api/classify/";
//
//        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + image.getOriginalFilename());
//            image.transferTo(convFile);
//
//            HttpPost uploadFile = new HttpPost(apiUrl);
//            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            builder.addBinaryBody("image", convFile);
//
//            HttpEntity multipart = builder.build();
//            uploadFile.setEntity(multipart);
//
//            HttpResponse response = httpClient.execute(uploadFile);
//            HttpEntity responseEntity = response.getEntity();
//            String apiResult = EntityUtils.toString(responseEntity, "UTF-8");
//
//            // Extract the predicted label
//            String predictedLabel = extractPredictedLabel(apiResult);
//            String videoUrl = getVideoUrl(predictedLabel);
//
//            // Only return the predicted label and the video URL
//            result.put("predictedLabel", predictedLabel);
//            result.put("videoUrl", videoUrl);
//
//            if (!convFile.delete()) {
//                System.err.println("Failed to delete the temporary file.");
//            }
//
//            return new ResponseEntity<>(result, HttpStatus.OK);
//        } catch (IOException e) {
//            e.printStackTrace();
//            result.put("error", "File processing error: " + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
//        }
//    }
//
//    private String extractPredictedLabel(String apiResult) {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            JsonNode rootNode = mapper.readTree(apiResult);
//            return rootNode.path("predicted_class_label").asText();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ""; // Return an empty string if there's an error
//        }
//    }
//
//    private String getVideoUrl(String predictedLabel) {
//        switch (predictedLabel) {
//            case "상리요":
//                return "https://www.youtube.com/embed/lwB0xB1whyA?t=483";
//            case "음림":
//                return "https://www.youtube.com/embed/CowQ9rSOAmI";
//            case "설지":
//                return "https://www.youtube.com/embed/LxG6_qX2SBA?t=13";
//            default:
//                return "https://www.youtube.com/embed/82W7E20T6UQ";
//        }
//    }
//}
