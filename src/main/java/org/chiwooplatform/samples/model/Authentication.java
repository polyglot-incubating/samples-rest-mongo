package org.chiwooplatform.samples.model;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.ToString;

/**
 * Created by seonbo.shim on 2017-07-06.
 */
@Data
@ToString
@JsonSerialize
@JsonRootName("authentication")
public class Authentication {

	private String id; /* UserPrincipal */

	private String token; /* token. It can be modified with new token*/

	private String expires; /* UTC Timestamp "2014-01-31T15:30:58Z", It can be modified with new token */

	private String sessionId;

	private User user;

}
