package org.chiwooplatform.samples.model;

import java.util.Collection;

import lombok.Data;

/**
 * Created by seonbo.shim on 2017-07-06.
 */
@Data
public class User {

	private String id;

	private String username;

	private Collection<String> authorities;

}
