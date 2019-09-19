package de.codeboje.springbootbook.commentstore.restapi;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import de.codeboje.springbootbook.commentstore.service.CommentService;
import de.codeboje.springbootbook.model.Comment;
import de.codeboje.springbootbook.spamdetection.SpamDetector;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.Meter.Type;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.noop.NoopCounter;


/**
 * Test for the {@link ReadController} using a mock test approach.
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest(ReadController.class)
@AutoConfigureMockMvc
@MockBean({SpamDetector.class})
public class ReadControllerTest {

	@MockBean
	private CommentService commentService;
	
	@MockBean
	private MeterRegistry meterRegistry;

	@Autowired
	private MockMvc mvc;

	private DateTimeFormatter DTF = DateTimeFormatter.ISO_INSTANT;
	
	@Before
	public void setup() {
		when(meterRegistry.counter(anyString())).thenReturn(new NoopCounter(new Id("dummy", Tags.empty(), null, null, Type.COUNTER)));
	}

	@Test
	public void testGetComments() throws Exception {
		Comment model = setupDummyModel();
		List<Comment> mockReturn = new ArrayList<Comment>();
		mockReturn.add(model);
		
		when(this.commentService.list( anyString())).thenReturn(mockReturn);
		
		this.mvc.perform(get("/comments/" + model.getPageId()))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$[0].id", is(model.getId())))
				.andExpect(
						jsonPath("$[0].creationDate", is(DTF.format(model
								.getCreationDate()))))
				.andExpect(jsonPath("$[0].username", is(model.getUsername())))
				.andExpect(jsonPath("$[0].comment", is(model.getComment())))
				.andExpect(
						jsonPath("$[0].emailAddress",
								is(model.getEmailAddress())));
		
		verify(this.commentService, times(1)).list( anyString());
	}

	@Test
	public void testGetSpamComments() throws Exception {
		Comment model = setupDummyModel();
		List<Comment> mockReturn = new ArrayList<Comment>();
		mockReturn.add(model);
		
		when(this.commentService.list( anyString())).thenReturn(mockReturn);
		
		this.mvc.perform(get("/comments/" + model.getPageId() + "/spam"))
				.andExpect(status().is(200))
				.andExpect(jsonPath("$", hasSize(0)));
		;
	}

	@Test
	public void testGetCommentsNotFound() throws Exception {
		this.mvc.perform(get("/comments/2")).andExpect(status().is(404));
	}

	private Comment setupDummyModel() {
		Comment model = new Comment();
		model.setId("aid");
		model.setUsername("testuser");
		model.setId("dqe345e456rf34rw");
		model.setPageId("product0815");
		model.setEmailAddress("example@example.com");
		model.setComment("I am the comment");
		model.setCreationDate(Instant.now());
		return model;
	}
}
