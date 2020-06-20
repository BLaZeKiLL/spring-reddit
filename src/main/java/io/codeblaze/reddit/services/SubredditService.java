package io.codeblaze.reddit.services;

import io.codeblaze.reddit.dto.SubredditDto;
import io.codeblaze.reddit.model.Subreddit;
import io.codeblaze.reddit.repository.SubredditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class SubredditService {

    private final SubredditRepository subredditRepository;

    @Transactional
    public SubredditDto save(SubredditDto subredditDto) {
        Subreddit subreddit = subredditRepository.save(mapSubredditDto(subredditDto));
        subredditDto.setId(subreddit.getId());
        return subredditDto;
    }

    @Transactional(readOnly = true)
    public List<SubredditDto> getAll() {
        return subredditRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    private Subreddit mapSubredditDto(SubredditDto subredditDto) {
        return Subreddit
            .builder()
            .name(subredditDto.getName())
            .description(subredditDto.getDescription())
            .build();
    }

    private SubredditDto mapToDto(Subreddit subreddit) {
        return  SubredditDto
            .builder()
            .name(subreddit.getName())
            .description(subreddit.getDescription())
            .build();
    }

}
