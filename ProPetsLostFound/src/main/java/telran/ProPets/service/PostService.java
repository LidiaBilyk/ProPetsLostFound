package telran.ProPets.service;

import java.security.Principal;
import java.util.List;

import telran.ProPets.dto.PageDto;
import telran.ProPets.dto.PostDto;


public interface PostService {
	PostDto postLost (Principal principal, String login, PostDto postDto);
	PostDto postFound (Principal principal, String login, PostDto postDto);
	PostDto getPostById(String id);
	PostDto updatePost(Principal principal, String id, PostDto postDto);
	PostDto deletePost(Principal principal, String id);
	PageDto getLosts(Integer itemsOnPage, Integer currentPage);
	PageDto getFounds(Integer itemsOnPage, Integer currentPage);
	PageDto getMatchingLosts(Integer itemsOnPage, Integer currentPage, PostDto postDto);
	PageDto getMatchingFounds(Integer itemsOnPage, Integer currentPage, PostDto postDto);
	List<String> getTags(String imageUrl);

}
