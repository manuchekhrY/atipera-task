package com.example.demo.api;

import com.example.demo.model.Error;
import com.example.demo.model.RepositoryInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/api")
public class Controller {

        @GetMapping("/repositories")
        public ResponseEntity<Object> getUserRepositories(@RequestParam("username") String username) {
            String apiUrl = "https://api.github.com/users/" + username + "/repos?fork=false";
            RestTemplate restTemplate = new RestTemplate();

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

                ResponseEntity<RepositoryInfo[]> responseEntity = restTemplate.getForEntity(apiUrl, RepositoryInfo[].class);

                RepositoryInfo[] repositories = responseEntity.getBody();

                    List<RepositoryInfo> repositoryInfos = new ArrayList<>();
                    if (repositories != null) {
                        for (RepositoryInfo repository : repositories) {
                            RepositoryInfo repositoryInfo = new RepositoryInfo();

                            repositoryInfo.setName(repository.getName());
                            repositoryInfo.setOwner(repository.getOwner());

                            repositoryInfos.add(repositoryInfo);
                        }
                    }

                    return ResponseEntity.ok(repositoryInfos);

            } catch (HttpClientErrorException ex) {
                if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                    Error errorMessage = new Error(HttpStatus.NOT_FOUND.value(), "Account with '" + username + "' username does not exist.");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                }
            }
            return null;
        }
}
