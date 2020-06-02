package telran.ProPets.dao;


import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

import telran.ProPets.model.Post;


public interface PostRepository extends MongoRepository<Post, String> {
	
	Page<Post> findByTypePostTrue(Pageable pageable);
	Page<Post> findByTypePostFalse(Pageable pageable);
	Page<Post> findByLocationNear(Point point, Distance distance, Example<Post> example, Pageable pageable);
//	@Query("select p from Post p join p.location pl where p.found = false and (p.type = :type or p.type is null) and (pl.country = :country or pl.country is null) and (pl.city = :city or pl.city is null)")
//	Page<Post> findLostsByFilter(Pageable pageable, String type, String country, String city);
//	@Query("select p from Post p join p.location pl where p.found = true and (p.type = :type or p.type is null) and (pl.country = :country or pl.country is null) and (pl.city = :city or pl.city is null)")
//	Page<Post> findFoundsByFilter(Pageable pageable, String type, String country, String city);

}
