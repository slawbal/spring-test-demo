package com.sb.notes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class NotesControllerIT {

    RestTemplate restTemplate = new RestTemplate();

    @LocalServerPort
    private int port;

    @Test
    public void _1_createNote() {
        final ResponseEntity<String> response = restTemplate
                .postForEntity(absoluteUrl("notes"), "my note 2", String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    @Test
    public void _2_addFewNotesAndReadThem() {
        newNote("my note");
        newNote("something more");
        newNote("something less");

        ResponseEntity<List<TestNoteJson>> response = readNotes();

        assertThat(response.getBody()).contains(
                draftNote("my note"),
                draftNote("something more"),
                draftNote("something less")
        );
    }

    @Test
    public void deleteSingleNote() {
        newNote("someNote");
        final String noteId = newNote("someOther");

        restTemplate.delete(absoluteUrl("notes/" + noteId));

        assertThat(readNotes().getBody()).extracting("content").doesNotContain("someOther");
    }

    @Test
    public void updateNote() {
        final String noteId = newNote("someOtherSuperNote");

        restTemplate.put(absoluteUrl("notes/" + noteId), "someOtherSuperNoteWithNewValue");

        assertThat(readNotes().getBody()).extracting("content")
                .doesNotContain("someOtherSuperNote")
                .contains("someOtherSuperNoteWithNewValue");
    }

    @Test
    public void publishNote() {
        final String noteId = newNote("forPublish");

        restTemplate.put(absoluteUrl("notes/published/" + noteId), null);

        assertThat(readNotes().getBody()).contains(new TestNoteJson("forPublish", "PUBLISHED"));
    }

    private ResponseEntity<List<TestNoteJson>> readNotes() {
        return restTemplate.exchange(
                absoluteUrl("notes"),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TestNoteJson>>() {
                });
    }

    private String newNote(final String content) {
        final ResponseEntity<String> response = restTemplate
                .postForEntity(absoluteUrl("notes"), content, String.class);
        return response.getBody();
    }

    private String absoluteUrl(final String url) {
        return format("http://localhost:%s/" + url, port);
    }

    private TestNoteJson draftNote(final String content) {
        return new TestNoteJson(content, "DRAFT");
    }

    public static class TestNoteJson {
        public String content;
        public String status;

        private TestNoteJson() {
        }

        public TestNoteJson(final String content, final String status) {
            this.content = content;
            this.status = status;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final TestNoteJson that = (TestNoteJson) o;
            return Objects.equals(content, that.content) && Objects.equals(status, that.status);
        }

        @Override
        public int hashCode() {
            return Objects.hash(content, status);
        }

        @Override
        public String toString() {
            return "TestNoteJson{" + "content='" + content + '\'' + ", status='" + status + '\'' + '}';
        }
    }
}
