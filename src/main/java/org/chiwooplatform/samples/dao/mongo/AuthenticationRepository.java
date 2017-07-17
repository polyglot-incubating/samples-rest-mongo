package org.chiwooplatform.samples.dao.mongo;

import java.util.List;

import org.chiwooplatform.samples.model.Authentication;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by seonbo.shim on 2017-07-06.
 * http://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#repository-query-keywords
 */
public interface AuthenticationRepository extends MongoRepository<Authentication, String> {

	Authentication findById(String id);

	Authentication findByToken(String token);

	List<Authentication> findByIdLike(String id);

}
