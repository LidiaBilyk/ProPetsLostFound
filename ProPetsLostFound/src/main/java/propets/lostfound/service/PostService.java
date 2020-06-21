package propets.lostfound.service;

import java.util.List;
import java.util.Set;

import propets.lostfound.dto.PageDto;
import propets.lostfound.dto.PostDto;
import propets.lostfound.dto.UserUpdateDto;
import propets.lostfound.model.Post;

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
	Set<PostDto> getPostsForUserData(Set<String> postId);
	Set<Post> updateUserPosts(UserUpdateDto userUpdateDto);
}
