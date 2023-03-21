package com.emanuel.mediaservice.services;

import com.emanuel.mediaservice.classes.FileFormats;
import com.emanuel.mediaservice.components.DocumentConverter;
import com.emanuel.mediaservice.components.MediaConverter;
import com.emanuel.mediaservice.dtos.DocumentDto;
import com.emanuel.mediaservice.dtos.MediaDto;
import com.emanuel.mediaservice.entities.DocumentEntity;
import com.emanuel.mediaservice.entities.MediaEntity;
import com.emanuel.mediaservice.exceptions.DataBaseException;
import com.emanuel.mediaservice.exceptions.DocumentException;
import com.emanuel.mediaservice.exceptions.EntityNotFoundException;
import com.emanuel.mediaservice.repositories.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class DocumentService {

    private final RestrictionService restrictionService;
    private final MediaService mediaService;
    private final MediaConverter mediaConverter;
    private final DocumentRepository documentRepository;
    private final DocumentConverter documentConverter;

    @SneakyThrows
    public DocumentDto uploadDocument(MultipartFile file, String title, String description) {
        String fileName = Objects.requireNonNull(file.getOriginalFilename());
        String extension = restrictionService.validateExtensionAndMimeType(FileFormats.getDOCUMENT_FORMATS(), fileName, file.getContentType());
        MediaDto mediaFields = mediaService.getMediaFields(file, title, description);
        int numberOfPages = 0;

        if("docx".equals(extension)) {
            try (InputStream inputStream = file.getInputStream()) {
                XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(IOUtils.toByteArray(inputStream)));
                numberOfPages = document.getProperties().getExtendedProperties().getUnderlyingProperties().getPages();
                document.close();
            } catch (IOException e) {
                throw new DocumentException("Can't process the document: " + e.getMessage());
            }
        } else if ("pdf".equals(extension)){
            try (InputStream inputStream = file.getInputStream()) {
                PDDocument document = PDDocument.load(inputStream);
                numberOfPages = document.getNumberOfPages();
                document.close();
            } catch (IOException e) {
                throw new DocumentException("Can't close the document: " + e.getMessage());
            }
        }

        MediaEntity entity = mediaConverter.toEntity(mediaFields);
        DocumentEntity documentEntity = new DocumentEntity(entity, numberOfPages);
        DocumentEntity savedEntity = documentRepository.save(documentEntity);
        return documentConverter.toDto(savedEntity);
    }

    @SneakyThrows
    public List<DocumentDto> getAllDocuments() {
        try {
            List<DocumentEntity> allDocuments = documentRepository.findAll();
            return allDocuments.stream()
                    .map(documentConverter::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataBaseException("Couldn't fetch data from database: " + e.getMessage());
        }
    }

    @SneakyThrows
    public DocumentDto getDocumentById(Long id) {
        DocumentEntity document = new DocumentEntity();
        final DocumentEntity entity = document;
        document = documentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("%s not found with id %s ", entity.getClass(), id));
        return documentConverter.toDto(document);
    }

    public DocumentDto deleteDocument(Long id) {
        DocumentDto document = getDocumentById(id);
        documentRepository.delete(documentConverter.toEntity(document));
        return document;
    }

    @SneakyThrows
    public DocumentDto updateDocument(Long id, DocumentDto dto) {
        MediaDto media = mediaService.updateMediaFields(id, dto);
        DocumentDto updatedDocument = new DocumentDto(media, dto.getNumberOfPages());
        DocumentEntity documentEntity = documentRepository.save(documentConverter.toEntity(updatedDocument));
        return documentConverter.toDto(documentEntity);
    }

    public void deleteAllDocuments() {
        documentRepository.deleteAll();
    }
}
