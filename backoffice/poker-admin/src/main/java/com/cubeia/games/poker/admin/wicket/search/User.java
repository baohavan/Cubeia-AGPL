package com.cubeia.games.poker.admin.wicket.search;

import java.io.Serializable;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
class User implements Serializable {
    private static final long serialVersionUID = 1191176946462071998L;
    
    @SuppressWarnings("serial")
	@JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserInformation implements Serializable {
        private String firstName;
        
        private String lastName;
        
        private String email;

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		@Override
		public String toString() {
			return "UserInformation [firstName=" + firstName + ", lastName="
					+ lastName + ", email=" + email + "]";
		}
    }
    
    
    private long userId;
    private String externalUserId;
    private String userName;
    private long operatorId;
    private String status;
    private String userType;
    private UserInformation userInformation;
    
    @JsonDeserialize(keyAs = String.class, contentAs = Attribute.class)
    private Map<String, Attribute> attributes;
    
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getExternalUserId() {
		return externalUserId;
	}
	
	public void setExternalUserId(String externalUserId) {
		this.externalUserId = externalUserId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public long getOperatorId() {
		return operatorId;
	}
	
	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUserType() {
		return userType;
	}
	
	public void setUserType(String userType) {
		this.userType = userType;
	}
	
	public UserInformation getUserInformation() {
		return userInformation;
	}
	
	public void setUserInformation(UserInformation userInformation) {
		this.userInformation = userInformation;
	}
	
	public Map<String, Attribute> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, Attribute> attributes) {
		this.attributes = attributes;
	}
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", externalUserId=" + externalUserId
				+ ", userName=" + userName + ", operatorId=" + operatorId
				+ ", status=" + status + ", userType=" + userType
				+ ", userInformation=" + userInformation + "]";
	}

    
}