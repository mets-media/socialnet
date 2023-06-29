package socialnet.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import socialnet.api.response.CommonRs;
import socialnet.api.response.PersonRs;
import socialnet.api.response.PostRs;
import socialnet.mappers.PersonMapper;
import socialnet.model.Person;
import socialnet.model.Post;
import socialnet.model.SearchOptions;
import socialnet.repository.PersonRepository;
import socialnet.repository.PostRepository;
import socialnet.security.jwt.JwtUtils;
import socialnet.model.PostServiceDetails;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FindService {
    private final JwtUtils jwtUtils;
    private final PersonRepository personRepository;
    private final PostRepository postRepository;
    private final PostService postService;

    public CommonRs<List<PostRs>> getPostsByQuery(SearchOptions searchOptions) {
        List<PostRs> postRsList = new ArrayList<>();
        long postListAll;
        searchOptions.setFlagQueryAll(false);

        List<Post> postList = postRepository.findPostStringSql(searchOptions);

        searchOptions.setFlagQueryAll(true);
        postListAll = Integer.toUnsignedLong(postRepository.findPostStringSqlAll(searchOptions));

        postList.forEach(post -> {
            int postId = post.getId().intValue();
            PostServiceDetails details1 = postService.getDetails(post.getAuthorId(), postId, searchOptions.getJwtToken());
            PostRs postRs = PostService.setPostRs(post, details1);
            postRsList.add(postRs);
        });

        return new CommonRs<>(postRsList, postRsList.size(), searchOptions.getOffset(), searchOptions.getPerPage(),
                System.currentTimeMillis(), postListAll);
    }

    public CommonRs<List<PersonRs>> findPersons(SearchOptions searchOptions) {

        String email = jwtUtils.getUserEmail(searchOptions.getJwtToken());
        Person personsEmail = personRepository.findByEmail(email);
        long findPersonQueryAll;
        List<Person> personList;
        List<PersonRs> personRsList = new ArrayList<>();
        searchOptions.setFlagQueryAll(false);
        searchOptions.setId(Math.toIntExact(personsEmail.getId()));
        personList = personRepository.findPersonsQuery(searchOptions);

        searchOptions.setFlagQueryAll(true);
        findPersonQueryAll = Integer.toUnsignedLong(personRepository.findPersonsQueryAll(searchOptions));

        personList.forEach(person -> {
            PersonRs personRs = PersonMapper.INSTANCE.toDTO(person);
            personRsList.add(personRs);
        });

        personRsList.sort(Comparator.comparing(PersonRs::getRegDate).reversed());
        return new CommonRs<>(personRsList, personRsList.size(), searchOptions.getOffset(), searchOptions.getPerPage(),
                System.currentTimeMillis(), findPersonQueryAll);
    }

}
