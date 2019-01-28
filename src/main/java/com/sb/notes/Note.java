package com.sb.notes;

public class Note {

	private final String id;
	private String content;
	private Status status = Status.DRAFT;

	public Note(final String id, final String content) {
		this.id = id;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public String getId() {
		return id;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public Status getStatus() {
		return status;
	}

	public void publish() {
		this.status = Status.PUBLISHED;
	}
}
