package io.codeblaze.reddit.services;

import io.codeblaze.reddit.dto.SubredditDto;
import io.codeblaze.reddit.model.Subreddit;
import io.codeblaze.reddit.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;

    public void save(SubredditDto subredditDto) {
        Subreddit subreddit = mapSubredditDto(subredditDto);
        subredditRepository.save(subreddit);
    }

    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
        return Subreddit
            .builder()
            .name(subredditDto.getName())
            .description(subredditDto.getDescription())
            .build();
    }

}
