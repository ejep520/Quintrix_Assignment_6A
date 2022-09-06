package com.quintrix.jepsen.erik.sixthA.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.quintrix.jepsen.erik.sixthA.client.RestClient;
import com.quintrix.jepsen.erik.sixthA.model.Person;

@RestController
public class SixAController {
  private RestClient restClient = new RestClient();

  @GetMapping("/robert/find")
  public String robertFind() {
    Person[] replies = restClient.getReplies("/person/first/{id}", "Robert");
    String output = "";
    for (Person thisPerson : replies) {
      output += thisPerson.toString();
      output += System.lineSeparator();
    }
    return output;
  }

  @GetMapping("robert/delete/{id}")
  public String robertDelete(@PathVariable int id) {
    final String uri = "/person/{id}";
    Person maybeRobert = restClient.getReply(uri, id);
    if (maybeRobert.getfName().equals("Robert"))
      return restClient.deleteReply(uri, id);
    return "This isn't a Robert.";
  }

  @GetMapping("robert/add")
  public String robertAdd() {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("fName", "Robert");
    map.add("lName", "Johnson\", -1); DROP TABLE persons;");
    map.add("deptId", "3");

    return restClient.postReply("/person/new", map);
  }

  @ModelAttribute
  public void setCacheDeny(HttpServletResponse response) {
    response.setHeader("Cache-Control", "no-cache");
  }
}
