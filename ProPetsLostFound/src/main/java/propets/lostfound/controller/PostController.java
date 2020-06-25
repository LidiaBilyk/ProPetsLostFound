package propets.lostfound.controller;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import propets.lostfound.dto.PageDto;
import propets.lostfound.dto.PostDto;
import propets.lostfound.dto.UserUpdateDto;
import propets.lostfound.model.Post;
import propets.lostfound.service.PostService;

@RestController
@RequestMapping("/{lang}/v1")
public class PostController {
	
	@Autowired
	PostService postService;
	
	@PostMapping("/lost/{login:.*}")
	public PostDto postLost(@PathVariable String login, @RequestBody PostDto postDto) {
		return postService.postLost(login, postDto);
	}
	
	@PostMapping("/found/{login:.*}")
	public PostDto postFound(@PathVariable String login, @RequestBody PostDto postDto) {
		return postService.postFound(login, postDto);
	}

	@GetMapping("/{id:.*}")
	public PostDto getPostById(@PathVariable String id) {
		return postService.getPostById(id);
	}
	
	@PutMapping("/{id:.*}")
	public PostDto updatePost(@PathVariable String id, @RequestBody PostDto postDto) {
		return postService.updatePost(id, postDto);
	}
	
	@DeleteMapping("/{id:.*}")
	public PostDto deletePost(@PathVariable String id) {
		return postService.deletePost(id);
	}
	
	@GetMapping("/losts")
	public PageDto getLosts(@RequestParam Integer itemsOnPage, @RequestParam Integer currentPage) {
		return postService.getLosts(itemsOnPage, currentPage);
	}
	
	@GetMapping("/founds")
	public PageDto getFounds(@RequestParam Integer itemsOnPage, @RequestParam Integer currentPage) {
		return postService.getFounds(itemsOnPage, currentPage);
	}
	
	@PostMapping("/losts/filter")
	public PageDto getMatchingLosts(@RequestParam Integer itemsOnPage, @RequestParam Integer currentPage, @RequestBody PostDto postDto) {
		return postService.getMatchingLosts(itemsOnPage, currentPage, postDto);
	}
	
	@PostMapping("/founds/filter")
	public PageDto getMatchingFounds(@RequestParam Integer itemsOnPage, @RequestParam Integer currentPage, @RequestBody PostDto postDto) {
		return postService.getMatchingFounds(itemsOnPage, currentPage, postDto);
	}
	
	@GetMapping("/tagscolors")
	public List<String> getTags(@RequestParam("image_url") String imageUrl) {
		return postService.getTags(imageUrl);
	}

	@PostMapping("/userdata")
	public Set<PostDto> getPostsForUserData(@RequestBody Set<String> postId) {
		return postService.getPostsForUserData(postId);
	}
	
	@PostMapping("/userdata/{login}")
	public Set<PostDto> getPostsForUserData(@PathVariable String login) {
		return postService.getPostsForUserData(login);
	}
	
	@PutMapping("/updateuser")
	public Set<Post> updateUserPosts(@RequestBody UserUpdateDto userUpdateDto) {	
		return postService.updateUserPosts(userUpdateDto);
	}
}
