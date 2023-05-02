package com.emanuel.starterlibrary.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VideoDto extends ImageDto {
    @NotNull
    @Positive
    private Long duration;
    @NotNull
    @Positive
    private Double aspectRatio;
    @NotNull
    @Positive
    private Double fps;

    public VideoDto(ImageDto imageDto, Long duration, Double aspectRatio, Double fps) {
        super(imageDto.getId(), imageDto.getTitle(), imageDto.getDescription(),
                imageDto.getFileName(), imageDto.getExtension(), imageDto.getUploadDate(),
                imageDto.getMimeType(), imageDto.getContent(), imageDto.getSize(),
                imageDto.getWidth(), imageDto.getHeight(), imageDto.getResolutionQuality());
        this.duration = duration;
        this.aspectRatio = aspectRatio;
        this.fps = fps;
    }
}