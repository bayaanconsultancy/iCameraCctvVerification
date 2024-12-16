package com.tcs.ion.icamera.cctv.model;

public class OnvifAuth {
	private String username;
	private String password;
	private String nonce;
	private String created;
	private String expires;

	private OnvifAuth(Builder builder) {
		setUsername(builder.username);
		setPassword(builder.password);
		setNonce(builder.nonce);
		setCreated(builder.created);
		setExpires(builder.expires);
	}

	public OnvifAuth(String username, String password, String nonce, String created, String expires) {
		this.username = username;
		this.password = password;
		this.nonce = nonce;
		this.created = created;
		this.expires = expires;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public static final class Builder {
		private String username;
		private String password;
		private String nonce;
		private String created;
		private String expires;

		public Builder() {
		}

		public Builder username(String val) {
			username = val;
			return this;
		}

		public Builder password(String val) {
			password = val;
			return this;
		}

		public Builder nonce(String val) {
			nonce = val;
			return this;
		}

		public Builder created(String val) {
			created = val;
			return this;
		}

		public OnvifAuth build() {
			return new OnvifAuth(this);
		}

		public Builder expires(String val) {
			expires = val;
			return this;
		}
	}
}
