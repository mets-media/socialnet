package socialnet.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import socialnet.api.response.CommonRs;
import socialnet.model.Person;
import socialnet.model.Storage;
import socialnet.repository.PersonRepository;
import socialnet.repository.StorageRepository;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final PersonService personService;
    private final PersonRepository personRepository;
    private final StorageRepository storageRepository;
    private final AmazonService amazonService;
    @Value("${s3.bucket}")
    private String bucket;

    @Value("${defaultPhoto}")
    private String defaultPhoto;

    public CommonRs<Storage> photoUpload(String fileType, MultipartFile file) throws IOException {

        if (file == null) { //загрузка из Yandex Object Storage
            var personId = personService.getAuthPersonId();
            var photoUrl = storageRepository.getUserPhotoUrl(personId);

            return photoUrl.map(url -> new CommonRs<>(getPhotoUrl(personId, url)))
                    .orElseGet(() -> new CommonRs<>(getPhotoUrl(personId, null)));
        }

//        if (!isImage(file))
//            return new CommonRs<>(new Storage());

        String uniqueFileName = generateUniqueFileName(file);

        Person person = personService.getAuthPerson();
        Storage storage = new Storage(
                person.getId(),
                String.format("%s/%s/%s", "https://storage.yandexcloud.net", bucket, uniqueFileName),
                file.getSize(),
                fileType,
                LocalDateTime.now()
        );
        personRepository.setPhoto(storage.getFileName(),person.getId());
        storageRepository.insertStorage(storage);

        //Загрузка фотографии в Yandex object storage
        uploadFile(file, uniqueFileName);

        return new CommonRs<>(storage);
    }

    private boolean isImage(MultipartFile file) {
        return Arrays.asList(IMAGE_PNG.getMimeType(),
                IMAGE_BMP.getMimeType(),
                IMAGE_GIF.getMimeType(),
                IMAGE_JPEG.getMimeType()).contains(file.getContentType());
    }

    private void uploadFile(MultipartFile file, String uniqueFileName) throws IOException {

        Map<String, String> metadata = new HashMap<>();

        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setUserMetadata(metadata);

        amazonService.upload(uniqueFileName, objectMetadata, file.getInputStream());
    }
    private String generateUniqueFileName(MultipartFile file) {
        UUID uuid = UUID.randomUUID();
        String fileName = file.getOriginalFilename();
        return String.format("%s/%s/%s", "users_photo", uuid, fileName);
    }

    private Storage getPhotoUrl(Long personId, String url) {
        if (url == null) url = defaultPhoto;
        return new Storage(
                personId,
                url,
                0L,
                "IMAGE",
                LocalDateTime.now()
        );
    }
}
