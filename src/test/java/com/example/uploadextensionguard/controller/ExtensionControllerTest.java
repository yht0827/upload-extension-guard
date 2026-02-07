package com.example.uploadextensionguard.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.uploadextensionguard.dto.ExtensionResponse;
import com.example.uploadextensionguard.dto.ExtensionResponse.CustomExtensionDto;
import com.example.uploadextensionguard.dto.ExtensionResponse.FixedExtensionDto;
import com.example.uploadextensionguard.exception.DuplicateExtensionException;
import com.example.uploadextensionguard.exception.ExtensionNotFoundException;
import com.example.uploadextensionguard.exception.MaxExtensionLimitException;
import com.example.uploadextensionguard.service.ExtensionService;

@WebMvcTest(ExtensionController.class)
@DisplayName("ExtensionController 테스트")
class ExtensionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ExtensionService extensionService;

	@Nested
	@DisplayName("GET /api/extensions")
	class GetAllExtensions {

		@Test
		@DisplayName("전체 확장자 조회 성공")
		void success() throws Exception {
			// given
			List<FixedExtensionDto> fixed = List.of(
				new FixedExtensionDto("bat", false),
				new FixedExtensionDto("exe", true)
			);
			List<CustomExtensionDto> custom = List.of(
				new CustomExtensionDto(1L, "pdf"),
				new CustomExtensionDto(2L, "doc")
			);
			given(extensionService.getAllExtensions())
				.willReturn(new ExtensionResponse(fixed, custom));

			// when & then
			mockMvc.perform(get("/api/extensions"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fixed").isArray())
				.andExpect(jsonPath("$.fixed[0].extension").value("bat"))
				.andExpect(jsonPath("$.fixed[1].blocked").value(true))
				.andExpect(jsonPath("$.custom").isArray())
				.andExpect(jsonPath("$.custom[0].extension").value("pdf"));
		}
	}

	@Nested
	@DisplayName("PATCH /api/extensions/fixed")
	class UpdateFixedExtension {

		@Test
		@DisplayName("고정 확장자 상태 변경 성공")
		void success() throws Exception {
			// given
			willDoNothing().given(extensionService).updateFixedExtension("exe", true);

			// when & then
			mockMvc.perform(patch("/api/extensions/fixed")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"extension\": \"exe\", \"blocked\": true}"))
				.andExpect(status().isOk());
		}

		@Test
		@DisplayName("존재하지 않는 확장자 - 404")
		void notFound() throws Exception {
			// given
			willThrow(new ExtensionNotFoundException("notexist"))
				.given(extensionService).updateFixedExtension(eq("notexist"), anyBoolean());

			// when & then
			mockMvc.perform(patch("/api/extensions/fixed")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"extension\": \"notexist\", \"blocked\": true}"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());
		}

		@Test
		@DisplayName("extension 누락 - 400")
		void missingExtension() throws Exception {
			mockMvc.perform(patch("/api/extensions/fixed")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"blocked\": true}"))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("blocked 누락 - 400")
		void missingBlocked() throws Exception {
			mockMvc.perform(patch("/api/extensions/fixed")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"extension\": \"exe\"}"))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("POST /api/extensions/custom")
	class AddCustomExtension {

		@Test
		@DisplayName("커스텀 확장자 추가 성공 - 201")
		void success() throws Exception {
			// given
			given(extensionService.addCustomExtension(any()))
				.willReturn(new CustomExtensionDto(1L, "pdf"));

			// when & then
			mockMvc.perform(post("/api/extensions/custom")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"extension\": \"pdf\"}"))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.extension").value("pdf"));
		}

		@Test
		@DisplayName("중복 확장자 - 409")
		void duplicate() throws Exception {
			// given
			given(extensionService.addCustomExtension(any()))
				.willThrow(new DuplicateExtensionException("pdf"));

			// when & then
			mockMvc.perform(post("/api/extensions/custom")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"extension\": \"pdf\"}"))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").exists());
		}

		@Test
		@DisplayName("최대 개수 초과 - 400")
		void maxLimit() throws Exception {
			// given
			given(extensionService.addCustomExtension(any()))
				.willThrow(new MaxExtensionLimitException());

			// when & then
			mockMvc.perform(post("/api/extensions/custom")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"extension\": \"pdf\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists());
		}

		@Test
		@DisplayName("extension 누락 - 400")
		void missingExtension() throws Exception {
			mockMvc.perform(post("/api/extensions/custom")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{}"))
				.andExpect(status().isBadRequest());
		}
	}

	@Nested
	@DisplayName("DELETE /api/extensions/custom/{id}")
	class DeleteCustomExtension {

		@Test
		@DisplayName("삭제 성공 - 204")
		void success() throws Exception {
			// given
			willDoNothing().given(extensionService).deleteCustomExtension(1L);

			// when & then
			mockMvc.perform(delete("/api/extensions/custom/1"))
				.andExpect(status().isNoContent());
		}

		@Test
		@DisplayName("존재하지 않는 확장자 - 404")
		void notFound() throws Exception {
			// given
			willThrow(new ExtensionNotFoundException(999L))
				.given(extensionService).deleteCustomExtension(999L);

			// when & then
			mockMvc.perform(delete("/api/extensions/custom/999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());
		}
	}
}
