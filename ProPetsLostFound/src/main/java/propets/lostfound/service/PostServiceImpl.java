package propets.lostfound.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import propets.lostfound.configuration.LostFoundConfiguration;
import propets.lostfound.dao.PostRepository;
import propets.lostfound.dto.PageDto;
import propets.lostfound.dto.PostDto;
import propets.lostfound.dto.UserUpdateDto;
import propets.lostfound.dto.imagga.ColorResponseDto;
import propets.lostfound.dto.imagga.TagResponseDto;
import propets.lostfound.exceptions.BadRequestException;
import propets.lostfound.exceptions.ConflictException;
import propets.lostfound.exceptions.NotFoundException;
import propets.lostfound.model.Post;


@Service
public class PostServiceImpl implements PostService {
	
	private static final String MARK = "DELETE";
	
	@Autowired
	PostRepository postRepository;
	@Autowired
	LostFoundConfiguration lostFoundConfiguration;
	@Autowired
	MongoTemplate mongoTemplate;

	@Override
	public PostDto postLost(String login, PostDto postDto) {		
		Post post = createNewPost(login, postDto);		
		post.setTypePost(false);
		postRepository.save(post);
		sendToAsyncSearch(post);
		return postToPostDto(post);
	}
	
	private void sendToAsyncSearch(Post post) {
		RestTemplate restTemplate = new RestTemplate();		
		try {
			RequestEntity<PostDto> requestEntity = new RequestEntity<PostDto>(postToPostDto(post), HttpMethod.POST, 
					new URI(lostFoundConfiguration.getAsyncSearchUri()));
			ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
		} catch (RestClientException e) {
			throw new ConflictException();
		} catch (URISyntaxException e) {
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
		.sex(postDto.getSex())
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
		sendToAsyncSearch(post);
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
				.sex(post.getSex())
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
		if (postDto.getSex()!= null) {
			post.setSex(postDto.getSex());
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
		sendToAsyncSearch(post);
		return postToPostDto(post);
	}

	@Override
	public PostDto deletePost(String id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new NotFoundException());
		postRepository.deleteById(id);
		post.setId(MARK + post.getId());
		System.out.println(post.getId());
		sendToAsyncSearch(post);
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
		postDto.setTypePost(false);	
		return queryBuilder(itemsOnPage, currentPage, postDto);
	}

	private PageDto queryBuilder(Integer itemsOnPage, Integer currentPage, PostDto postDto) { 
		Pageable pageable = PageRequest.of(currentPage, itemsOnPage);		
		Query dynamicQuery = new Query();
		Criteria typePostCriteria = Criteria.where("typePost").is(postDto.isTypePost());
		dynamicQuery.addCriteria(typePostCriteria);
		if (postDto.getType() != null) {						
//			Criteria orType = new Criteria().orOperator(typeCriteria,Criteria.where("type").is(null));    =====> doesn't need, not null in DB
			dynamicQuery.addCriteria(Criteria.where("type").regex("\\Q" + postDto.getType() + "\\E", "i"));
		}
		if (postDto.getSex() != null) {		    
			dynamicQuery.addCriteria(Criteria.where("sex").in(postDto.getSex(), null));
		}
		if (postDto.getBreed() != null) {
			dynamicQuery.addCriteria(Criteria.where("breed").in(Pattern.compile("\\Q" + postDto.getBreed() + "\\E", Pattern.CASE_INSENSITIVE), null));
		}
		if (postDto.getTags() != null) {
			Set<Pattern> tagPattern = postDto.getTags().stream().map(t -> Pattern.compile("\\Q" + t + "\\E", Pattern.CASE_INSENSITIVE)).collect(Collectors.toSet());
			Criteria tagsCriteria = Criteria.where("tags").all(tagPattern);
			dynamicQuery.addCriteria(tagsCriteria);
		}
		if (postDto.getLocation() != null) {
			Point point = new Point(postDto.getLocation().getLongitude(), postDto.getLocation().getLatitude());
			Criteria locationCriteria = Criteria.where("location").nearSphere(new Point(point)).maxDistance(lostFoundConfiguration.getRadius()/lostFoundConfiguration.getDistanceMultiplier());
			dynamicQuery.addCriteria(locationCriteria);
//			NearQuery nearQuery = NearQuery.near(postDto.getLocation().getLongitude(), postDto.getLocation().getLatitude())
//					.maxDistance(lostFoundConfiguration.getRadius(), Metrics.KILOMETERS).query(dynamicQuery).with(pageable);
//			GeoResults<Post> geoRes = mongoTemplate.geoNear(nearQuery, Post.class);
//			List<Post> res = StreamSupport.stream(geoRes.spliterator(), false).map(g -> g.getContent()).collect(Collectors.toList());
//			return PageableExecutionUtils.getPage(res, pageable, () -> mongoTemplate.count(nearQuery, Post.class));                    // pageable doesn't wooork((((
		}		
		List<Post> result = mongoTemplate.find(dynamicQuery.with(pageable), Post.class);
		return pageToPageDto(PageableExecutionUtils.getPage(result, pageable, () -> mongoTemplate.count(Query.of(dynamicQuery).limit(-1).skip(-1), Post.class)));
	}

	@Override
	public PageDto getMatchingFounds(Integer itemsOnPage, Integer currentPage, PostDto postDto) {		
		postDto.setTypePost(true);
		return queryBuilder(itemsOnPage, currentPage, postDto);
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
		return StreamSupport.stream(postRepository.findAllById(postId).spliterator(), false)
		.map(d -> postToPostDto(d))		
		.collect(Collectors.toSet());		 
	}
	

	@Override
	public Set<PostDto> getPostsForUserData(String login) {		
		return postRepository.findByUserLogin(login).stream().map(p -> postToPostDto(p)).collect(Collectors.toSet());
	}

	@Override
	public Set<Post> updateUserPosts(UserUpdateDto userUpdateDto) {	
		return postRepository.findByUserLogin(userUpdateDto.getLogin()).stream()
				.map(p -> updateUser(userUpdateDto, p))
				.map(p -> postRepository.save(p))
				.collect(Collectors.toSet());
	}
	
	private Post updateUser(UserUpdateDto userUpdateDto, Post post) { 
		if (userUpdateDto.getUsername() != null) {
			post.setUsername(userUpdateDto.getUsername());
		}
		if (userUpdateDto.getAvatar() != null) {
			post.setAvatar(userUpdateDto.getAvatar());
		}
		return post;
	}
}
