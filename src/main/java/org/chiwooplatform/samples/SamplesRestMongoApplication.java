package org.chiwooplatform.samples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories("org.chiwooplatform.samples.dao.mongo")
@SpringBootApplication
public class SamplesRestMongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SamplesRestMongoApplication.class, args);
	}
}
