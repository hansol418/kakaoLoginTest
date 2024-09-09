package com.busanit501.springproject3.lhs.repository.mongoRepository;



import com.busanit501.springproject3.lhs.entity.mongoEntity.ProfileImage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileImageRepository extends MongoRepository<ProfileImage, String> {
}
