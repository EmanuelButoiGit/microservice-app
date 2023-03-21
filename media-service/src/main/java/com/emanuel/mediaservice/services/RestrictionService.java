package com.emanuel.mediaservice.services;

import com.emanuel.mediaservice.exceptions.WrongExtensionException;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

@Service
public class RestrictionService {

    @SneakyThrows
    public String validateExtensionAndMimeType(String[] fileFormat, MultipartFile file){
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        String[] parts = fileName.split("\\.");
        String extension = parts[parts.length - 1];
        if (Arrays.stream(fileFormat).noneMatch(ext -> ext.equals(extension))) {
            throw new WrongExtensionException(extension);
        }
        Tika tika = new Tika();
        try (InputStream inputStream = file.getInputStream()) {
            String mimeType = tika.detect(inputStream);
            String contentType = file.getContentType();
            // special cases:
            if("video/x-msvideo".equals(mimeType) && "video/avi".equals(contentType) && "avi".equals(extension)){
                return  extension;
            } else if ("application/x-matroska".equals(mimeType) && "video/webm".equals(contentType) && "webm".equals(extension)) {
                return  extension;
            } else if ("audio/vnd.wave".equals(mimeType) && "audio/wav".equals(contentType) && "wav".equals(extension)) {
                return extension;
            } else if ("application/x-tika-ooxml".equals(mimeType) && "application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType) && "docx".equals(extension)) {
                return extension;
            } else if (!Objects.equals(mimeType, contentType)){
                throw new WrongExtensionException(contentType, fileName, extension);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return extension;
    }
}
