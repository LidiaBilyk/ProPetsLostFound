package telran.ProPets.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import telran.ProPets.model.Location;

@Getter
@Builder
public class PostDto {
	String id;
	String userLogin;
	LocalDateTime datePost;
	String type;
//	@Singular
	List<String> tags;
//	@Singular
	List<String> photos;
	Location location;
	int radius;

}
