package de.codeboje.springbootbook.commentstore.restapi;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import de.codeboje.springbootbook.commentstore.service.CommentService;
import de.codeboje.springbootbook.model.Comment;
import io.micrometer.core.instrument.MeterRegistry;


@Controller
public class WriteController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WriteController.class);

	@Autowired
	private CommentService service;

	@Autowired
	private MeterRegistry meterRegistry;

	@RequestMapping(value = "/comments", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.CREATED)
	public @ResponseBody String create(@ModelAttribute Comment model) throws IOException {

		meterRegistry.counter("commentstore.post").increment();;

		LOGGER.info("form post started");

		String id = service.put(model);
		
		LOGGER.info("form post done");
		
		return id;
	}
	
	@RequestMapping(value = "/comment/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(value = HttpStatus.OK)
	public void delete(@PathVariable(value = "id") String id,
			HttpServletResponse response) throws IOException {
		service.delete(id);
	}
	
    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handle404(Exception ex, Locale locale) {
        LOGGER.debug("Resource not found {}", ex.getMessage());
    }
}
