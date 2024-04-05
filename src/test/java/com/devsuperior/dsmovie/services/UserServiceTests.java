package com.devsuperior.dsmovie.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsmovie.entities.UserEntity;
import com.devsuperior.dsmovie.projections.UserDetailsProjection;
import com.devsuperior.dsmovie.repositories.UserRepository;
import com.devsuperior.dsmovie.tests.UserDetailsFactory;
import com.devsuperior.dsmovie.tests.UserFactory;
import com.devsuperior.dsmovie.utils.CustomUserUtil;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class UserServiceTests {

	@InjectMocks
	private UserService service;
	
	@Mock
	private UserRepository repository;
	
	@Mock
	private CustomUserUtil userUtil;
	
	private UserEntity user;
	private String userAdminClient, otherUser;	
	
	
	@BeforeEach
	void SetUp() throws Exception {
		userAdminClient = "maria@gmail.com";
		otherUser = "other@gmail.com";
		
		user = UserFactory.createUserEntity();
		List<UserDetailsProjection> list = UserDetailsFactory.createCustomAdminClientUser(userAdminClient);
		
		Mockito.when(repository.findByUsername(userAdminClient)).thenReturn(Optional.of(user));
		Mockito.when(repository.searchUserAndRolesByUsername(userAdminClient)).thenReturn(list);
		Mockito.doThrow(UsernameNotFoundException.class).when(repository).searchUserAndRolesByUsername("otherUser");
		
	}

	@Test
	public void authenticatedShouldReturnUserEntityWhenUserExists() {
		Mockito.when(userUtil.getLoggedUsername()).thenReturn(userAdminClient);
		
		UserEntity result = service.authenticated();
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), userAdminClient);
		Assertions.assertEquals(result.getId(), user.getId());
	}

	@Test
	public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		Mockito.doThrow(UsernameNotFoundException.class).when(userUtil).getLoggedUsername();
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.authenticated();
		});
		
	}

	@Test
	public void loadUserByUsernameShouldReturnUserDetailsWhenUserExists() {
		
		UserDetails result = service.loadUserByUsername(userAdminClient);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getUsername(), userAdminClient);
	}
	
	@Test
	public void loadUserByUsernameShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExists() {
		
		Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			service.loadUserByUsername(otherUser);
		});
	}
}
