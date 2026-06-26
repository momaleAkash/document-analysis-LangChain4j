package com.document.ai.document.repository;

import com.document.ai.document.model.Document;
import com.document.ai.document.model.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, String> {

    List<Document> findByClassifiedType(DocumentType type);

    @Query("SELECT d.classifiedType, COUNT(d) FROM Document d GROUP BY d.classifiedType")
    List<Object[]> countByType();

    List<Document> findByFileNameContainingIgnoreCase(String fileName);
}