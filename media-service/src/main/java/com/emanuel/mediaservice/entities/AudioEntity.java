package com.emanuel.mediaservice.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "audio")
@NoArgsConstructor
@AllArgsConstructor
public class AudioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String fileName;
    private Date uploadDate;
    private String mimeType;
    @Lob
    private byte[] content;
    private Long size;
    // additional fields:
    private Long duration;
    private Float sampleRate;
}
