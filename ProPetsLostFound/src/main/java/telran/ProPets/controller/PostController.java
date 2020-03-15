package telran.ProPets.controller;

import java.security.Principal;
import java.util.List;

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

import telran.ProPets.dto.PageDto;
import telran.ProPets.dto.PostDto;
import telran.ProPets.service.PostService;

@RestController
@RequestMapping("/{lang}/v1")
public class PostController {
	
	@Autowired
	PostService postService;
	
	@PostMapping("/lost/{login:.*}")
	public PostDto postLost(Principal principal, @PathVariable String login, @RequestBody PostDto postDto) {
		return postService.postLost(principal, login, postDto);
	}
	
	@PostMapping("/found/{login:.*}")
	public PostDto postFound(Principal principal, @PathVariable String login, @RequestBody PostDto postDto) {
		return postService.postFound(principal, login, postDto);
	}

	@GetMapping("/{id:.*}")
	public PostDto getPostById(@PathVariable String id) {
		return postService.getPostById(id);
	}
	
	@PutMapping("/{id:.*}")
	public PostDto updatePost(Principal principal, @PathVariable String id, @RequestBody PostDto postDto) {
		return postService.updatePost(principal, id, postDto);
	}
	
	@DeleteMapping("/{id:.*}")
	public PostDto deletePost(Principal principal, @PathVariable String id) {
		return postService.deletePost(principal, id);
	}
	
	@GetMapping("/losts")
	public PageDto getLosts(@RequestParam Integer pageSize, @RequestParam Integer pageNumber) {
		return postService.getLosts(pageSize, pageNumber);
	}
	
	@GetMapping("/founds")
	public PageDto getFounds(@RequestParam Integer pageSize, @RequestParam Integer pageNumber) {
		return postService.getFounds(pageSize, pageNumber);
	}
	
	@PostMapping("/losts/filter")
	public PageDto getMatchingLosts(@RequestParam Integer pageSize, @RequestParam Integer pageNumber, @RequestBody PostDto postDto) {
		return postService.getMatchingLosts(pageSize, pageNumber, postDto);
	}
	
	@PostMapping("/founds/filter")
	public PageDto getMatchingFounds(@RequestParam Integer pageSize, @RequestParam Integer pageNumber, @RequestBody PostDto postDto) {
		return postService.getMatchingFounds(pageSize, pageNumber, postDto);
	}
	
	@GetMapping("tagscolors")
	public List<String> getTags(@RequestParam("image_url") String imageUrl) {
		return postService.getTags(imageUrl);
	}
	
}
