package de.codeboje.springbootbook.commentstore.restapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import de.codeboje.springbootbook.commentstore.service.CommentService;
import de.codeboje.springbootbook.model.Comment;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
@WithMockUser(username="admin")
public class WriteControllerTest {

    @Autowired
    private CommentService service;
    
    @Autowired
    private MockMvc mvc;
    
    @Test
    public void testPost() throws Exception {

        Comment model = setupDummyModel();
        
        MvcResult result = this.mvc.perform(MockMvcRequestBuilders.post("/api/comments")
                        .param("comment", model.getComment())
                        .param("pageId", model.getPageId())
                        .param("emailAddress", model.getEmailAddress())
                        .param("username", model.getUsername()).with(csrf()))
                    .andExpect(status().is(201)).andReturn();
        
        String id = result.getResponse().getContentAsString();
        Comment dbModel = service.get(id);
		assertNotNull(dbModel);
		assertEquals(model.getComment(), dbModel.getComment());
		assertEquals(model.getPageId(), dbModel.getPageId());
		assertEquals(model.getUsername(), dbModel.getUsername());
		assertEquals(model.getEmailAddress(), dbModel.getEmailAddress());

		assertNotNull(dbModel.getLastModificationDate());
		assertNotNull(dbModel.getCreationDate());
		assertFalse(model.isSpam());

    }

    private Comment setupDummyModel() {
    	Comment model = new Comment();
		model.setUsername("testuser");
		model.setId("dqe345e456rf34rw");
		model.setPageId("product0815");
		model.setEmailAddress("example@example.com");
		model.setComment("I am the comment");
		return model;
    }

    @Test
    public void testDelete() throws Exception {
    	Comment model = new Comment();
		model.setUsername("testuser");
		model.setId("dqe345e456rf34rw");
		model.setPageId("product0815");
		model.setEmailAddress("example@example.com");
		model.setComment("I am the comment");
        String id = service.put(model);
        
        this.mvc.perform(delete("/api/comment/" + id).with(csrf())).andExpect(status().isOk());
        
        assertNull(service.get(model.getId()));        
    }
    
}
