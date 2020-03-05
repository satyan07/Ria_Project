package com.example.demo.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.example.demo.entity.AuthRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.example.demo.util.CustomAuthenticationManager;
import com.example.demo.util.JwtUtil;

@RestController
@RequestMapping("/ria")
public class UserContoller {

	@Autowired
	UserService userService;

	@Autowired
	RestTemplate restTemplate;

	@Value("${api.key}")
	private String apiKey;

	@Autowired
	JwtUtil jwtUtil;

	@Autowired
	private CustomAuthenticationManager authenticationManager;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;



	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public ResponseEntity<List<User>> listAllUsers() {
		List<User> users = userService.getUser();
		if(users.isEmpty()){
			return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);
	}



	@RequestMapping(value = "/sign-up", method = RequestMethod.POST)
	public ResponseEntity<Void> createUser(@RequestBody User user) {
		if (userService.isUserExist(user)) {
			return new ResponseEntity<Void>(HttpStatus.CONFLICT);
		}
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userService.saveUser(user);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

	@RequestMapping("/user/{id}")
	public ResponseEntity<Object> getWeather(@PathVariable("id") int id){

		User user = userService.getUser(id);
		if (user != null) {
			String city = user.getCity();
			Object weather = restTemplate.getForObject(
					"http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" +apiKey
					, Object.class);

			return new ResponseEntity<Object>( weather, HttpStatus.OK);

		}else {


			System.out.println("User with id " + id + " not found");
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}

	}


	@PostMapping("/authenticate")
	public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
		try {

			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authRequest.getUserName(),authRequest.getPassword()));

		}catch(Exception ex){
			throw new Exception("Invalid credentials");
		}
		return jwtUtil.generateToken(authRequest.getUserName());

	}





}