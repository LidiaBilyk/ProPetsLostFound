package propets.lostfound.dto;

import java.util.Set;
import lombok.Getter;


@Getter
public class UserUpdateDto {	
	String username;
	String avatar;
	Set<String> postId;
}
