package com.nulhart.services;

import com.nulhart.dto.GameDTO;
import com.nulhart.dto.SuggestionDTO;
import com.nulhart.openai.OpenAIClient;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAIService {
    private final OpenAIClient openAIClient;
    private final GameService gameService;
    public List<SuggestionDTO> getSuggestions() {
        List<GameDTO> allGames =gameService.getAllGames();
        String promptMessaage ="You are an expert in video games both current and new" +
                "Based on the following backlog JSON between triple quotes that represents the backlog of" +
                " games a person has played or is playing and their opinions about it:" +
                "\n\"\"\" \n"+ allGames.toString()+
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
}
