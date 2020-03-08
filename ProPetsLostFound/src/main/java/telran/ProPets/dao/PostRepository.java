package telran.ProPets.dao;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import telran.ProPets.dto.PostDto;
import telran.ProPets.model.Post;

public interface PostRepository extends JpaRepository<Post, String> {
	
	Page<Post> findByFoundFalse(Pageable pageable);
	Page<Post> findByFoundTrue(Pageable pageable);
//	@Query("select p from Post p join p.location pl where p.found = false and (p.type = :type or p.type is null) and (pl.country = :country or pl.country is null) and (pl.city = :city or pl.city is null)")
//	Page<Post> findLostsByFilter(Pageable pageable, String type, String country, String city);
//	@Query("select p from Post p join p.location pl where p.found = true and (p.type = :type or p.type is null) and (pl.country = :country or pl.country is null) and (pl.city = :city or pl.city is null)")
//	Page<Post> findFoundsByFilter(Pageable pageable, String type, String country, String city);

}
