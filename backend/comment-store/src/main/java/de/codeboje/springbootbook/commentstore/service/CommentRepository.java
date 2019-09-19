package de.codeboje.springbootbook.commentstore.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import de.codeboje.springbootbook.model.Comment;

public interface CommentRepository extends CrudRepository<Comment, String>{

//    @Query("select a from CommentModel a where a.pageId = ?1")
    List<Comment> findByPageId(String pageId);
    
    List<Comment> findByPageIdAndSpamIsTrue(String pageId);
    
    Page<Comment> findByPageId(String pageId, Pageable pageable);
    
    Page<Comment> findAll(Pageable pageable);
}
