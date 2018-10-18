package com.seu.repository;

import com.seu.domian.Comment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CommentRepository extends JpaRepository<Comment, String> {

}
