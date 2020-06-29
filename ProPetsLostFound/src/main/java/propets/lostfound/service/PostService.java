package propets.lostfound.service;

import java.util.List;
import propets.lostfound.dto.PageDto;
import propets.lostfound.dto.PostDto;

public interface PostService {
	PostDto postLost (String login, PostDto postDto);
	PostDto postFound (String login, PostDto postDto);
	PostDto getPostById(String id);
	PostDto updatePost(String id, PostDto postDto);
	PostDto deletePost(String id);
	PageDto getLosts(Integer itemsOnPage, Integer currentPage);
	PageDto getFounds(Integer itemsOnPage, Integer currentPage);
	PageDto getMatchingLosts(Integer itemsOnPage, Integer currentPage, PostDto postDto);
	PageDto getMatchingFounds(Integer itemsOnPage, Integer currentPage, PostDto postDto);
	List<String> getTags(String imageUrl);	
}
