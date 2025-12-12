package es.davante.mi_primer_servicio.repository;

import es.davante.mi_primer_servicio.model.Article;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Optional<Article> findByTitle(String title);
}