package pl.lodz.p.edu.grs.controller.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.lodz.p.edu.grs.model.Category;
import pl.lodz.p.edu.grs.model.Game;
import pl.lodz.p.edu.grs.repository.CategoryRepository;
import pl.lodz.p.edu.grs.repository.GameRepository;
import pl.lodz.p.edu.grs.service.CategoryService;
import pl.lodz.p.edu.grs.service.GameService;
import pl.lodz.p.edu.grs.util.CategoryUtil;
import pl.lodz.p.edu.grs.util.GameUtil;

import java.io.IOException;
import java.util.HashMap;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class GamePOSTAddGameEndpointTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        gameRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    public void shouldReturnOkStatusWhenAddGame() throws Exception {
        //given
        GameDto gameDto = GameUtil.mockGameDto();

        Category category = categoryService.addCategory(CategoryUtil.mockCategoryDto());

        String content = objectMapper.writeValueAsString(gameDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(String.format("/api/games/%d/new/",category.getId()))
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(content);

        //when
        ResultActions result = mockMvc.perform(requestBuilder);

        //then
        String body = result.andReturn().getResponse().getContentAsString();
        long id = getIdFromContentBody(body);
        Game game = gameRepository.findOne(id);

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(game.getId()))
                .andExpect(jsonPath("$.title").exists())
                .andExpect(jsonPath("$.title").value(game.getTitle()))
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.description").value(game.getDescription()))
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.price").value(game.getPrice()))
                .andExpect(jsonPath("$.category.id").exists())
                .andExpect(jsonPath("$.category.id").value(game.getCategory().getId()))
                .andExpect(jsonPath("$.category.name").exists())
                .andExpect(jsonPath("$.category.name").value(game.getCategory().getName()))
                .andExpect(jsonPath("$.available").exists())
                .andExpect(jsonPath("$.available").value(game.isAvailable()));

    }

    private long getIdFromContentBody(final String content) throws IOException {
        TypeReference<HashMap<String, String>> typeRef
                = new TypeReference<HashMap<String, String>>() {
        };

        HashMap<String, Object> map = objectMapper.readValue(content, typeRef);
        return Long.valueOf((String) map.get("id"));
    }
}