package com.sb.notes;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@RestController
public class NotesController {

	private List<Note> notes = new ArrayList<>();

	@PostMapping("/notes")
	public String addNote(@RequestBody final String content) {
		final String id = UUID.randomUUID().toString();
		notes.add(new Note(id, content));
		return id;
	}

	@GetMapping("/notes")
	public List<Note> getNotes() {
		return notes;
	}

	@DeleteMapping("/notes/{id}")
	public void deleteNote(@PathVariable String id){
		notes = notes.stream().filter(n -> !id.equals(n.getId())).collect(Collectors.toList());
	}

	@PutMapping("/notes/{id}")
	public void updateNote(@PathVariable final String id, @RequestBody final String content){
		notes.stream().filter(n -> id.equals(n.getId())).findFirst().ifPresent(n -> n.setContent(content));
	}

	@PutMapping("/notes/published/{id}")
	public void publishNote(@PathVariable final String id){
		notes.stream().filter(n -> id.equals(n.getId())).findFirst().ifPresent(n -> n.publish());
	}
}
