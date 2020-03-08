package telran.ProPets.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageDto {

	Integer itemsOnPage;
	Integer currentPage;
	Long itemsTotal;
	List<PostDto> posts;

}
