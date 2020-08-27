package com.example.kafka;

import com.example.kafka.domain.Book;
import com.example.kafka.event.LibraryEvent;
import com.example.kafka.producer.LibraryEventProducer;
import com.example.kafka.rest.BookController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc
public class KafkaApplicationMockTests {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper mapper;

	@MockBean
	LibraryEventProducer producer;

	@Test
	public void postLibraryEvent() throws Exception {
		//given
		Book book = Book.builder().id(1).name("meu-livro").build();

		doNothing().when(producer).sendLibraryEvent(isA(LibraryEvent.class));

		//when and then
		ResultActions resultActions = mockMvc.perform(post("/v1/book")
				.content(mapper.writeValueAsString(book))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isAccepted());

	}

	@Test
	public void badRequest() throws Exception {
		//given
		Book book = Book.builder().id(1).name("").build();

		doNothing().when(producer).sendLibraryEvent(isA(LibraryEvent.class));

		//when and then
		ResultActions resultActions = mockMvc.perform(post("/v1/book")
				.content(mapper.writeValueAsString(book))
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("name - must not be blank"));

	}

}
