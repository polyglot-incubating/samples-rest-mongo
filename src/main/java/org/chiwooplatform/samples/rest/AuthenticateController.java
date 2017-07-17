package org.chiwooplatform.samples.rest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.chiwooplatform.samples.dao.mongo.AuthenticationRepository;
import org.chiwooplatform.samples.model.Authentication;
import org.chiwooplatform.samples.model.SimpleCredentials;
import org.chiwooplatform.samples.support.ConverterUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class AuthenticateController {

	private static final int EXPIRES_DAYS = 30;

	private static final DateTimeFormatter DEFAULT_TIMESTAMP_FORMAT = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final AuthenticationRepository repository;

	private final NamedParameterJdbcTemplate jdbcTemplate;

	public AuthenticateController(
			@Autowired NamedParameterJdbcTemplate jdbcTemplate,
			@Autowired AuthenticationRepository repository) {
		this.jdbcTemplate = jdbcTemplate;
		this.repository = repository;
	}

	private static LocalDateTime expiresDateTime() {
		final LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
		return now.plusDays(EXPIRES_DAYS).plusMinutes(1);
	}

	private static String uuid() {
		return UUID.randomUUID().toString();
	}

	@RequestMapping(value = "/auth/token", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Authentication> authenticate(@RequestBody SimpleCredentials creds, HttpSession session) {
		log.debug("{}", creds);
		HashMap<String, Object> param = new HashMap<>();
		param.put("username", creds.getUsername());
		param.put("password", creds.getPassword());
		String userName;
		try {
			userName = jdbcTemplate.queryForObject("SELECT u.id, u.username FROM USER u WHERE u.username = :username AND u.password = :password",
					param, (rs, num) -> rs.getString("username"));
		}
		catch (DataAccessException e) {
			userName = null;
		}
		log.info("userName: {}", userName);
		if (StringUtils.isEmpty(userName)) {
			log.warn("username or password is not matched!");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
		final String sessionId = session.getId();
		log.debug("sessionId: {}", sessionId);
		LocalDateTime expires = expiresDateTime();
		final String token = uuid();
		final String username = creds.getUsername();
		Authentication auth = new Authentication();
		auth.setId(username);
		auth.setSessionId(sessionId);
		auth.setExpires(expires.format(DEFAULT_TIMESTAMP_FORMAT));
		auth.setToken(token);
		if (repository.exists(username)) {
			log.debug("exists username: {}", username);
			log.debug("exists auth: {}", repository.findById(username));
		}
		else {
			log.debug("not exists username: {}", username);
			repository.save(auth);
			log.debug("save: {}", auth);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(auth);
	}

	@RequestMapping(value = "/auth/users/query", method = RequestMethod.GET, consumes = {
			MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.OK)
	public @JsonSerialize
	List<Authentication> query(@RequestParam Map<String, Object> params,
			@PageableDefault(sort = {"expires"}) Pageable pageable,
			HttpSession session) {
		final String sessionId = session.getId();
		log.debug("sessionId: {}", sessionId);
		Authentication auth = ConverterUtils.toBeanQuietly(params, Authentication.class);
		if (auth == null) {
			auth = new Authentication();
		}
		ExampleMatcher matcher = ExampleMatcher.matching()
				.withMatcher("id", GenericPropertyMatchers.startsWith())
				.withMatcher("token", GenericPropertyMatchers.contains());
		Example<Authentication> example = Example.of(auth, matcher);

		Page<Authentication> page = repository.findAll(example, pageable);
		return page.getContent();
	}


	@RequestMapping(value = "/auth/users/details", method = RequestMethod.GET, consumes = {
			MediaType.APPLICATION_JSON_VALUE})
	@ResponseStatus(HttpStatus.OK)
	public @JsonSerialize
	List<Authentication> users(@RequestParam Map<String, Object> params,
			HttpSession session) {
		final String sessionId = session.getId();
		log.debug("sessionId: {}", sessionId);
		try {
			Authentication auth = ConverterUtils.toBeanInstance(params, Authentication.class);
			ExampleMatcher matcher = ExampleMatcher.matching()
					.withMatcher("id", GenericPropertyMatchers.startsWith())
					.withMatcher("token", GenericPropertyMatchers.startsWith())
					// .withMatcher("token", StringMatcher.valueOf())
					// .withMatcher("content", ExampleMatcher.GenericPropertyMatchers.contains())
					;
			Example<Authentication> example = Example.of(auth, matcher);
			return repository.findAll(example).stream().limit(5).sorted(Comparator.comparing(Authentication::getId)).collect(
					Collectors.toList());
		}
		catch (Exception e) {
			log.error(e.getMessage(), e);
			return null;
		}
	}

}
