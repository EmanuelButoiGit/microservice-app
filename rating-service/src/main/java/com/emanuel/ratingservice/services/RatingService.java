package com.emanuel.ratingservice.services;

import com.emanuel.ratingservice.components.RatingConverter;
import com.emanuel.ratingservice.dtos.MediaDto;
import com.emanuel.ratingservice.dtos.RatingDto;
import com.emanuel.ratingservice.entities.RatingEntity;
import com.emanuel.ratingservice.exceptions.DataBaseException;
import com.emanuel.ratingservice.repositories.RatingRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final RatingConverter ratingConverter;

    @SneakyThrows
    public RatingDto addRating(RatingDto rating) {
        // check if the media id is valid
        try {
           new RestTemplate().getForEntity("http://localhost:8080/api/media/" + rating.getMediaId(), MediaDto.class);
        } catch (RestClientException e) {
            throw new EntityNotFoundException("Entity not found with id: " + rating.getMediaId());
        }
        // check if a rating already has been added by the user
        List<RatingDto> allRatings = getAllRatings();
        Optional<RatingDto> matchingRating = allRatings.stream()
                .filter(it -> Objects.equals(it.getMediaId(), rating.getMediaId()))
                .findFirst();
        if (matchingRating.isPresent()) {
            throw new DataBaseException("A rating already exists for the given media id: " + rating.getMediaId());
        }
        RatingEntity ratingEntity =
                new RatingEntity(null,
                        rating.getMediaId(),
                        rating.getGeneralRating(),
                        rating.getTutorRating(),
                        rating.getContentRating(),
                        rating.getContentStructureRating(),
                        rating.getPresentationRating(),
                        rating.getEngagementRating(),
                        rating.getDifficultyRating()
                );
        RatingEntity savedEntity = ratingRepository.save(ratingEntity);
        return ratingConverter.toDto(savedEntity);
    }

    @SneakyThrows
    public List<RatingDto> getAllRatings(){
        try {
            List<RatingEntity> allRatings = ratingRepository.findAll();
            return allRatings.stream()
                    .map(ratingConverter::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataBaseException("Couldn't fetch data from database: " + e.getMessage());
        }
    }

    @SneakyThrows
    public RatingDto getRatingById(Long id) {
        RatingEntity rating = new RatingEntity();
        final RatingEntity entity = rating;
        rating = ratingRepository.findById(id).orElseThrow(() -> new com.emanuel.ratingservice.exceptions.EntityNotFoundException("%s not found with id %s ", entity.getClass(), id));
        return ratingConverter.toDto(rating);
    }
}
