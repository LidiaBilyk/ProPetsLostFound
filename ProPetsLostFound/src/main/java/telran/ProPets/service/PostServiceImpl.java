package telran.ProPets.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import telran.ProPets.configuration.LostFoundConfiguration;
import telran.ProPets.dao.PostRepository;
import telran.ProPets.dto.PageDto;
import telran.ProPets.dto.PostDto;
import telran.ProPets.dto.imagga.TagResponseDto;
import telran.ProPets.dto.imagga.ColorResponseDto;
import telran.ProPets.exceptions.BadRequestException;
import telran.ProPets.exceptions.ConflictException;
import telran.ProPets.exceptions.NotFoundException;
import telran.ProPets.model.Post;


@Service
public class PostServiceImpl implements PostService {
	
	@Autowired
	PostRepository postRepository;
	@Autowired
	LostFoundConfiguration lostFoundConfiguration;

	@Override
	public PostDto postLost(String login, PostDto postDto) {		
		Post post = createNewPost(login, postDto);		
		post.setTypePost(false);
		postRepository.save(post);
		sendToActivities(post.getUserLogin(), post.getId(), HttpMethod.PUT);
		return postToPostDto(post);
	}
	
	private void sendToActivities(String userLogin, String id, HttpMethod method) {
		RestTemplate restTemplate = new RestTemplate();	
		String activityTemplate = "/activity/";
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-ServiceName", lostFoundConfiguration.getApplicationName());		
		try {
			RequestEntity<String> restRequest = new RequestEntity<>(headers, method, 			
					new URI(lostFoundConfiguration.getActivityUri().concat(userLogin).concat(activityTemplate).concat(id)));
			ResponseEntity<String>restResponse = restTemplate.exchange(restRequest, String.class);
		} catch (RestClientException e) {
			throw new ConflictException();
		} 
		catch (URISyntaxException e) {	
			e.printStackTrace();
			throw new BadRequestException();
		}		
	}

	private Post createNewPost(String login, PostDto postDto) {
		return Post.builder()		
		.datePost(LocalDateTime.now())
		.userLogin(login)
		.username(postDto.getUsername())
		.avatar(postDto.getAvatar())
		.type(postDto.getType())
		.breed(postDto.getBreed())
		.address(postDto.getAddress())
		.location(postDto.getLocation())
		.tags(postDto.getTags())
		.photos(postDto.getPhotos())		
		.build();
	}

	@Override
	public PostDto postFound(String login, PostDto postDto) {		
		Post post = createNewPost(login, postDto);	
		post.setTypePost(true);
		postRepository.save(post);
		return postToPostDto(post);
	}

	private PostDto postToPostDto(Post post) {		
		List<String> postList = post.getPhotos();
		List<String> tagList = post.getTags();
		return PostDto.builder()
				.id(post.getId())
				.typePost(post.isTypePost())
				.userLogin(post.getUserLogin())
				.username(post.getUsername())
				.avatar(post.getAvatar())
				.datePost(post.getDatePost())
				.type(post.getType())
				.breed(post.getBreed())
				.address(post.getAddress())	
				.location(post.getLocation())
				.tags(tagList)
				.photos(postList)
				.build();
	}

	@Override
	public PostDto getPostById(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());		
		return postToPostDto(post);
	}

	@Override
	public PostDto updatePost(String id, PostDto postDto) {
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());		
		if (postDto.getType() != null) {
			post.setType(postDto.getType());
		}
		if (postDto.getBreed() != null) {
			post.setBreed(postDto.getBreed());
		}
		if (postDto.getAddress() != null) {
			post.setAddress(postDto.getAddress());
		}
		if (postDto.getTags() != null) {
			post.setTags(postDto.getTags());
		}
		if (postDto.getPhotos() != null) {
			post.setPhotos(postDto.getPhotos());
		}
		postRepository.save(post);
		return postToPostDto(post);
	}

	@Override
	public PostDto deletePost(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());
		postRepository.deleteById(id);
		sendToActivities(post.getUserLogin(), id, HttpMethod.DELETE);
		return postToPostDto(post);
	}

	@Override
	public PageDto getLosts(Integer itemsOnPage, Integer currentPage) {
		Pageable pageable = PageRequest.of(currentPage, itemsOnPage);
		Page<Post> page = postRepository.findByTypePostFalse(pageable);
		return pageToPageDto(page);		
	}
	
	private PageDto pageToPageDto(Page<Post> page) {		
		return PageDto.builder()
				.itemsOnPage(page.getNumberOfElements())
				.currentPage(page.getNumber())
				.itemsTotal(page.getTotalElements())
				.posts(page.getContent().stream().map(p -> postToPostDto(p)).collect(Collectors.toList()))
				.build();
	}

	@Override
	public PageDto getFounds(Integer itemsOnPage, Integer currentPage) {
		Pageable pageable = PageRequest.of(currentPage, itemsOnPage);
		Page<Post> page = postRepository.findByTypePostTrue(pageable);
		return pageToPageDto(page);		
	}

	@Override
	public PageDto getMatchingLosts(Integer itemsOnPage, Integer currentPage, PostDto postDto) {
		Pageable pageable = PageRequest.of(currentPage, itemsOnPage);
		Post post = createPostExample(postDto);	
		post.setTypePost(false);		
		Point point = new Point(postDto.getLocation().getLongitude(), postDto.getLocation().getLatitude());
		Distance distance = new Distance(lostFoundConfiguration.getRadius(), Metrics.KILOMETERS);
		Example<Post> example = Example.of(post, createExampleMatcher());
//		Page<Post> page = postRepository.findAll(example, pageable);	
		Page<Post> page = postRepository.findByLocationNear(point, distance, example, pageable);
		return pageToPageDto(page);
	}

	private ExampleMatcher createExampleMatcher() {			
		ExampleMatcher matcher = ExampleMatcher.matchingAll()
				.withIgnorePaths( "photos", "address.building", "location.longitude", "location.latitude", "tags")
				.withIgnoreCase();
		return matcher;
	}

	@Override
	public PageDto getMatchingFounds(Integer itemsOnPage, Integer currentPage, PostDto postDto) {
		Pageable pageable = PageRequest.of(currentPage, itemsOnPage);
		Post post = createPostExample(postDto);	
		post.setTypePost(true);
//		Point point = new Point(postDto.getLocation().getLongitude(), postDto.getLocation().getLatitude());
//		Distance distance = new Distance(lostFoundConfiguration.getRadius(), Metrics.KILOMETERS);
		Example<Post> example = Example.of(post, createExampleMatcher());
		Page<Post> page = postRepository.findAll(example, pageable);
//		Page<Post> page = postRepository.findByLocationNear(point, distance, pageable);
		return pageToPageDto(page);
	}

	private Post createPostExample(PostDto postDto) {		
		return Post.builder()
				.type(postDto.getType())
				.breed(postDto.getBreed())
				.address(postDto.getAddress())
				.location(postDto.getLocation())
				.tags(postDto.getTags())
				.build();
	}

	@Override
	public List<String> getTags(String imageUrl) {
		String colorUrl = lostFoundConfiguration.getColorUrl();
		String tagUrl = lostFoundConfiguration.getTagUrl();
		String headerKey = lostFoundConfiguration.getHeaderKey();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", headerKey);		
		RestTemplate restTemplate = new RestTemplate();
//get colors		
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(colorUrl).queryParam("image_url", imageUrl);
		RequestEntity<String> request = null;
		ResponseEntity<ColorResponseDto> responseColor = null;
		try {
			request = new RequestEntity<>(headers, HttpMethod.GET, builder.build().toUri());
			responseColor = restTemplate.exchange(request, ColorResponseDto.class);
		} catch (RestClientException e) {
			throw new ConflictException();			
		}		 
		List<String> colors = responseColor.getBody().getResult().getColors().getForegroundColors().stream().map(e -> e.getParentName()).distinct().collect(Collectors.toList());
		System.out.println(colors);
//get tags		
		builder = UriComponentsBuilder.fromHttpUrl(tagUrl).queryParam("image_url", imageUrl).queryParam("threshold", 35);
		request = new RequestEntity<>(headers, HttpMethod.GET, builder.build().toUri());
		ResponseEntity<TagResponseDto>responseTag = restTemplate.exchange(request, TagResponseDto.class);
		List<String> tags = responseTag.getBody().getResult().getTags().stream().map(e -> e.getTag().getWord()).collect(Collectors.toList());
 		System.out.println(tags);
 		
 		tags.addAll(colors);
		return tags;
	}

	@Override
	public Set<PostDto> getPostsForUserData(Set<String> postId) {		
		Iterable<Post> res = postRepository.findAllById(postId);		
		Set<PostDto> result = StreamSupport.stream(res.spliterator(), false)
		.map(d -> postToPostDto(d))		
		.collect(Collectors.toSet());		
		return result;
	}
}
