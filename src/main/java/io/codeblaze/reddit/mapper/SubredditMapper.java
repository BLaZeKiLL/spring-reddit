package io.codeblaze.reddit.mapper;

import io.codeblaze.reddit.dto.SubredditDto;
import io.codeblaze.reddit.model.Subreddit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubredditMapper {

    @Mapping(target = "numberOfPosts", expression = "java(subreddit.getPosts().size())")
    SubredditDto mapSubredditToDto(Subreddit subreddit);

}
