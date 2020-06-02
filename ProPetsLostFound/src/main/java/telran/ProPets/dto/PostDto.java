package telran.ProPets.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import telran.ProPets.model.Address;
import telran.ProPets.model.Location;

@Getter
@Builder
public class PostDto {
	String id;
	boolean typePost;
	String userLogin;
    String username; 
    String avatar;
	LocalDateTime datePost;
	String type;
	@Singular
	List<String> tags;
	@Singular
	List<String> photos;
	Address address;
    Location location;
}
