package de.codeboje.springbootbook.commentstore.restapi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.codeboje.springbootbook.commentstore.service.CommentService;
import de.codeboje.springbootbook.model.Comment;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/api")
public class ReadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadController.class);

    @Autowired
    private CommentService service;

    @Autowired
    private MeterRegistry meterRegistry;

    @RequestMapping(value = "/comments/{id}")
    public List<Comment> getComments(@PathVariable(value = "id") String pageId) throws IOException {
        LOGGER.info("get comments for pageId {}", pageId);
        meterRegistry.counter("commentstore.list_comments").increment();
        List<Comment> r = service.list(pageId);
        if (r.isEmpty()) {
            LOGGER.info("get comments for pageId {} - not found", pageId);
            throw new FileNotFoundException("/list/" + pageId);
        }

        LOGGER.info("get comments for pageId {} - done", pageId);
        return r;
    }
    
    @RequestMapping(value = "/comments/{id}/paging")
    public Page<Comment> getCommentsPaging(@PathVariable(value = "id") String pageId, Pageable pageable) throws IOException {
        return service.list(pageId, pageable);
    }

    @RequestMapping(value = "/comments/{id}/spam")
    public List<Comment> getSpamComments(@PathVariable(value = "id") String pageId) throws IOException {
        LOGGER.info("get spam comments for pageId {}", pageId);
        meterRegistry.counter("commentstore.list_comments").increment();
        List<Comment> r = service.listSpamComments(pageId);
        LOGGER.info("get spam comments for pageId {} - done", pageId);
        return r;
    }
   
    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handle404(Exception ex, Locale locale) {
        LOGGER.debug("Resource not found {}", ex.getMessage());
    }

    @RequestMapping(value = "/comments")
    public Page<Comment> listComments(
    	@PageableDefault(sort={"creationDate"}, direction= Direction.DESC) 
    	Pageable pageable
    ) throws IOException {
        LOGGER.info("list comments");
           
        Page<Comment> r = service.list(pageable);
        
        LOGGER.info("list comments - done");
        return r;
    }

}
