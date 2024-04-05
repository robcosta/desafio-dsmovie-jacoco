package com.devsuperior.dsmovie.services;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.dto.MovieDTO;
import com.devsuperior.dsmovie.dto.ScoreDTO;
import com.devsuperior.dsmovie.entities.MovieEntity;
import com.devsuperior.dsmovie.entities.ScoreEntity;
import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.repositories.MovieRepository;
import com.devsuperior.dsmovie.repositories.ScoreRepository;
import com.devsuperior.dsmovie.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsmovie.tests.MovieFactory;
import com.devsuperior.dsmovie.tests.ScoreFactory;

@ExtendWith(SpringExtension.class)
public class ScoreServiceTests {
	
	@InjectMocks
	private ScoreService service;
	
	@Mock
	private UserService userService;
	
	@Mock
	private MovieRepository movieRepository;
	
	@Mock
	private ScoreRepository scoreRepository;
	
	Long existingMovieId, nonExistingMovieId;
	
	private UserEntity user;	
	private MovieEntity movie;
	private ScoreEntity score;
	private ScoreDTO scoreDto;
	
	@BeforeEach
	void Setup() throws Exception {
		existingMovieId = 1L;
		nonExistingMovieId = 100L;
		movie = MovieFactory.createMovieEntity();	
		score = ScoreFactory.createScoreEntity();
		
		movie.getScores().add(score);
		
		scoreDto = ScoreFactory.createScoreDTO();
		
		Mockito.when(userService.authenticated()).thenReturn(user);
		
		Mockito.when(movieRepository.save(ArgumentMatchers.any())).thenReturn(movie);
		
		Mockito.when(scoreRepository.saveAndFlush(ArgumentMatchers.any())).thenReturn(score);
	}
	
	@Test
	public void saveScoreShouldReturnMovieDTO() {		
		Mockito.when(movieRepository.findById(existingMovieId)).thenReturn(Optional.of(movie));
		MovieDTO result = service.saveScore(scoreDto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getId(), movie.getId());		
	}
	
	@Test
	public void saveScoreShouldThrowResourceNotFoundExceptionWhenNonExistingMovieId() {
		Mockito.doThrow(ResourceNotFoundException.class).when(movieRepository).findById(nonExistingMovieId);
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.saveScore(scoreDto);
		});
	}
}
