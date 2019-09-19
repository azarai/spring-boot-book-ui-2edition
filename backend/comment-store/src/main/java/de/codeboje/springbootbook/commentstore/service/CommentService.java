package de.codeboje.springbootbook.commentstore.service;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import de.codeboje.springbootbook.model.Comment;

public interface CommentService {

    /**
     * Saves the comment
     * @param model
     * @return returns the id of the model
     * @throws IOException when there are problems with the DB
     */
    String put(Comment model) throws IOException;

    List<Comment> list(String pageId) throws IOException;
    
    Page<Comment> list(String pageId, Pageable pageable) throws IOException;

    Comment get(String id);
    
    List<Comment> listSpamComments(String pageId) throws IOException;
    
    void delete(String id);
    
    Page<Comment> list(Pageable pageable);

}