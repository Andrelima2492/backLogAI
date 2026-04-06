package com.nulhart.services;

import com.nulhart.dto.anime.AnimeTagsDTO;
import com.nulhart.dto.game.GameDTO;
import com.nulhart.dto.game.SuggestionDTO;
import com.nulhart.dto.manga.MangaTagsDTO;
import com.nulhart.model.Anime;
import com.nulhart.model.Manga;
import com.nulhart.openai.OpenAIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final OpenAIClient openAIClient;
    private final GameService gameService;
    public List<SuggestionDTO> getSuggestions() {
        List<GameDTO> allGames =gameService.getAllGames();
        ObjectMapper mapper = new ObjectMapper();
        String jsonGames = mapper.writeValueAsString(allGames);
        String promptMessaage ="You are an expert in video games both current and new" +
                "Based on the following backlog JSON between triple quotes that represents the backlog of" +
                " games a person has played or is playing and their opinions about it:" +
                "\n\"\"\" \n"+ jsonGames+
                "\n\"\"\"\n Please provide 3 new different games that you would recommend to someone who has the played or is playing depending on the status shown" +
                "above in the json. The last suggested game should be an indie." +
                "{format}";

        ParameterizedTypeReference<List<SuggestionDTO>> typeReference =  new ParameterizedTypeReference<List<SuggestionDTO>>(){};
        final BeanOutputConverter<List<SuggestionDTO>> beanOutputConverter = new BeanOutputConverter<>(typeReference);

         PromptTemplate promptTemplate = new PromptTemplate(promptMessaage);
       Prompt prompt = promptTemplate.create(Map.of("format", beanOutputConverter.getFormat()));
        String aiResponse = this.openAIClient.getChatClient().prompt(
                        prompt)
                .user(promptMessaage).call().content();

        return beanOutputConverter.convert(aiResponse);

    }

    public Set<String> getTags(Anime anime){
         String promptMessage = """
                            You are an expert in anime.
                            Based on the following anime, generate 3 to 6 descriptive tags.
                        
                            Anime:
                            Title: %s
                            Episodes: %s
                        
                            {format}
                            """.formatted(anime.getTitle(), anime.getNumberOfEpisodes());
            ParameterizedTypeReference<AnimeTagsDTO> typeRef = new ParameterizedTypeReference<>() {
            };
            BeanOutputConverter<AnimeTagsDTO> converter = new BeanOutputConverter<>(typeRef);
            PromptTemplate promptTemplate = new PromptTemplate(promptMessage);
            Prompt prompt = promptTemplate.create(Map.of("format", converter.getFormat()));
            String response = openAIClient.getChatClient().prompt(prompt).call().content();
            AnimeTagsDTO dto = converter.convert(response);
            return dto.tags();

    }

    public Set<String> getTags(Manga manga){
        String promptMessage = """
                            You are an expert in manga.
                            Based on the following manga, generate 3 to 6 descriptive tags.
                        
                            Manga:
                            Title: %s
                            Chapters: %s
                            Volumes: %s
                        
                            {format}
                            """.formatted(manga.getTitle(), manga.getNumberOfChapters(), manga.getNumberOfVolumes());
        ParameterizedTypeReference<MangaTagsDTO> typeRef = new ParameterizedTypeReference<MangaTagsDTO>() {
        };
        BeanOutputConverter<MangaTagsDTO> converter = new BeanOutputConverter<>(typeRef);
        PromptTemplate promptTemplate = new PromptTemplate(promptMessage);
        Prompt prompt = promptTemplate.create(Map.of("format", converter.getFormat()));
        String response = openAIClient.getChatClient().prompt(prompt).call().content();
        MangaTagsDTO dto = converter.convert(response);
        return dto.tags();
    }
}
