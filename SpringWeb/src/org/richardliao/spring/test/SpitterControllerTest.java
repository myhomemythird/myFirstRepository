package org.richardliao.spring.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.Test;
import org.richardliao.spring.Spitter;
import org.richardliao.spring.controler.SpitterController;
import org.richardliao.spring.data.SpitterRepository;
import org.springframework.test.web.servlet.MockMvc;

public class SpitterControllerTest {

	@Test
	public void shouldShowRegistration() throws Exception {
		SpitterController controller = new SpitterController();
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/spitter/register")).andExpect(view().name("registerForm"));
	}
	
	@Test
	public void shouldProcessRegistration() throws Exception {
		SpitterRepository mockRepository = mock(SpitterRepository.class);
		Spitter unsaved = new Spitter("jbauer", "24hours", "Jack", "Bauer");
		Spitter saved = new Spitter(24L, "jbauer", "24hours", "Jack", "Bauer");
		when(mockRepository.save(unsaved)).thenReturn(saved);
		SpitterController controller = new SpitterController(mockRepository);
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(post("/spitter/register")
			   .param("firstName", "Jack")
			   .param("lastName", "Bauer")
			   .param("username", "jbauer")
			   .param("password", "24hours"))
		    .andExpect(redirectedUrl("/spitter/jbauer"));
	}
}
