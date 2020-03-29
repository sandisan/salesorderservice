package com.demo.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.demo.domain.Item;

/**
 * @author Abesh
 *
 */
@Service
public class ItemService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	private final static String URL = "http://itemservice-cicd.169-61-227-230.nip.io/item/all";
	
	public Collection<Item> getItems(Collection<Long> ids) {
		
		ParameterizedTypeReference<Collection<Item>> typeRef = new ParameterizedTypeReference<Collection<Item>>() {
		};
		ResponseEntity<Collection<Item>> responseEntity = restTemplate.exchange(URL, HttpMethod.GET, null, typeRef);
		
		Collection<Item> allItems = responseEntity.getBody();
		Set<Item> selectedItems = allItems.stream().filter(e -> ids.contains(e.getId()) ).collect(Collectors.toSet());
		return selectedItems;
	}

}
