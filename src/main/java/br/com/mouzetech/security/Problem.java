package br.com.mouzetech.security;
import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Problem {

	private Integer status;
	private String type;
	private String title;
	private String detail;
	private OffsetDateTime timeStamp;
	private String userMessage;	
}