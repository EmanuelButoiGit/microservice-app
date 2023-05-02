package com.emanuel.mediaservice.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Entity
@Table(name = "image")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageEntity extends MediaEntity {
    @NotNull
    @Positive
    private Integer width;
    @NotNull
    @Positive
    private Integer height;
    @NotNull
    @Positive
    private Integer resolutionQuality;

    public ImageEntity(MediaEntity mediaEntity, Integer width, Integer height, Integer resolutionQuality) {
        super(mediaEntity.getId(), mediaEntity.getTitle(), mediaEntity.getDescription(),
                mediaEntity.getFileName(), mediaEntity.getExtension(), mediaEntity.getUploadDate(),
                mediaEntity.getMimeType(), mediaEntity.getContent(), mediaEntity.getSize());
        this.width = width;
        this.height = height;
        this.resolutionQuality = resolutionQuality;
    }
}
