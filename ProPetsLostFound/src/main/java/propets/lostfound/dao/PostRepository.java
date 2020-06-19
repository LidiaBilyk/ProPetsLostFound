package propets.lostfound.dao;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import propets.lostfound.model.Post;


public interface PostRepository extends MongoRepository<Post, String> {
	
	Page<Post> findByTypePostTrue(Pageable pageable);
	Page<Post> findByTypePostFalse(Pageable pageable);
	Page<Post> findByLocationNear(Point point, Distance distance, Example<Post> example, Pageable pageable);
//	@Query("{$and:[\r\n" + 
//			"{$or:[{\"type\": { \"$regex\" : \"\\\\Q?0\\\\E\", \"$options\" : \"i\"}}, {\"type\": null}]}, \r\n" + 
//			"{$or:[{\"sex\": { \"$regex\" : \"\\\\Q?1\\\\E\", \"$options\" : \"i\"}}, {\"sex\": null}]},\r\n" + 
//			"{$or:[{\"breed\": { \"$regex\" : \"\\\\Q?2\\\\E\", \"$options\" : \"i\"}}, {\"breed\": null}]},\r\n" + 
//			"{$or:[{\"tags\":{\"$all\":?3}}, {\"tags\": null}]},\r\n" + 
//			"{\"location\" : { \"$nearSphere\" : { \"x\" : ?4, \"y\" : ?5}, \"$maxDistance\" : ?6}},\r\n" + 
//			"{\"typePost\": ?7}\r\n" + 
//			"]}")
//	Page<Post> findAllInfo(String type, String sex, String breed, List<String> tags, double longitude, double latitude, Double distance, boolean typePost, Pageable pageable);
}
