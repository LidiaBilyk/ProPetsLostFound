package telran.ProPets.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import telran.ProPets.dao.PostRepository;
import telran.ProPets.model.Post;

@Component
public class CustomSecurity {
	
	@Autowired
	PostRepository postRepository;
	
	public boolean checkAuthorityForPost(String login, Principal principal) {
		return login.equals(principal.getName());
	}
	
	public boolean checkAuthorityForDeletePost(String id, Principal principal) {
		Post post = postRepository.findById(id).orElse(null);
		if (post == null) {
			return false;
		}
		return post.getUserLogin().equals(principal.getName());
	}
}
