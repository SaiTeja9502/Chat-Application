package com.example.demo.dto;

import java.util.List;

import org.springframework.web.util.HtmlUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GroupCreateDTO {
	
	@NotBlank(message = "Group Name is required")
    @Size(max = 25, message = "Group Name cannot be more than 25 Characters")
	private String groupName;
	
	@NotNull(message = "Contact Ids list cannot be null")
    @Size(max = 20, message = "Contact Ids list cannot have more than 20 participants")
    @Valid
	private List<Long> contactIds;
	
	public GroupCreateDTO() {
		
	}

	public GroupCreateDTO(String groupName, List<Long> contactIds) {
		super();
		this.groupName = groupName;
		this.contactIds = contactIds;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = sanitize(groupName);
	}

	public List<Long> getContactIds() {
		return contactIds;
	}

	public void setContactIds(List<Long> contactIds) {
		this.contactIds = contactIds;
	}
	
	private String sanitize(String input) {
        // Perform HTML escaping to prevent XSS attacks
        return input != null ? HtmlUtils.htmlEscape(input.trim()) : null;
    }
}
