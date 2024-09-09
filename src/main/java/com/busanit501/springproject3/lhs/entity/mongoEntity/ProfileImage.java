package com.busanit501.springproject3.lhs.entity.mongoEntity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profile_images")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileImage {

    @Id
    private String id;

    private String fileName;
    private String contentType;
    private byte[] data;

    public ProfileImage(String fileName, String contentType, byte[] data) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

}
