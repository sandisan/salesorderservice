package com.demo.controller;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.domain.Item;
import com.demo.domain.Order;
import com.demo.repository.OrderRepository;
import com.demo.service.CustomerService;
import com.demo.service.ItemService;
import com.demo.service.NextSequenceService;

/**
 * @author Abesh
 *
 */
@RestController
@RequestMapping("/order")
public class SalesOrderController {

	private static final Logger LOG = Logger.getLogger(SalesOrderController.class);

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ItemService itemService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private NextSequenceService nextSequenceService;

	@PostMapping("/create")
	public ResponseEntity<String> createOrder(@RequestBody Order order) {
		LOG.info("Creating order");
		if (!customerService.isCustomerValid(order.getCustId())) {
			new ResponseEntity<String>("", HttpStatus.CREATED);
			return new ResponseEntity<String>("Customer doesn't exist", HttpStatus.BAD_REQUEST);
		}
		if (null == order.getItems() || order.getItems().isEmpty()) {
			return new ResponseEntity<String>("No items selected", HttpStatus.BAD_REQUEST);
		}
		int itemsInReq = order.getItems().size();
		Collection<Item> items = itemService
				.getItems(order.getItems().stream().map(Item::getId).collect(Collectors.toSet()));
		if (null == items || items.isEmpty()) {
			return new ResponseEntity<String>("No valid items selected", HttpStatus.BAD_REQUEST);
		}
		int validItems = items.size();
		Collection<Long> validItemsIds = items.stream().map(Item::getId).collect(Collectors.toSet());
		if (itemsInReq != validItems) {

			return new ResponseEntity<String>(
					"Items with the following IDs are not valid : "
							+ order.getItems().stream().filter(e -> !validItemsIds.contains(e.getId()))
									.map(f -> String.valueOf(f.getId())).collect(Collectors.joining(" , ")),
					HttpStatus.BAD_REQUEST);
		}
		order.setTotalPrice(items.stream().map(e -> Double.valueOf(e.getItemPrice()))
				.collect(Collectors.summingDouble(Double::floatValue)).floatValue());
		order.setItems(items);
		order.setOrderId(nextSequenceService.getNextSequence("customSequences"));
		return new ResponseEntity<String>(
				"Order created successfully : ID : " + orderRepository.save(order).getOrderId(), HttpStatus.CREATED);
	}

	@GetMapping("/all")
	public List<Order> getAllOrders() {
		LOG.info("Getting all orders");
		return orderRepository.findAll();
	}

	@GetMapping("/{id}")
	public Optional<Order> getOrderById(@PathVariable long id) {
		LOG.info("Get order by id");
		return orderRepository.findById(id);
	}

}
