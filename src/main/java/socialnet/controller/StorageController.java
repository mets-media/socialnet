package socialnet.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import socialnet.api.response.CommonRs;
import socialnet.model.Storage;
import socialnet.service.StorageService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
@Tag(name = "storage-controller", description = "Work with account image file")
public class StorageController {

    private final StorageService storageService;

    @PostMapping(
            path = "",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "storage")
    public ResponseEntity<CommonRs<Storage>> addFile(@RequestParam String type,
                                                     @RequestBody MultipartFile file)
            throws IOException {
        return ResponseEntity.ok(storageService.photoUpload(type, file));
    }
}
